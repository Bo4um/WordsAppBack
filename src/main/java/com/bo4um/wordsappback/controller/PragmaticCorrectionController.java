package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.PragmaticCorrectionResponse;
import com.bo4um.wordsappback.service.PragmaticCorrectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pragmatics")
@RequiredArgsConstructor
@Tag(name = "Pragmatic Error Correction", description = "Soft Skills Coach")
@SecurityRequirement(name = "bearerAuth")
public class PragmaticCorrectionController {

    private final PragmaticCorrectionService correctionService;

    @PostMapping("/analyze")
    @Operation(summary = "Анализ прагматики", description = "Анализировать текст на уместность тона и формальности")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Анализ завершён"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    public ResponseEntity<PragmaticCorrectionResponse> analyzePragmatics(
            @RequestBody Map<String, String> request) {
        String text = request.get("text");
        String context = request.getOrDefault("context", "general");
        String language = request.getOrDefault("language", "English");

        return ResponseEntity.ok(correctionService.analyzePragmatics(text, context, language));
    }

    @GetMapping("/recent")
    @Operation(summary = "Недавние ошибки", description = "Получить недавние прагматические ошибки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<PragmaticCorrectionResponse>> getRecentErrors(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(correctionService.getRecentErrors(limit));
    }

    @GetMapping("/by-type")
    @Operation(summary = "Ошибки по типу", description = "Получить ошибки по типу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<PragmaticCorrectionResponse>> getErrorsByType(
            @RequestParam String errorType) {
        return ResponseEntity.ok(correctionService.getErrorsByType(errorType));
    }

    @PostMapping("/{id}/helpful")
    @Operation(summary = "Отметить как полезное", description = "Отметить исправление как полезное")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отмечено"),
    })
    public ResponseEntity<Void> markAsHelpful(@PathVariable Long id) {
        correctionService.markErrorAsHelpful(id);
        return ResponseEntity.ok().build();
    }
}
