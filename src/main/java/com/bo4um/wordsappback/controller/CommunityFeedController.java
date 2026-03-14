package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.entity.CommunityPost;
import com.bo4um.wordsappback.service.CommunityFeedService;
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
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Tag(name = "Community Feed", description = "Социальная лента (аудио/видео)")
@SecurityRequirement(name = "bearerAuth")
public class CommunityFeedController {

    private final CommunityFeedService feedService;

    @GetMapping("/trending")
    @Operation(summary = "Трендовые посты", description = "Получить популярные посты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<CommunityPost>> getTrendingPosts(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(feedService.getTrendingPosts(limit));
    }

    @GetMapping("/language/{language}")
    @Operation(summary = "Посты по языку", description = "Получить посты по языку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<CommunityPost>> getPostsByLanguage(
            @PathVariable String language) {
        return ResponseEntity.ok(feedService.getPostsByLanguage(language));
    }

    @PostMapping
    @Operation(summary = "Создать пост", description = "Опубликовать аудио/видео кружочек")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост создан"),
    })
    public ResponseEntity<CommunityPost> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        Long userId = 1L; // Заглушка
        String username = userDetails.getUsername();

        CommunityPost post = feedService.createPost(
                userId,
                username,
                request.get("content"),
                request.get("mediaUrl"),
                request.get("mediaType"),
                request.get("language"),
                request.get("topic")
        );

        return ResponseEntity.ok(post);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Лайкнуть пост", description = "Поставить лайк")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк поставлен"),
    })
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        feedService.likePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Комментировать", description = "Добавить комментарий")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий добавлен"),
    })
    public ResponseEntity<Void> commentOnPost(@PathVariable Long id) {
        feedService.commentOnPost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "Поделиться", description = "Поделиться постом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Постом поделились"),
    })
    public ResponseEntity<Void> sharePost(@PathVariable Long id) {
        feedService.sharePost(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    @Operation(summary = "Мои посты", description = "Получить свои посты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<List<CommunityPost>> getMyPosts(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка
        return ResponseEntity.ok(feedService.getUserPosts(userId));
    }
}
