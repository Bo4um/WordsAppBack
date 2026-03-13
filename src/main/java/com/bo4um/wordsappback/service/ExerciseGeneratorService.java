package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.ExerciseResponse;
import com.bo4um.wordsappback.dto.GenerateExerciseRequest;
import com.bo4um.wordsappback.dto.SubmitAnswerRequest;
import com.bo4um.wordsappback.dto.SubmitAnswerResponse;
import com.bo4um.wordsappback.entity.Exercise;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.repository.ExerciseRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseGeneratorService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.url:https://api.openai.com/v1/responses}")
    private String openAiUrl;

    @Value("${openai.api.key:}")
    private String openAiKey;

    /**
     * Генерация упражнений через AI
     */
    @Transactional
    public List<ExerciseResponse> generateExercises(Long userId, GenerateExerciseRequest request) {
        log.info("Generating exercises: language={}, difficulty={}, type={}, count={}",
                request.getLanguage(), request.getDifficulty(), request.getExerciseType(), request.getCount());

        try {
            // Запрос к OpenAI для генерации упражнений
            List<Exercise> exercises = callOpenAIForExercises(request);

            // Сохраняем упражнения (если пользователь указан)
            if (userId != null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                for (Exercise exercise : exercises) {
                    exercise.setUser(user);
                    exercise.setCreatedAt(LocalDateTime.now());
                    exercise.setIsCompleted(false);
                }

                exercises = exerciseRepository.saveAll(exercises);
            }

            return exercises.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (WebClientResponseException e) {
            log.error("OpenAI API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI API error: " + e.getStatusCode(), e);
        } catch (JsonProcessingException e) {
            log.error("Failed to process JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to process JSON", e);
        }
    }

    /**
     * Проверка ответа пользователя
     */
    @Transactional
    public SubmitAnswerResponse submitAnswer(Long userId, SubmitAnswerRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        boolean isCorrect = checkAnswer(exercise, request.getAnswer());

        // Обновляем упражнение
        if (isCorrect) {
            exercise.setIsCompleted(true);
            exercise.setCompletedAt(LocalDateTime.now());
            exerciseRepository.save(exercise);
        }

        return SubmitAnswerResponse.builder()
                .exerciseId(exercise.getId())
                .isCorrect(isCorrect)
                .userAnswer(request.getAnswer())
                .correctAnswer(exercise.getCorrectAnswer())
                .explanation(exercise.getExplanation())
                .points(isCorrect ? 10 : 0)
                .build();
    }

    /**
     * Получение упражнений пользователя
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> getUserExercises(Long userId, Boolean completed) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Exercise> exercises;
        if (completed != null) {
            exercises = exerciseRepository.findByUserAndIsCompleted(user, completed);
        } else {
            exercises = exerciseRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return exercises.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получение упражнения по ID
     */
    @Transactional(readOnly = true)
    public ExerciseResponse getExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        return mapToResponse(exercise);
    }

    /**
     * Удаление упражнения
     */
    @Transactional
    public void deleteExercise(Long exerciseId) {
        exerciseRepository.deleteById(exerciseId);
    }

    private List<Exercise> callOpenAIForExercises(GenerateExerciseRequest request)
            throws JsonProcessingException {

        Map<String, Object> apiRequest = buildExerciseGenerationRequest(request);
        String response = callOpenAI(apiRequest);

        // Парсим ответ - ожидаем массив упражнений
        return parseExercisesFromResponse(response, request);
    }

    private Map<String, Object> buildExerciseGenerationRequest(GenerateExerciseRequest request) {
        String systemPrompt = buildExerciseGenerationPrompt(request);

        String userPrompt = String.format(
                "Generate %d %s exercises for %s level (%s) students learning %s.%s",
                request.getCount() != null ? request.getCount() : 5,
                request.getExerciseType() != null ? request.getExerciseType() : "mixed",
                request.getDifficulty() != null ? request.getDifficulty() : "intermediate",
                getCEFRDescription(request.getDifficulty()),
                request.getLanguage() != null ? request.getLanguage() : "English",
                request.getTopic() != null ? " Topic: " + request.getTopic() : ""
        );

        return Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "max_tokens", 1500,
                "temperature", 0.7,
                "response_format", Map.of("type", "json_object")
        );
    }

    private String buildExerciseGenerationPrompt(GenerateExerciseRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert language teacher. Generate language learning exercises.\n\n");

        prompt.append("Return exercises in JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"exercises\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"type\": \"FILL_IN_BLANK|TRANSLATION|SENTENCE_BUILDING|VOCABULARY_QUIZ|GRAMMAR_CHOICE\",\n");
        prompt.append("      \"question\": \"The exercise question\",\n");
        prompt.append("      \"correctAnswer\": \"The correct answer\",\n");
        prompt.append("      \"hint\": \"Optional hint for students\",\n");
        prompt.append("      \"explanation\": \"Explanation of why this answer is correct\",\n");
        prompt.append("      \"options\": [\"option1\", \"option2\"] // for multiple choice\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");

        prompt.append("Guidelines:\n");
        prompt.append("- Make questions clear and unambiguous\n");
        prompt.append("- Use vocabulary appropriate for the specified level\n");
        prompt.append("- Include helpful explanations\n");
        prompt.append("- For multiple choice, provide 3-4 options\n");
        prompt.append("- Make exercises engaging and practical\n");

        return prompt.toString();
    }

    private String getCEFRDescription(String level) {
        if (level == null) return "intermediate";

        return switch (level.toUpperCase()) {
            case "A1" -> "beginner";
            case "A2" -> "elementary";
            case "B1" -> "intermediate";
            case "B2" -> "upper-intermediate";
            case "C1" -> "advanced";
            case "C2" -> "proficiency";
            default -> "intermediate";
        };
    }

    private String callOpenAI(Map<String, Object> request) throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(request);
        log.debug("OpenAI Exercise Generation Request: {}", jsonRequest);

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

    @SuppressWarnings("unchecked")
    private List<Exercise> parseExercisesFromResponse(String jsonResponse, GenerateExerciseRequest request)
            throws JsonProcessingException {

        log.debug("OpenAI Exercise Response: {}", jsonResponse);

        // Извлекаем JSON из текста
        String jsonContent = extractJsonFromText(jsonResponse);

        Map<String, Object> parsed = objectMapper.readValue(jsonContent, Map.class);
        List<Map<String, Object>> exercisesData = (List<Map<String, Object>>) parsed.get("exercises");

        if (exercisesData == null || exercisesData.isEmpty()) {
            throw new RuntimeException("No exercises generated");
        }

        List<Exercise> exercises = new ArrayList<>();
        for (Map<String, Object> exerciseData : exercisesData) {
            Exercise exercise = Exercise.builder()
                    .type(parseExerciseType((String) exerciseData.get("type")))
                    .question((String) exerciseData.get("question"))
                    .correctAnswer((String) exerciseData.get("correctAnswer"))
                    .hint((String) exerciseData.get("hint"))
                    .explanation((String) exerciseData.get("explanation"))
                    .language(request.getLanguage())
                    .difficulty(request.getDifficulty())
                    .build();

            // Добавляем опции для multiple choice
            if (exerciseData.get("options") instanceof List) {
                // Опции можно сохранить в отдельном поле или в JSON формате
                // Для простоты пока пропускаем
            }

            exercises.add(exercise);
        }

        return exercises;
    }

    private String extractJsonFromText(String text) {
        int startIdx = text.indexOf("{");
        int endIdx = text.lastIndexOf("}");

        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            return text.substring(startIdx, endIdx + 1);
        }
        return text.trim();
    }

    private Exercise.ExerciseType parseExerciseType(String type) {
        if (type == null) return Exercise.ExerciseType.FILL_IN_BLANK;

        try {
            return Exercise.ExerciseType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Exercise.ExerciseType.FILL_IN_BLANK;
        }
    }

    private boolean checkAnswer(Exercise exercise, String userAnswer) {
        if (exercise.getCorrectAnswer() == null) return false;

        // Для разных типов упражнений разная логика проверки
        return switch (exercise.getType()) {
            case VOCABULARY_QUIZ, GRAMMAR_CHOICE ->
                    exercise.getCorrectAnswer().equalsIgnoreCase(userAnswer);
            case FILL_IN_BLANK, TRANSLATION, SENTENCE_BUILDING ->
                    normalizeText(exercise.getCorrectAnswer()).equals(normalizeText(userAnswer));
            default -> exercise.getCorrectAnswer().equalsIgnoreCase(userAnswer);
        };
    }

    private String normalizeText(String text) {
        if (text == null) return "";
        return text.trim().toLowerCase().replaceAll("[^a-zа-яё0-9\\s]", "");
    }

    private ExerciseResponse mapToResponse(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .type(exercise.getType().name())
                .question(exercise.getQuestion())
                .hint(exercise.getHint())
                .explanation(exercise.getExplanation())
                .language(exercise.getLanguage())
                .difficulty(exercise.getDifficulty())
                .isCompleted(exercise.getIsCompleted())
                .createdAt(exercise.getCreatedAt())
                .build();
    }
}
