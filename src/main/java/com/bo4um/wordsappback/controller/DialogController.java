package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.*;
import com.bo4um.wordsappback.service.DialogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dialog")
@RequiredArgsConstructor
@Tag(name = "AI Диалоги", description = "Ролевые диалоги с AI-компаньоном")
@SecurityRequirement(name = "bearerAuth")
public class DialogController {

    private final DialogService dialogService;

    @GetMapping("/scenarios")
    @Operation(summary = "Все сценарии", description = "Получить список всех доступных сценариев для ролевых диалогов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<ScenarioResponse>> getScenarios(
            @RequestParam(required = false) String language) {
        return ResponseEntity.ok(dialogService.getAllScenarios(language));
    }

    @GetMapping("/scenarios/{id}")
    @Operation(summary = "Сценарий по ID", description = "Получить детали сценария по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Сценарий не найден")
    })
    public ResponseEntity<ScenarioResponse> getScenario(@PathVariable Long id) {
        return ResponseEntity.ok(dialogService.getScenarioById(id));
    }

    @PostMapping("/start")
    @Operation(summary = "Начать диалог", description = "Начать новую сессию ролевого диалога")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Диалог начат"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Сценарий или персонаж не найден")
    })
    public ResponseEntity<DialogSessionResponse> startDialog(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StartDialogRequest request) {
        // TODO: Get user ID from JWT token properly
        Long userId = 1L; // Заглушка, нужно реализовать получение из токена
        return ResponseEntity.ok(dialogService.startDialog(userId, request));
    }

    @GetMapping("/sessions")
    @Operation(summary = "История сессий", description = "Получить все сессии пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<DialogSessionResponse>> getSessions(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(dialogService.getUserSessions(userId));
    }

    @GetMapping("/sessions/{id}/history")
    @Operation(summary = "История сообщений", description = "Получить историю сообщений сессии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    public ResponseEntity<List<DialogMessageResponse>> getSessionHistory(@PathVariable Long id) {
        return ResponseEntity.ok(dialogService.getSessionHistory(id));
    }

    @PostMapping("/message")
    @Operation(summary = "Отправить сообщение", description = "Отправить сообщение в диалог и получить ответ AI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ получен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    public ResponseEntity<DialogMessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DialogMessageRequest request) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(dialogService.sendMessage(userId, request));
    }

    @PostMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Отправить сообщение (streaming)", description = "Отправить сообщение и получить streaming ответ AI через SSE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streaming начался"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    public Flux<DialogMessageResponse> sendMessageStreaming(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DialogMessageRequest request) {
        Long userId = 1L; // Заглушка
        return dialogService.sendMessageStreaming(userId, request);
    }

    @PostMapping("/sessions/{id}/end")
    @Operation(summary = "Завершить сессию", description = "Завершить текущую сессию диалога")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сессия завершена"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    public ResponseEntity<DialogSessionResponse> endSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(dialogService.endSession(id, userId));
    }
}
