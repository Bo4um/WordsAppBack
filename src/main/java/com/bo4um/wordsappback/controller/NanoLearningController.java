package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.entity.NanoLearningSession;
import com.bo4um.wordsappback.service.NanoLearningService;
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
@RequestMapping("/api/nanolearning")
@RequiredArgsConstructor
@Tag(name = "Smart Nanolearning", description = "Микро-обучение по запросу")
@SecurityRequirement(name = "bearerAuth")
public class NanoLearningController {

    private final NanoLearningService nanoLearningService;

    @PostMapping("/generate")
    @Operation(summary = "Сгенерировать нано-урок", description = "Создать микро-урок из контента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Урок создан"),
    })
    public ResponseEntity<NanoLearningSession> generateNanoSession(
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        String contentType = request.getOrDefault("contentType", "article");
        String language = request.getOrDefault("language", "English");
        String difficulty = request.getOrDefault("difficulty", "B1");

        NanoLearningSession session = nanoLearningService.generateNanoSession(
                content, contentType, language, difficulty);

        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Завершить урок", description = "Отметить урок как завершённый")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Урок завершён"),
    })
    public ResponseEntity<NanoLearningSession> completeSession(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quizScore = request.get("quizScore");
        return ResponseEntity.ok(nanoLearningService.completeSession(id, quizScore));
    }

    @GetMapping("/incomplete")
    @Operation(summary = "Незавершённые уроки", description = "Получить незавершённые уроки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<NanoLearningSession>> getIncompleteSessions() {
        return ResponseEntity.ok(nanoLearningService.getIncompleteSessions());
    }

    @GetMapping("/by-level")
    @Operation(summary = "Уроки по уровню", description = "Получить уроки по уровню")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<NanoLearningSession>> getSessionsByLevel(
            @RequestParam String language,
            @RequestParam String difficulty) {
        return ResponseEntity.ok(nanoLearningService.getSessionsByLevel(language, difficulty));
    }
}
