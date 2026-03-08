package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.DictionaryProgressResponse;
import com.bo4um.wordsappback.dto.UserProgressResponse;
import com.bo4um.wordsappback.dto.WordLearningRequest;
import com.bo4um.wordsappback.dto.WordLearningResponse;
import com.bo4um.wordsappback.security.JwtTokenProvider;
import com.bo4um.wordsappback.service.DictionaryProgressService;
import com.bo4um.wordsappback.service.UserProgressService;
import com.bo4um.wordsappback.service.WordLearningService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для управления прогрессом пользователя
 */
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService userProgressService;
    private final DictionaryProgressService dictionaryProgressService;
    private final WordLearningService wordLearningService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Получить общий прогресс пользователя
     * GET /api/progress
     */
    @GetMapping
    public ResponseEntity<UserProgressResponse> getProgress(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(userProgressService.getProgress(userId));
    }

    /**
     * Получить статистику (streak, всего слов и т.д.)
     * GET /api/progress/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<UserProgressResponse> getStats(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(userProgressService.getStats(userId));
    }

    /**
     * Получить прогресс по словарям
     * GET /api/progress/dictionaries
     */
    @GetMapping("/dictionaries")
    public ResponseEntity<List<DictionaryProgressResponse>> getDictionaryProgress(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(dictionaryProgressService.getDictionaryProgress(userId));
    }

    /**
     * Получить прогресс по конкретному словарю
     * GET /api/progress/dictionaries/{language}
     */
    @GetMapping("/dictionaries/{language}")
    public ResponseEntity<DictionaryProgressResponse> getDictionaryProgress(
            HttpServletRequest request,
            @PathVariable String language
    ) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(dictionaryProgressService.getDictionaryProgress(userId, language));
    }

    /**
     * Получить все изученные слова
     * GET /api/progress/words
     */
    @GetMapping("/words")
    public ResponseEntity<List<WordLearningResponse>> getLearnedWords(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(wordLearningService.getLearnedWords(userId));
    }

    /**
     * Получить изученные слова по языку
     * GET /api/progress/words?language=English
     */
    @GetMapping("/words/by-language")
    public ResponseEntity<List<WordLearningResponse>> getLearnedWordsByLanguage(
            HttpServletRequest request,
            @RequestParam String language
    ) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(wordLearningService.getLearnedWords(userId, language));
    }

    /**
     * Получить слова для повторения
     * GET /api/progress/words/review
     */
    @GetMapping("/words/review")
    public ResponseEntity<List<WordLearningResponse>> getWordsForReview(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(wordLearningService.getWordsForReview(userId));
    }

    /**
     * Отметить слово как изученное
     * POST /api/progress/words
     */
    @PostMapping("/words")
    public ResponseEntity<WordLearningResponse> markWordAsLearned(
            HttpServletRequest request,
            @RequestBody WordLearningRequest wordRequest
    ) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(wordLearningService.markWordAsLearned(userId, wordRequest));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header not found or invalid");
        }
        String token = authHeader.substring(7);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        // Для простоты используем маппинг: user=1, admin=2
        // В реальном приложении нужно использовать UserRepository
        if ("user".equals(username)) {
            return 1L;
        } else if ("admin".equals(username)) {
            return 2L;
        } else {
            throw new IllegalArgumentException("Unknown user: " + username);
        }
    }
}
