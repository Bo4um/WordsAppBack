package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.MemeExerciseRequest;
import com.bo4um.wordsappback.dto.MemeExerciseResponse;
import com.bo4um.wordsappback.dto.MemeResponse;
import com.bo4um.wordsappback.entity.LearningMeme;
import com.bo4um.wordsappback.repository.LearningMemeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class MemeService {

    private final LearningMemeRepository memeRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key:}")
    private String openAiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/responses}")
    private String openAiUrl;

    private final WebClient webClient;

    /**
     * Get trending memes for language learning
     */
    @Transactional(readOnly = true)
    public List<MemeResponse> getTrendingMemes(String language, Integer limit) {
        List<LearningMeme> memes = memeRepository.findTop10ByIsActiveOrderByCreatedAtDesc(true);

        if (language != null) {
            memes = memeRepository.findByLanguageAndIsActiveOrderByLikesDesc(language, true);
        }

        return memes.stream()
                .limit(limit != null ? limit : 10)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get memes by difficulty level
     */
    @Transactional(readOnly = true)
    public List<MemeResponse> getMemesByDifficulty(String language, String difficulty) {
        List<LearningMeme> memes = memeRepository.findByLanguageAndDifficultyAndIsActive(
                language, difficulty, true);

        return memes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Generate exercise from meme
     */
    @Transactional
    public MemeExerciseResponse generateExercise(MemeExerciseRequest request) {
        LearningMeme meme = memeRepository.findById(request.getMemeId())
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));

        // Update last used timestamp
        meme.setLastUsedAt(LocalDateTime.now());
        memeRepository.save(meme);

        // Generate exercise based on type
        return switch (request.getExerciseType()) {
            case "explain" -> generateExplainExercise(meme);
            case "complete" -> generateCompleteExercise(meme);
            case "translate" -> generateTranslateExercise(meme);
            default -> generateExplainExercise(meme);
        };
    }

    /**
     * Analyze meme with AI - get cultural context and vocabulary
     */
    @Transactional
    public MemeResponse analyzeMemeWithAI(Long memeId) {
        LearningMeme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));

        // Call OpenAI to analyze meme
        try {
            Map<String, Object> analysis = analyzeMemeContent(meme);

            meme.setCulturalContext((String) analysis.get("culturalContext"));
            meme.setVocabularyWords((String) analysis.get("vocabularyWords"));
            meme.setDifficulty((String) analysis.get("difficulty"));

            memeRepository.save(meme);

            log.info("Analyzed meme {} with AI", memeId);

        } catch (Exception e) {
            log.error("Failed to analyze meme: {}", e.getMessage());
        }

        return mapToResponse(meme);
    }

    /**
     * Like a meme
     */
    @Transactional
    public void likeMeme(Long memeId) {
        LearningMeme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));

        meme.setLikes(meme.getLikes() != null ? meme.getLikes() + 1 : 1);
        memeRepository.save(meme);
    }

    /**
     * Share a meme
     */
    @Transactional
    public void shareMeme(Long memeId) {
        LearningMeme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));

        meme.setShares(meme.getShares() != null ? meme.getShares() + 1 : 1);
        memeRepository.save(meme);
    }

    // ==================== Private Methods ====================

    private MemeExerciseResponse generateExplainExercise(LearningMeme meme) {
        String question = "Explain why this meme is funny in " + meme.getLanguage();
        String explanation = "This meme uses cultural references and wordplay typical of " +
                meme.getLanguage() + " humor. The joke relies on understanding " +
                (meme.getCulturalContext() != null ? meme.getCulturalContext() : "cultural context");

        List<String> vocabWords = parseVocabularyWords(meme.getVocabularyWords());

        return MemeExerciseResponse.builder()
                .memeId(meme.getId())
                .question(question)
                .correctAnswer(explanation)
                .explanation(explanation)
                .vocabularyWords(vocabWords)
                .culturalContext(meme.getCulturalContext())
                .isCorrect(true)
                .pointsEarned(10)
                .build();
    }

    private MemeExerciseResponse generateCompleteExercise(LearningMeme meme) {
        String question = "Complete the meme caption: '___'";
        String answer = meme.getTitle();

        return MemeExerciseResponse.builder()
                .memeId(meme.getId())
                .question(question)
                .correctAnswer(answer)
                .explanation("The correct caption captures the humor and cultural context of the meme")
                .vocabularyWords(parseVocabularyWords(meme.getVocabularyWords()))
                .culturalContext(meme.getCulturalContext())
                .isCorrect(null)
                .pointsEarned(15)
                .build();
    }

    private MemeExerciseResponse generateTranslateExercise(LearningMeme meme) {
        String question = "Translate this meme to your native language";
        String answer = meme.getDescription() != null ? meme.getDescription() : meme.getTitle();

        return MemeExerciseResponse.builder()
                .memeId(meme.getId())
                .question(question)
                .correctAnswer(answer)
                .explanation("Translation should preserve both meaning and humor")
                .vocabularyWords(parseVocabularyWords(meme.getVocabularyWords()))
                .culturalContext(meme.getCulturalContext())
                .isCorrect(null)
                .pointsEarned(20)
                .build();
    }

    private Map<String, Object> analyzeMemeContent(LearningMeme meme) {
        // In production, this would call OpenAI Vision API or similar
        // For now, return simulated analysis

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("culturalContext", "This meme format is popular in " + meme.getLanguage() +
                " speaking countries, often used to express relatable situations");
        analysis.put("vocabularyWords", meme.getVocabularyWords() != null ?
                meme.getVocabularyWords() : "slang, colloquial, expression");
        analysis.put("difficulty", meme.getDifficulty() != null ? meme.getDifficulty() : "B1");

        return analysis;
    }

    private List<String> parseVocabularyWords(String vocabularyWords) {
        if (vocabularyWords == null || vocabularyWords.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(vocabularyWords.split(","));
    }

    private MemeResponse mapToResponse(LearningMeme meme) {
        return MemeResponse.builder()
                .id(meme.getId())
                .imageUrl(meme.getImageUrl())
                .title(meme.getTitle())
                .description(meme.getDescription())
                .memeType(meme.getMemeType())
                .language(meme.getLanguage())
                .difficulty(meme.getDifficulty())
                .culturalContext(meme.getCulturalContext())
                .vocabularyWords(parseVocabularyWords(meme.getVocabularyWords()))
                .likes(meme.getLikes())
                .shares(meme.getShares())
                .isActive(meme.getIsActive())
                .createdAt(meme.getCreatedAt())
                .build();
    }
}
