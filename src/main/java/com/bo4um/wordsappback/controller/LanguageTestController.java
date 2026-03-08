package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.TestSubmitRequest;
import com.bo4um.wordsappback.dto.TestWithQuestionsResponse;
import com.bo4um.wordsappback.dto.LanguageTestResponse;
import com.bo4um.wordsappback.dto.UserTestResultResponse;
import com.bo4um.wordsappback.security.JwtTokenProvider;
import com.bo4um.wordsappback.service.LanguageTestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для тестирования уровня языка
 */
@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class LanguageTestController {

    private final LanguageTestService languageTestService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Получить все доступные тесты
     * GET /api/tests
     */
    @GetMapping
    public ResponseEntity<List<LanguageTestResponse>> getAvailableTests() {
        return ResponseEntity.ok(languageTestService.getAvailableTests());
    }

    /**
     * Получить тест с вопросами
     * GET /api/tests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestWithQuestionsResponse> getTestWithQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(languageTestService.getTestWithQuestions(id));
    }

    /**
     * Отправить ответы на тест
     * POST /api/tests/{id}/submit
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<UserTestResultResponse> submitTest(
            @PathVariable Long id,
            @RequestBody TestSubmitRequest request,
            HttpServletRequest servletRequest
    ) {
        Long userId = getUserIdFromRequest(servletRequest);
        return ResponseEntity.ok(languageTestService.submitTest(userId, id, request));
    }

    /**
     * Получить историю тестов пользователя
     * GET /api/tests/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<UserTestResultResponse>> getUserTestHistory(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(languageTestService.getUserTestHistory(userId));
    }

    /**
     * Получить лучший результат по тесту
     * GET /api/tests/{id}/best-result
     */
    @GetMapping("/{id}/best-result")
    public ResponseEntity<UserTestResultResponse> getBestResult(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        UserTestResultResponse result = languageTestService.getBestResult(userId, id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header not found or invalid");
        }
        String token = authHeader.substring(7);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        if ("user".equals(username)) {
            return 1L;
        } else if ("admin".equals(username)) {
            return 2L;
        } else {
            throw new IllegalArgumentException("Unknown user: " + username);
        }
    }
}
