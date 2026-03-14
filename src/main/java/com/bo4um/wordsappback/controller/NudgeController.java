package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.entity.UserNudge;
import com.bo4um.wordsappback.service.NudgeService;
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
@RequestMapping("/api/nudges")
@RequiredArgsConstructor
@Tag(name = "Contextual Nudges", description = "Персонализированные уведомления")
@SecurityRequirement(name = "bearerAuth")
public class NudgeController {

    private final NudgeService nudgeService;

    @GetMapping("/unread")
    @Operation(summary = "Непрочитанные nudges", description = "Получить непрочитанные уведомления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<UserNudge>> getUnreadNudges(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(nudgeService.getUnreadNudges(userId));
    }

    @GetMapping("/history")
    @Operation(summary = "История nudges", description = "Получить историю уведомлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<UserNudge>> getNudgeHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(nudgeService.getNudgeHistory(userId, limit));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Отметить как прочитанное", description = "Отметить уведомление как прочитанное")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
    })
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        nudgeService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/action")
    @Operation(summary = "Отметить как выполненное", description = "Пользователь выполнил действие из уведомления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
    })
    public ResponseEntity<Void> markAsActioned(@PathVariable Long id) {
        nudgeService.markAsActioned(id);
        return ResponseEntity.ok().build();
    }

    // Demo endpoints for testing nudges
    @PostMapping("/test/encouragement")
    @Operation(summary = "Тест: поощрение", description = "Отправить тестовое поощрение")
    public ResponseEntity<UserNudge> testEncouragement(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L;
        return ResponseEntity.ok(nudgeService.sendEncouragement(userId, "7-day streak!"));
    }

    @PostMapping("/test/reminder")
    @Operation(summary = "Тест: напоминание", description = "Отправить тестовое напоминание")
    public ResponseEntity<UserNudge> testReminder(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L;
        return ResponseEntity.ok(nudgeService.sendReminder(userId, "vocabulary practice"));
    }

    @PostMapping("/test/challenge")
    @Operation(summary = "Тест: челлендж", description = "Отправить тестовый челлендж")
    public ResponseEntity<UserNudge> testChallenge(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L;
        return ResponseEntity.ok(nudgeService.sendChallenge(userId, "Learn 50 words today"));
    }
}
