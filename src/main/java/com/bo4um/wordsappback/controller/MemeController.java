package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.MemeExerciseRequest;
import com.bo4um.wordsappback.dto.MemeExerciseResponse;
import com.bo4um.wordsappback.dto.MemeResponse;
import com.bo4um.wordsappback.service.MemeService;
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
@RequestMapping("/api/memes")
@RequiredArgsConstructor
@Tag(name = "Meme-Based Learning", description = "Изучение языка через мемы")
@SecurityRequirement(name = "bearerAuth")
public class MemeController {

    private final MemeService memeService;

    @GetMapping("/trending")
    @Operation(summary = "Трендовые мемы", description = "Получить популярные мемы для изучения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<MemeResponse>> getTrendingMemes(
            @RequestParam(required = false) String language,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(memeService.getTrendingMemes(language, limit));
    }

    @GetMapping("/difficulty")
    @Operation(summary = "Мемы по уровню", description = "Получить мемы по уровню сложности")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<MemeResponse>> getMemesByDifficulty(
            @RequestParam String language,
            @RequestParam String difficulty) {
        return ResponseEntity.ok(memeService.getMemesByDifficulty(language, difficulty));
    }

    @PostMapping("/exercise")
    @Operation(summary = "Упражнение из мема", description = "Сгенерировать упражнение на основе мема")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Упражнение создано"),
            @ApiResponse(responseCode = "404", description = "Мем не найден")
    })
    public ResponseEntity<MemeExerciseResponse> generateExercise(
            @RequestBody MemeExerciseRequest request) {
        return ResponseEntity.ok(memeService.generateExercise(request));
    }

    @PostMapping("/{id}/analyze")
    @Operation(summary = "AI анализ мема", description = "Проанализировать мем с помощью AI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Анализ завершён"),
            @ApiResponse(responseCode = "404", description = "Мем не найден")
    })
    public ResponseEntity<MemeResponse> analyzeMeme(@PathVariable Long id) {
        return ResponseEntity.ok(memeService.analyzeMemeWithAI(id));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Лайкнуть мем", description = "Поставить лайк мему")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк поставлен"),
    })
    public ResponseEntity<Void> likeMeme(@PathVariable Long id) {
        memeService.likeMeme(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "Поделиться мемом", description = "Поделиться мемом в соцсетях")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Мем partagé"),
    })
    public ResponseEntity<Map<String, Object>> shareMeme(@PathVariable Long id) {
        memeService.shareMeme(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "shareUrl", "https://wordsapp.com/meme/" + id
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Мем по ID", description = "Получить информацию о меме")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Мем не найден")
    })
    public ResponseEntity<MemeResponse> getMeme(@PathVariable Long id) {
        return ResponseEntity.ok(
                memeService.getTrendingMemes(null, 100).stream()
                        .filter(m -> m.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Meme not found"))
        );
    }
}
