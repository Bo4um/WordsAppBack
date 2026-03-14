package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.entity.ExamPrepTest;
import com.bo4um.wordsappback.service.ExamPrepService;
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
@RequestMapping("/api/exam-prep")
@RequiredArgsConstructor
@Tag(name = "IELTS/TOEFL Prep", description = "Подготовка к экзаменам")
@SecurityRequirement(name = "bearerAuth")
public class ExamPrepController {

    private final ExamPrepService examPrepService;

    @PostMapping("/submit")
    @Operation(summary = "Сдать тест", description = "Отправить результаты практического теста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тест сдан"),
    })
    public ResponseEntity<ExamPrepTest> submitTest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request) {
        Long userId = 1L; // Заглушка

        ExamPrepTest test = examPrepService.submitTest(
                userId,
                (String) request.get("examType"),
                (String) request.get("section"),
                (Integer) request.get("score"),
                (Integer) request.get("maxScore")
        );

        return ResponseEntity.ok(test);
    }

    @GetMapping("/history")
    @Operation(summary = "История тестов", description = "Получить историю тестов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<ExamPrepTest>> getTestHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String examType) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(examPrepService.getUserTests(userId, examType));
    }

    @GetMapping("/average")
    @Operation(summary = "Средний балл", description = "Получить средний балл по секции")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<Map<String, Object>> getAverageScore(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String examType,
            @RequestParam String section) {
        Long userId = 1L; // Заглушка
        double average = examPrepService.getAverageScore(userId, examType, section);

        return ResponseEntity.ok(Map.of(
                "examType", examType,
                "section", section,
                "averageScore", average,
                "maxScore", 100
        ));
    }
}
