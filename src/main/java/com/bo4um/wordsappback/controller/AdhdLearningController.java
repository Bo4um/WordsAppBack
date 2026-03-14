package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.entity.AdhdLearningProfile;
import com.bo4um.wordsappback.service.AdhdLearningService;
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

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/adhd")
@RequiredArgsConstructor
@Tag(name = "ADHD-Friendly Mode", description = "Режим для пользователей с ADHD")
@SecurityRequirement(name = "bearerAuth")
public class AdhdLearningController {

    private final AdhdLearningService adhdService;

    @GetMapping("/profile")
    @Operation(summary = "Получить профиль", description = "Получить настройки ADHD режима")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<AdhdLearningProfile> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(adhdService.getOrCreateProfile(userId));
    }

    @PostMapping("/enable")
    @Operation(summary = "Включить ADHD режим", description = "Активировать режим для ADHD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Режим включён"),
    })
    public ResponseEntity<AdhdLearningProfile> enableAdhdMode(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(adhdService.enableAdhdMode(userId));
    }

    @PostMapping("/disable")
    @Operation(summary = "Выключить ADHD режим", description = "Деактивировать режим")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Режим выключен"),
    })
    public ResponseEntity<AdhdLearningProfile> disableAdhdMode(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(adhdService.disableAdhdMode(userId));
    }

    @PutMapping("/session-duration")
    @Operation(summary = "Длительность сессии", description = "Установить длительность сессии (2-10 мин)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Неверная длительность")
    })
    public ResponseEntity<AdhdLearningProfile> updateSessionDuration(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Integer> request) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(adhdService.updateSessionDuration(userId, request.get("duration")));
    }

    @PutMapping("/focus-mode")
    @Operation(summary = "Режим фокуса", description = "Установить режим фокуса (pomodoro, flow, flexible)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
    })
    public ResponseEntity<AdhdLearningProfile> updateFocusMode(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(adhdService.updateFocusMode(userId, request.get("mode")));
    }

    @GetMapping("/recommended-duration")
    @Operation(summary = "Рекомендуемая длительность", description = "Получить рекомендуемую длительность сессии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<Map<String, Integer>> getRecommendedDuration(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        int duration = adhdService.getRecommendedSessionLength(userId);
        return ResponseEntity.ok(Map.of("recommendedMinutes", duration));
    }
}
