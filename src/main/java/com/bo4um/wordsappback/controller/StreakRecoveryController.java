package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.service.StreakRecoveryService;
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
@RequestMapping("/api/streak")
@RequiredArgsConstructor
@Tag(name = "Streak Recovery", description = "Восстановление серии")
@SecurityRequirement(name = "bearerAuth")
public class StreakRecoveryController {

    private final StreakRecoveryService recoveryService;

    @PostMapping("/recover")
    @Operation(summary = "Восстановить серию", description = "Использовать токен восстановления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Серия восстановлена"),
            @ApiResponse(responseCode = "400", description = "Нет токенов")
    })
    public ResponseEntity<Map<String, Object>> recoverStreak(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка

        boolean success = recoveryService.useRecoveryToken(userId);
        int remaining = recoveryService.getRecoveryTokens(userId);

        return ResponseEntity.ok(Map.of(
                "success", success,
                "tokensRemaining", remaining,
                "message", success ? "Streak recovered!" : "No tokens available"
        ));
    }

    @GetMapping("/tokens")
    @Operation(summary = "Токены восстановления", description = "Получить количество токенов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<Map<String, Integer>> getRecoveryTokens(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка

        int tokens = recoveryService.getRecoveryTokens(userId);
        return ResponseEntity.ok(Map.of("tokens", tokens));
    }

    @PostMapping("/award")
    @Operation(summary = "Наградить токеном", description = "Выдать токен восстановления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен выдан"),
    })
    public ResponseEntity<Void> awardToken(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка

        recoveryService.awardToken(userId);
        return ResponseEntity.ok().build();
    }
}
