package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.ExerciseResponse;
import com.bo4um.wordsappback.dto.GenerateExerciseRequest;
import com.bo4um.wordsappback.dto.SubmitAnswerRequest;
import com.bo4um.wordsappback.dto.SubmitAnswerResponse;
import com.bo4um.wordsappback.service.ExerciseGeneratorService;
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

@Slf4j
@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
@Tag(name = "Упражнения", description = "Генерация и выполнение упражнений")
@SecurityRequirement(name = "bearerAuth")
public class ExerciseController {

    private final ExerciseGeneratorService exerciseGeneratorService;

    @PostMapping("/generate")
    @Operation(summary = "Сгенерировать упражнения", description = "Сгенерировать упражнения через AI на основе параметров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Упражнения сгенерированы"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры")
    })
    public ResponseEntity<List<ExerciseResponse>> generateExercises(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GenerateExerciseRequest request) {
        Long userId = 1L; // Заглушка - получить из JWT

        if (request.getLanguage() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ExerciseResponse> exercises = exerciseGeneratorService.generateExercises(userId, request);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping
    @Operation(summary = "Мои упражнения", description = "Получить список упражнений пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<ExerciseResponse>> getMyExercises(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Boolean completed) {
        Long userId = 1L; // Заглушка

        List<ExerciseResponse> exercises = exerciseGeneratorService.getUserExercises(userId, completed);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Упражнение по ID", description = "Получить конкретное упражнение")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Упражнение не найдено")
    })
    public ResponseEntity<ExerciseResponse> getExercise(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseGeneratorService.getExercise(id));
    }

    @PostMapping("/submit")
    @Operation(summary = "Отправить ответ", description = "Проверить ответ пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ проверен"),
            @ApiResponse(responseCode = "404", description = "Упражнение не найдено")
    })
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SubmitAnswerRequest request) {
        Long userId = 1L; // Заглушка

        SubmitAnswerResponse response = exerciseGeneratorService.submitAnswer(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить упражнение", description = "Удалить упражнение по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Удалено"),
            @ApiResponse(responseCode = "404", description = "Упражнение не найдено")
    })
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseGeneratorService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
