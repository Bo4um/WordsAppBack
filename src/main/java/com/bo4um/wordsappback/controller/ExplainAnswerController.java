package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.ExplainAnswerRequest;
import com.bo4um.wordsappback.dto.ExplainAnswerResponse;
import com.bo4um.wordsappback.service.ExplainAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Объяснения", description = "Объяснение ошибок и грамматических правил")
@SecurityRequirement(name = "bearerAuth")
public class ExplainAnswerController {

    private final ExplainAnswerService explainAnswerService;

    @PostMapping("/explain")
    @Operation(summary = "Объяснить ответ", description = "Получить подробное объяснение почему ответ неверный")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объяснение получено"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    public ResponseEntity<ExplainAnswerResponse> explainAnswer(@RequestBody ExplainAnswerRequest request) {
        // Валидация
        if (request.getUserAnswer() == null || request.getCorrectAnswer() == null) {
            return ResponseEntity.badRequest().build();
        }

        ExplainAnswerResponse response = explainAnswerService.explainAnswer(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain/quick")
    @Operation(summary = "Быстрое объяснение", description = "Краткое объяснение ошибки (без деталей)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объяснение получено"),
    })
    public ResponseEntity<ExplainAnswerResponse> explainAnswerQuick(@RequestBody ExplainAnswerRequest request) {
        // Для quick версии можно установить флаг в сервисе
        // Пока используем тот же метод
        return explainAnswer(request);
    }
}
