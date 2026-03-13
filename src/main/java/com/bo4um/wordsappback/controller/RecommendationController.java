package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.RecommendationResponse;
import com.bo4um.wordsappback.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Рекомендации", description = "Персонализированные рекомендации для пользователя")
@SecurityRequirement(name = "bearerAuth")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    @Operation(summary = "Все рекомендации", description = "Получить все рекомендации для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка - получить из JWT
        return ResponseEntity.ok(recommendationService.getRecommendations(userId));
    }

    @GetMapping("/unread")
    @Operation(summary = "Непрочитанные рекомендации", description = "Получить непрочитанные рекомендации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<RecommendationResponse>> getUnreadRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(recommendationService.getUnreadRecommendations(userId));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Отметить как прочитанное", description = "Отметить рекомендацию как прочитанную")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "404", description = "Рекомендация не найдена")
    })
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = 1L; // Заглушка
        recommendationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    @Operation(summary = "Отметить все как прочитанное", description = "Отметить все рекомендации как прочитанные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
    })
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        recommendationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cleanup")
    @Operation(summary = "Удалить прочитанные", description = "Удалить все прочитанные рекомендации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
    })
    public ResponseEntity<Void> cleanupOldRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        recommendationService.cleanupOldRecommendations(userId);
        return ResponseEntity.ok().build();
    }
}
