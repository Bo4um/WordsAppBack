package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.LearningMaterialResponse;
import com.bo4um.wordsappback.service.LearningMaterialService;
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

@Slf4j
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Tag(name = "Учебные материалы", description = "Загрузка и анализ изображений/PDF")
@SecurityRequirement(name = "bearerAuth")
public class LearningMaterialController {

    private final LearningMaterialService learningMaterialService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить материал", description = "Загрузить изображение или PDF для анализа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Материал загружен"),
            @ApiResponse(responseCode = "400", description = "Неверный файл"),
            @ApiResponse(responseCode = "413", description = "Файл слишком большой (>10MB)")
    })
    public ResponseEntity<LearningMaterialResponse> uploadMaterial(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        Long userId = 1L; // Заглушка - получить из JWT
        return ResponseEntity.ok(learningMaterialService.uploadMaterial(userId, file));
    }

    @GetMapping
    @Operation(summary = "Все материалы", description = "Получить все загруженные материалы пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<LearningMaterialResponse>> getUserMaterials(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(learningMaterialService.getUserMaterials(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Материал по ID", description = "Получить конкретный материал")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Материал не найден")
    })
    public ResponseEntity<LearningMaterialResponse> getMaterial(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(learningMaterialService.getMaterial(id, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить материал", description = "Удалить загруженный материал")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удалено"),
            @ApiResponse(responseCode = "404", description = "Материал не найден")
    })
    public ResponseEntity<Void> deleteMaterial(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = 1L; // Заглушка
        learningMaterialService.deleteMaterial(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Обработать материал", description = "Повторно обработать материал (OCR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обработка начата"),
            @ApiResponse(responseCode = "404", description = "Материал не найден")
    })
    public ResponseEntity<Void> processMaterial(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = 1L; // Заглушка
        var material = learningMaterialService.getMaterial(id, userId);
        // В реальном приложении запускать асинхронно
        return ResponseEntity.ok().build();
    }
}
