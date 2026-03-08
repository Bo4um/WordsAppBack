package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.LanguageTestResponse;
import com.bo4um.wordsappback.dto.TestQuestionResponse;
import com.bo4um.wordsappback.dto.TestSubmitRequest;
import com.bo4um.wordsappback.dto.TestWithQuestionsResponse;
import com.bo4um.wordsappback.dto.UserTestResultResponse;
import com.bo4um.wordsappback.entity.LanguageTest;
import com.bo4um.wordsappback.entity.TestQuestion;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserTestResult;
import com.bo4um.wordsappback.repository.LanguageTestRepository;
import com.bo4um.wordsappback.repository.TestQuestionRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import com.bo4um.wordsappback.repository.UserTestResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LanguageTestService {

    private final LanguageTestRepository languageTestRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final UserTestResultRepository userTestResultRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Получить все доступные тесты
     */
    public List<LanguageTestResponse> getAvailableTests() {
        log.debug("Fetching available tests");
        return languageTestRepository.findByIsActiveTrue().stream()
                .map(this::toTestResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получить тест с вопросами
     */
    public TestWithQuestionsResponse getTestWithQuestions(Long testId) {
        log.debug("Fetching test with questions for id: {}", testId);

        LanguageTest test = languageTestRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found with id: " + testId));

        List<TestQuestion> questions = testQuestionRepository.findByTestIdOrderByOrderNumberAsc(testId);

        return TestWithQuestionsResponse.builder()
                .id(test.getId())
                .name(test.getName())
                .description(test.getDescription())
                .language(test.getLanguage())
                .totalQuestions(test.getTotalQuestions())
                .passingScore(test.getPassingScore())
                .questions(questions.stream().map(this::toQuestionResponse).collect(Collectors.toList()))
                .build();
    }

    /**
     * Отправить ответы на тест
     */
    @Transactional
    public UserTestResultResponse submitTest(Long userId, Long testId, TestSubmitRequest request) {
        log.info("Submitting test {} for user {}", testId, userId);

        LanguageTest test = languageTestRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found with id: " + testId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<TestQuestion> questions = testQuestionRepository.findByTestIdOrderByOrderNumberAsc(testId);

        // Подсчёт результатов
        int score = 0;
        int maxScore = 0;
        Map<Long, Boolean> questionResults = new HashMap<>();
        Map<String, Integer> levelScores = new HashMap<>();

        for (TestQuestion question : questions) {
            String userAnswer = request.getAnswers().get(question.getId());
            int points = question.getPoints() != null ? question.getPoints() : 1;
            maxScore += points;

            boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(userAnswer);
            questionResults.put(question.getId(), isCorrect);

            if (isCorrect) {
                score += points;
                // Считаем баллы по уровням
                levelScores.merge(question.getLevel(), points, Integer::sum);
            }
        }

        // Определяем уровень
        String determinedLevel = calculateLevel(levelScores, score, maxScore);
        int percentage = (int) ((score * 100.0) / maxScore);

        // Сохраняем ответы в JSON
        String answersJson;
        try {
            answersJson = objectMapper.writeValueAsString(request.getAnswers());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize answers", e);
            answersJson = "{}";
        }

        // Создаём результат
        UserTestResult result = UserTestResult.builder()
                .user(user)
                .test(test)
                .score(score)
                .maxScore(maxScore)
                .determinedLevel(determinedLevel)
                .percentage(percentage)
                .answers(answersJson)
                .build();

        UserTestResult saved = userTestResultRepository.save(result);
        log.info("Test result saved for user {} with level {}", userId, determinedLevel);

        return toResultResponse(saved, test, questionResults);
    }

    /**
     * Получить историю тестов пользователя
     */
    public List<UserTestResultResponse> getUserTestHistory(Long userId) {
        log.debug("Fetching test history for user {}", userId);

        List<UserTestResult> results = userTestResultRepository.findByUserIdOrderByCompletedAtDesc(userId);
        return results.stream()
                .map(r -> toResultResponse(r, r.getTest(), null))
                .collect(Collectors.toList());
    }

    /**
     * Получить лучший результат пользователя по тесту
     */
    public UserTestResultResponse getBestResult(Long userId, Long testId) {
        log.debug("Fetching best result for user {} and test {}", userId, testId);

        List<UserTestResult> results = userTestResultRepository.findByUserIdAndTestId(userId, testId);
        
        if (results.isEmpty()) {
            return null;
        }

        UserTestResult best = results.stream()
                .max((r1, r2) -> Integer.compare(r1.getScore(), r2.getScore()))
                .orElse(null);

        return best != null ? toResultResponse(best, best.getTest(), null) : null;
    }

    /**
     * Рассчитать уровень на основе результатов
     */
    private String calculateLevel(Map<String, Integer> levelScores, int totalScore, int maxScore) {
        // Процент правильных ответов
        int percentage = (int) ((totalScore * 100.0) / maxScore);

        // Если набрано меньше 25% - уровень A1
        if (percentage < 25) {
            return "A1";
        }

        // Считаем преобладающий уровень
        String dominantLevel = levelScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("A1");

        // Корректируем по проценту
        if (percentage >= 90) {
            return "C1";
        } else if (percentage >= 75) {
            return "B2";
        } else if (percentage >= 50) {
            return "B1";
        } else if (percentage >= 35) {
            return "A2";
        } else {
            return "A1";
        }
    }

    private LanguageTestResponse toTestResponse(LanguageTest test) {
        return LanguageTestResponse.builder()
                .id(test.getId())
                .name(test.getName())
                .description(test.getDescription())
                .language(test.getLanguage())
                .totalQuestions(test.getTotalQuestions())
                .passingScore(test.getPassingScore())
                .isActive(test.getIsActive())
                .build();
    }

    private TestQuestionResponse toQuestionResponse(TestQuestion question) {
        return TestQuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .level(question.getLevel())
                .points(question.getPoints())
                .orderNumber(question.getOrderNumber())
                .build();
    }

    private UserTestResultResponse toResultResponse(UserTestResult result, LanguageTest test, Map<Long, Boolean> questionResults) {
        return UserTestResultResponse.builder()
                .id(result.getId())
                .testId(test.getId())
                .testName(test.getName())
                .score(result.getScore())
                .maxScore(result.getMaxScore())
                .percentage(result.getPercentage())
                .determinedLevel(result.getDeterminedLevel())
                .completedAt(result.getCompletedAt())
                .questionResults(questionResults)
                .build();
    }
}
