package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.PronunciationResponse;
import com.bo4um.wordsappback.service.PronunciationService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pronunciation")
@RequiredArgsConstructor
@Tag(name = "Произношение", description = "Анализ произношения через Whisper API")
@SecurityRequirement(name = "bearerAuth")
public class PronunciationController {

    private final PronunciationService pronunciationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Проверить произношение", description = "Загрузить аудио и получить анализ произношения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Анализ завершён"),
            @ApiResponse(responseCode = "400", description = "Неверный файл"),
            @ApiResponse(responseCode = "413", description = "Файл слишком большой (>25MB)")
    })
    public ResponseEntity<PronunciationResponse> submitPronunciation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("targetPhrase") String targetPhrase) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(pronunciationService.submitPronunciation(userId, audioFile, targetPhrase));
    }

    @GetMapping
    @Operation(summary = "История попыток", description = "Получить все попытки произношения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<PronunciationResponse>> getUserAttempts(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(pronunciationService.getUserAttempts(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Попытка по ID", description = "Получить конкретную попытку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Попытка не найдена")
    })
    public ResponseEntity<PronunciationResponse> getAttempt(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(pronunciationService.getAttempt(id, userId));
    }

    @GetMapping("/stats")
    @Operation(summary = "Статистика произношения", description = "Получить статистику по произношению")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<Map<String, Object>> getPronunciationStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(pronunciationService.getPronunciationStats(userId));
    }
}
