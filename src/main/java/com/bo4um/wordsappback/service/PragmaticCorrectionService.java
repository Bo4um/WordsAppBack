package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.PragmaticCorrectionResponse;
import com.bo4um.wordsappback.entity.PragmaticError;
import com.bo4um.wordsappback.repository.PragmaticErrorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PragmaticCorrectionService {

    private final PragmaticErrorRepository errorRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key:}")
    private String openAiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/responses}")
    private String openAiUrl;

    private final WebClient webClient;

    /**
     * Analyze text for pragmatic errors (tone, formality, cultural appropriateness)
     */
    @Transactional
    public PragmaticCorrectionResponse analyzePragmatics(String text, String context, String targetLanguage) {
        log.info("Analyzing pragmatics for text: {} in context: {}", text, context);

        // Call AI to analyze pragmatics
        PragmaticCorrectionResponse analysis = analyzeWithAI(text, context, targetLanguage);

        // Save error if found
        if (!analysis.getIsFormalAppropriate() || !analysis.getIsToneAppropriate()) {
            savePragmaticError(text, analysis);
        }

        return analysis;
    }

    /**
     * Get recent pragmatic errors for learning
     */
    @Transactional(readOnly = true)
    public List<PragmaticCorrectionResponse> getRecentErrors(Integer limit) {
        List<PragmaticError> errors = errorRepository.findTop10ByOrderByCreatedAtDesc();
        return errors.stream()
                .limit(limit != null ? limit : 10)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get errors by type (grammar, tone, formality, cultural)
     */
    @Transactional(readOnly = true)
    public List<PragmaticCorrectionResponse> getErrorsByType(String errorType) {
        List<PragmaticError> errors = errorRepository.findByErrorTypeOrderByCreatedAtDesc(errorType);
        return errors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mark error as helpful (for AI training)
     */
    @Transactional
    public void markErrorAsHelpful(Long errorId) {
        PragmaticError error = errorRepository.findById(errorId)
                .orElseThrow(() -> new IllegalArgumentException("Error not found"));

        error.setIsHelpful(true);
        error.setReviewedAt(LocalDateTime.now());
        errorRepository.save(error);
    }

    // ==================== Private Methods ====================

    private PragmaticCorrectionResponse analyzeWithAI(String text, String context, String targetLanguage) {
        // In production, this would call OpenAI API
        // For now, return simulated analysis based on common patterns

        PragmaticCorrectionResponse response = new PragmaticCorrectionResponse();
        response.setOriginalText(text);
        response.setTimestamp(LocalDateTime.now());

        // Simulate AI analysis
        Map<String, Object> analysis = simulatePragmaticAnalysis(text, context);

        response.setCorrectedText((String) analysis.get("corrected"));
        response.setErrorType((String) analysis.get("errorType"));
        response.setExplanation((String) analysis.get("explanation"));
        response.setSuggestedAlternatives((List<String>) analysis.get("alternatives"));
        response.setSeverityLevel((Integer) analysis.get("severity"));
        response.setIsFormalAppropriate((Boolean) analysis.get("formalAppropriate"));
        response.setIsToneAppropriate((Boolean) analysis.get("toneAppropriate"));
        response.setCulturalNote((String) analysis.get("culturalNote"));

        return response;
    }

    private Map<String, Object> simulatePragmaticAnalysis(String text, String context) {
        Map<String, Object> analysis = new HashMap<>();

        // Simple rule-based analysis for demo
        // In production, this would be AI-powered

        boolean isFormal = text.contains("please") || text.contains("could you") || text.contains("would you");
        boolean isInformal = text.contains("hey") || text.contains("wanna") || text.contains("gonna");
        boolean isRude = text.contains("shut up") || text.contains("whatever") || text.endsWith("!");

        String corrected = text;
        String errorType = "none";
        String explanation = "Your text is appropriate for the context.";
        Integer severity = 1;

        if (isRude && ("business".equals(context) || "formal".equals(context))) {
            corrected = "Could you please " + text.toLowerCase().replaceAll("[!]", ".");
            errorType = "tone";
            explanation = "This phrase may sound too direct/rude in a formal context. Consider using a softer tone.";
            severity = 4;
        } else if (isInformal && "business".equals(context)) {
            corrected = text.replace("wanna", "want to").replace("gonna", "going to");
            errorType = "formality";
            explanation = "Informal contractions are not appropriate in business contexts.";
            severity = 3;
        }

        analysis.put("corrected", corrected);
        analysis.put("errorType", errorType);
        analysis.put("explanation", explanation);
        analysis.put("alternatives", Arrays.asList(
                "Could you please...",
                "I would appreciate if you...",
                "Would you mind..."
        ));
        analysis.put("severity", severity);
        analysis.put("formalAppropriate", !isInformal || !"business".equals(context));
        analysis.put("toneAppropriate", !isRude);
        analysis.put("culturalNote", "In English-speaking business contexts, indirect requests are often more polite.");

        return analysis;
    }

    private void savePragmaticError(String text, PragmaticCorrectionResponse analysis) {
        PragmaticError error = PragmaticError.builder()
                .userUtterance(text)
                .correctedVersion(analysis.getCorrectedText())
                .errorType(analysis.getErrorType())
                .explanation(analysis.getExplanation())
                .context("conversation")
                .suggestedAlternatives(String.join(", ", analysis.getSuggestedAlternatives()))
                .severityLevel(analysis.getSeverityLevel())
                .createdAt(LocalDateTime.now())
                .build();

        errorRepository.save(error);
        log.info("Saved pragmatic error: {}", error.getErrorType());
    }

    private PragmaticCorrectionResponse mapToResponse(PragmaticError error) {
        return PragmaticCorrectionResponse.builder()
                .originalText(error.getUserUtterance())
                .correctedText(error.getCorrectedVersion())
                .errorType(error.getErrorType())
                .explanation(error.getExplanation())
                .suggestedAlternatives(error.getSuggestedAlternatives() != null ?
                        Arrays.asList(error.getSuggestedAlternatives().split(",")) : new ArrayList<>())
                .severityLevel(error.getSeverityLevel())
                .isFormalAppropriate(error.getErrorType() == null || !error.getErrorType().equals("formality"))
                .isToneAppropriate(error.getErrorType() == null || !error.getErrorType().equals("tone"))
                .timestamp(error.getCreatedAt())
                .build();
    }
}
