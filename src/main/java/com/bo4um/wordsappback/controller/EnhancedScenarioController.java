package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.EnhancedScenarioResponse;
import com.bo4um.wordsappback.service.EnhancedScenarioService;
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
@RequestMapping("/api/scenarios/enhanced")
@RequiredArgsConstructor
@Tag(name = "Situational AI 2.0", description = "Улучшенные сценарии с эмоциями")
@SecurityRequirement(name = "bearerAuth")
public class EnhancedScenarioController {

    private final EnhancedScenarioService scenarioService;

    @GetMapping("/trending")
    @Operation(summary = "Трендовые сценарии", description = "Получить популярные сценарии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<EnhancedScenarioResponse>> getTrendingScenarios(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(scenarioService.getTrendingScenarios(limit));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Сценарии по категории", description = "Получить сценарии по категории")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<EnhancedScenarioResponse>> getScenariosByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(scenarioService.getScenariosByCategory(category));
    }

    @GetMapping("/relocation")
    @Operation(summary = "Сценарии для релокации", description = "Сценарии для эмигрантов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<EnhancedScenarioResponse>> getRelocationScenarios(
            @RequestParam(required = false, defaultValue = "English") String language) {
        return ResponseEntity.ok(scenarioService.getRelocationScenarios(language));
    }

    @GetMapping("/with-emotions")
    @Operation(summary = "Сценарии с эмоциями", description = "Сценарии с симуляцией эмоций")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<EnhancedScenarioResponse>> getScenariosWithEmotions(
            @RequestParam String language,
            @RequestParam String difficulty) {
        return ResponseEntity.ok(scenarioService.getScenariosWithEmotions(language, difficulty));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Завершить сценарий", description = "Отметить сценарий как завершённый")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сценарий завершён"),
    })
    public ResponseEntity<Void> completeScenario(@PathVariable Long id) {
        scenarioService.incrementCompletionCount(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/rate")
    @Operation(summary = "Оценить сценарий", description = "Поставить оценку сценарию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оценка поставлена"),
            @ApiResponse(responseCode = "400", description = "Неверная оценка")
    })
    public ResponseEntity<Void> rateScenario(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> ratingRequest) {
        Integer rating = ratingRequest.get("rating");
        scenarioService.rateScenario(id, rating);
        return ResponseEntity.ok().build();
    }
}
