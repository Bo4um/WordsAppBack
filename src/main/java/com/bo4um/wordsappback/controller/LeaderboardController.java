package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.service.LeaderboardService;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Dynamic Leaderboards", description = "Таблицы лидеров по категориям")
@SecurityRequirement(name = "bearerAuth")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Глобальный лидерборд", description = "Общий рейтинг пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<Map<String, Object>>> getGlobalLeaderboard(
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(limit));
    }

    @GetMapping("/{category}")
    @Operation(summary = "Лидерборд по категории", description = "Рейтинг по категории (streak, words, exercises, pronunciation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<Map<String, Object>>> getLeaderboardByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(leaderboardService.getLeaderboardByCategory(category));
    }

    @GetMapping("/my-rank/{category}")
    @Operation(summary = "Мой ранг", description = "Получить свой ранг в категории")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<Map<String, Object>> getMyRank(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String category) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(leaderboardService.getUserRank(userId, category));
    }
}
