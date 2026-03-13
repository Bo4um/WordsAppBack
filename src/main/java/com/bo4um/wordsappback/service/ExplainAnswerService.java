package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.ExplainAnswerRequest;
import com.bo4um.wordsappback.dto.ExplainAnswerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExplainAnswerService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.url:https://api.openai.com/v1/responses}")
    private String openAiUrl;

    @Value("${openai.api.key:}")
    private String openAiKey;

    /**
     * Объяснение почему ответ пользователя неверный
     */
    @Cacheable(value = "wordCache", key = "'explain:' + #request.userAnswer + ':' + #request.correctAnswer + ':' + #request.language")
    public ExplainAnswerResponse explainAnswer(ExplainAnswerRequest request) {
        log.info("Explaining answer: user='{}', correct='{}', language={}",
                request.getUserAnswer(), request.getCorrectAnswer(), request.getLanguage());

        try {
            Map<String, Object> apiRequest = buildExplainRequest(request);
            String response = callOpenAI(apiRequest);
            return parseExplainResponse(response, request);
        } catch (WebClientResponseException e) {
            log.error("OpenAI API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI API error: " + e.getStatusCode(), e);
        } catch (JsonProcessingException e) {
            log.error("Failed to process JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to process JSON", e);
        }
    }

    private Map<String, Object> buildExplainRequest(ExplainAnswerRequest request) {
        String systemPrompt = buildSystemPrompt(request);

        String userPrompt = String.format(
                "User's answer: \"%s\"\nCorrect answer: \"%s\"\nContext: %s",
                request.getUserAnswer(),
                request.getCorrectAnswer(),
                request.getContext() != null ? request.getContext() : "N/A"
        );

        return Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "max_tokens", 500,
                "temperature", 0.3,
                "response_format", Map.of("type", "json_object")
        );
    }

    private String buildSystemPrompt(ExplainAnswerRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert language tutor. Analyze the user's answer and explain why it's correct or incorrect.\n\n");
        prompt.append("Provide your response in JSON format with the following structure:\n");
        prompt.append("{\n");
        prompt.append("  \"explanation\": \"Clear explanation of the mistake or confirmation of correctness\",\n");
        prompt.append("  \"grammarRule\": \"Relevant grammar rule if applicable\",\n");
        prompt.append("  \"grammarTips\": [\"tip1\", \"tip2\"],\n");
        prompt.append("  \"correctedSentence\": \"Corrected version of user's sentence\",\n");
        prompt.append("  \"examples\": [\"example1\", \"example2\"],\n");
        prompt.append("  \"isCorrect\": true/false,\n");
        prompt.append("  \"confidence\": \"HIGH/MEDIUM/LOW\"\n");
        prompt.append("}\n\n");

        if (request.getLanguage() != null) {
            prompt.append("Target language: ").append(request.getLanguage()).append("\n");
        }

        if (request.getQuestionType() != null) {
            prompt.append("Question type: ").append(request.getQuestionType()).append("\n");
        }

        prompt.append("\nBe encouraging and supportive. Focus on learning, not just correction.");

        return prompt.toString();
    }

    private String callOpenAI(Map<String, Object> request) throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(request);
        log.debug("OpenAI Explain Request: {}", jsonRequest);

        Map<String, Object> response = webClient.post()
                .uri(openAiUrl)
                .header("Authorization", "Bearer " + openAiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("output")) {
            throw new RuntimeException("Empty response from OpenAI API");
        }

        return extractContentFromResponse(response);
    }

    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> output = (List<Map<String, Object>>) response.get("output");
            if (output != null && !output.isEmpty()) {
                Map<String, Object> content = output.get(0);
                List<Map<String, Object>> contentList = (List<Map<String, Object>>) content.get("content");
                if (contentList != null && !contentList.isEmpty()) {
                    return (String) contentList.get(0).get("text");
                }
            }
            throw new RuntimeException("Invalid response structure from OpenAI");
        } catch (Exception e) {
            log.error("Failed to extract content: {}", e.getMessage());
            throw new RuntimeException("Failed to extract content", e);
        }
    }

    private ExplainAnswerResponse parseExplainResponse(String jsonResponse, ExplainAnswerRequest request) {
        try {
            log.debug("OpenAI Explain Response: {}", jsonResponse);

            // OpenAI может вернуть JSON внутри текста, нужно извлечь
            String jsonContent = extractJsonFromText(jsonResponse);

            Map<String, Object> parsed = objectMapper.readValue(jsonContent, Map.class);

            return ExplainAnswerResponse.builder()
                    .explanation(getString(parsed, "explanation"))
                    .grammarRule(getString(parsed, "grammarRule"))
                    .grammarTips(getStringList(parsed, "grammarTips"))
                    .correctedSentence(getString(parsed, "correctedSentence"))
                    .examples(getStringList(parsed, "examples"))
                    .isCorrect(getBoolean(parsed, "isCorrect"))
                    .confidence(getString(parsed, "confidence"))
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse explanation response: {}", e.getMessage());
            // Возвращаем fallback ответ
            return createFallbackResponse(request);
        }
    }

    private String extractJsonFromText(String text) {
        // Если ответ содержит JSON внутри markdown или текста, извлекаем
        int startIdx = text.indexOf("{");
        int endIdx = text.lastIndexOf("}");

        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            return text.substring(startIdx, endIdx + 1);
        }
        return text.trim();
    }

    private ExplainAnswerResponse createFallbackResponse(ExplainAnswerRequest request) {
        boolean isCorrect = request.getUserAnswer().trim().equalsIgnoreCase(request.getCorrectAnswer().trim());

        return ExplainAnswerResponse.builder()
                .explanation(isCorrect ?
                        "Correct! Well done!" :
                        "The correct answer is: " + request.getCorrectAnswer())
                .grammarRule(null)
                .grammarTips(List.of("Review the lesson material", "Practice similar exercises"))
                .correctedSentence(request.getCorrectAnswer())
                .examples(List.of())
                .isCorrect(isCorrect)
                .confidence("MEDIUM")
                .build();
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return List.of();
    }
}
