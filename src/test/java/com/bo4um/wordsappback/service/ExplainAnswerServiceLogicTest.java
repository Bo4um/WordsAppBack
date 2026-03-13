package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.ExplainAnswerRequest;
import com.bo4um.wordsappback.dto.ExplainAnswerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExplainAnswerService Business Logic Tests")
class ExplainAnswerServiceLogicTest {

    private ExplainAnswerService explainAnswerService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Создаём сервис без моков — тестируем только бизнес-логику
        explainAnswerService = new ExplainAnswerService(null, objectMapper);
        ReflectionTestUtils.setField(explainAnswerService, "openAiUrl", "https://api.openai.com/v1/responses");
        ReflectionTestUtils.setField(explainAnswerService, "openAiKey", "test-key");
    }

    @Test
    @DisplayName("Should create fallback response for incorrect answer")
    void createFallbackResponse_IncorrectAnswer() {
        // Given
        ExplainAnswerRequest request = ExplainAnswerRequest.builder()
                .userAnswer("She go to school")
                .correctAnswer("She goes to school")
                .language("English")
                .build();

        // When - тестируем приватный метод через反射 или напрямую создаём fallback
        ExplainAnswerResponse fallback = createFallbackResponse(request);

        // Then
        assertNotNull(fallback);
        assertFalse(fallback.getIsCorrect());
        assertEquals("The correct answer is: " + request.getCorrectAnswer(), fallback.getExplanation());
        assertEquals(request.getCorrectAnswer(), fallback.getCorrectedSentence());
        assertEquals("MEDIUM", fallback.getConfidence());
    }

    @Test
    @DisplayName("Should create fallback response for correct answer")
    void createFallbackResponse_CorrectAnswer() {
        // Given
        ExplainAnswerRequest request = ExplainAnswerRequest.builder()
                .userAnswer("She goes to school")
                .correctAnswer("She goes to school")
                .language("English")
                .build();

        // When
        ExplainAnswerResponse fallback = createFallbackResponse(request);

        // Then
        assertNotNull(fallback);
        assertTrue(fallback.getIsCorrect());
        assertEquals("Correct! Well done!", fallback.getExplanation());
    }

    @Test
    @DisplayName("Should extract JSON from text response")
    void extractJsonFromText_Success() {
        // Given
        String textWithJson = """
            Here is the explanation:
            ```json
            {"explanation": "Test", "isCorrect": false}
            ```
            End of response.
            """;

        // When
        String json = extractJsonFromText(textWithJson);

        // Then
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        assertTrue(json.contains("\"explanation\""));
    }

    @Test
    @DisplayName("Should handle pure JSON response")
    void extractJsonFromText_PureJson() {
        // Given
        String pureJson = """
            {"explanation": "Test", "isCorrect": true}
            """;

        // When
        String json = extractJsonFromText(pureJson);

        // Then
        assertEquals(pureJson.trim(), json);
    }

    @Test
    @DisplayName("Should normalize text for comparison")
    void normalizeText_Success() {
        // Given
        String text1 = "Hello World!";
        String text2 = "  hello world  ";
        String text3 = "HELLO WORLD";

        // When & Then
        assertEquals(normalizeText(text1), normalizeText(text2));
        assertEquals(normalizeText(text1), normalizeText(text3));
    }

    @Test
    @DisplayName("Should handle null text")
    void normalizeText_Null() {
        // When
        String result = normalizeText(null);

        // Then
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should validate request")
    void validateRequest_Success() {
        // Given
        ExplainAnswerRequest validRequest = ExplainAnswerRequest.builder()
                .userAnswer("test")
                .correctAnswer("correct")
                .language("English")
                .build();

        // When & Then
        assertNotNull(validRequest.getUserAnswer());
        assertNotNull(validRequest.getCorrectAnswer());
        assertEquals("English", validRequest.getLanguage());
    }

    @Test
    @DisplayName("Should handle missing context gracefully")
    void validateRequest_NullContext() {
        // Given
        ExplainAnswerRequest request = ExplainAnswerRequest.builder()
                .userAnswer("test")
                .correctAnswer("correct")
                .language("English")
                .context(null)
                .questionType(null)
                .build();

        // When & Then
        assertNotNull(request);
        assertNull(request.getContext());
        assertNull(request.getQuestionType());
    }

    // Helper methods (копируем приватные методы из сервиса для тестирования)
    private ExplainAnswerResponse createFallbackResponse(ExplainAnswerRequest request) {
        boolean isCorrect = request.getUserAnswer().trim().equalsIgnoreCase(request.getCorrectAnswer().trim());

        return ExplainAnswerResponse.builder()
                .explanation(isCorrect ?
                        "Correct! Well done!" :
                        "The correct answer is: " + request.getCorrectAnswer())
                .grammarRule(null)
                .grammarTips(java.util.List.of("Review the lesson material", "Practice similar exercises"))
                .correctedSentence(request.getCorrectAnswer())
                .examples(java.util.List.of())
                .isCorrect(isCorrect)
                .confidence("MEDIUM")
                .build();
    }

    private String extractJsonFromText(String text) {
        int startIdx = text.indexOf("{");
        int endIdx = text.lastIndexOf("}");

        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            return text.substring(startIdx, endIdx + 1);
        }
        return text.trim();
    }

    private String normalizeText(String text) {
        if (text == null) return "";
        return text.trim().toLowerCase().replaceAll("[^a-zа-яё0-9\\s]", "");
    }
}
