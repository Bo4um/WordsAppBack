package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.*;
import com.bo4um.wordsappback.service.SocialService;
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
@RequestMapping("/api/social")
@RequiredArgsConstructor
@Tag(name = "Социальные функции", description = "Друзья, лидерборды, челленджи")
@SecurityRequirement(name = "bearerAuth")
public class SocialController {

    private final SocialService socialService;

    // ==================== FRIENDS ====================

    @PostMapping("/friends/request/{friendId}")
    @Operation(summary = "Отправить запрос в друзья", description = "Отправить запрос пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос отправлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка")
    })
    public ResponseEntity<Void> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId) {
        Long userId = 1L; // Заглушка
        socialService.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/friends/accept/{requestId}")
    @Operation(summary = "Принять запрос в друзья", description = "Принять запрос в друзья")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Принято"),
            @ApiResponse(responseCode = "400", description = "Ошибка")
    })
    public ResponseEntity<Void> acceptFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long requestId) {
        Long userId = 1L; // Заглушка
        socialService.acceptFriendRequest(userId, requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friends")
    @Operation(summary = "Мои друзья", description = "Получить список друзей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<FriendResponse>> getFriends(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(socialService.getFriends(userId));
    }

    // ==================== LEADERBOARD ====================

    @GetMapping("/leaderboard")
    @Operation(summary = "Таблица лидеров", description = "Получить топ пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(socialService.getLeaderboard(limit));
    }

    @GetMapping("/leaderboard/my-rank")
    @Operation(summary = "Мой ранг", description = "Получить свой ранг в лидерборде")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<RankResponse> getMyRank(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(socialService.getUserRank(userId));
    }

    // ==================== CHALLENGES ====================

    @GetMapping("/challenges")
    @Operation(summary = "Активные челленджи", description = "Получить список активных челленджей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<ChallengeResponse>> getActiveChallenges() {
        return ResponseEntity.ok(socialService.getActiveChallenges());
    }

    @GetMapping("/challenges/progress")
    @Operation(summary = "Мой прогресс", description = "Получить прогресс по челленджам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<ChallengeProgressResponse>> getChallengeProgress(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(socialService.getUserChallengeProgress(userId));
    }

    @PostMapping("/challenges/{progressId}/claim")
    @Operation(summary = "Получить награду", description = "Получить награду за завершённый челлендж")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Награда получена"),
            @ApiResponse(responseCode = "400", description = "Ошибка")
    })
    public ResponseEntity<Void> claimReward(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long progressId) {
        Long userId = 1L; // Заглушка
        socialService.claimReward(userId, progressId);
        return ResponseEntity.ok().build();
    }
}
