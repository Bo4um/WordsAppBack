package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.SubscriptionResponse;
import com.bo4um.wordsappback.dto.UpgradeSubscriptionRequest;
import com.bo4um.wordsappback.entity.UserSubscription;
import com.bo4um.wordsappback.service.StripeService;
import com.bo4um.wordsappback.service.SubscriptionService;
import com.stripe.exception.StripeException;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Tag(name = "Подписка", description = "Управление подпиской и лимитами")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final StripeService stripeService;

    @GetMapping
    @Operation(summary = "Моя подписка", description = "Получить информацию о текущей подписке пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<SubscriptionResponse> getMySubscription(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка - нужно получить из JWT

        Optional<UserSubscription> subscription = subscriptionService.getUserSubscription(userId);

        if (subscription.isPresent()) {
            UserSubscription sub = subscription.get();
            return ResponseEntity.ok(mapToResponse(sub));
        } else {
            // Возвращаем FREE подписку по умолчанию
            return ResponseEntity.ok(SubscriptionResponse.builder()
                    .tier("FREE")
                    .isActive(true)
                    .dailyLimit(20)
                    .build());
        }
    }

    @GetMapping("/usage")
    @Operation(summary = "Статистика использования", description = "Получить статистику использования API за сегодня")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<Map<String, Object>> getUsageStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка

        Map<String, Object> stats = new HashMap<>();
        stats.put("dialogMessages", subscriptionService.getCurrentUsage(userId, "POST:/api/dialog/message"));
        stats.put("dialogMessagesRemaining", subscriptionService.getRemainingLimit(userId, "POST:/api/dialog/message"));
        stats.put("wordExplanations", subscriptionService.getCurrentUsage(userId, "POST:/api/word"));
        stats.put("wordExplanationsRemaining", subscriptionService.getRemainingLimit(userId, "POST:/api/word"));
        stats.put("exercises", subscriptionService.getCurrentUsage(userId, "POST:/api/exercise"));
        stats.put("exercisesRemaining", subscriptionService.getRemainingLimit(userId, "POST:/api/exercise"));

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/upgrade")
    @Operation(summary = "Улучшить подписку", description = "Перейти на Premium подписку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписка активирована"),
    })
    public ResponseEntity<SubscriptionResponse> upgradeSubscription(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpgradeSubscriptionRequest request) {
        Long userId = 1L; // Заглушка

        // В реальной реализации здесь будет интеграция со Stripe
        // Для демо просто создаём подписку

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = request.getTier().equals("LIFETIME") ?
                null : startDate.plusMonths(1);

        UserSubscription.SubscriptionTier tier = UserSubscription.SubscriptionTier.valueOf(request.getTier());

        UserSubscription subscription = subscriptionService.createSubscription(
                userId, tier, startDate, endDate);

        return ResponseEntity.ok(mapToResponse(subscription));
    }

    @PostMapping("/cancel")
    @Operation(summary = "Отменить подписку", description = "Отменить Premium подписку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписка отменена"),
            @ApiResponse(responseCode = "404", description = "Подписка не найдена")
    })
    public ResponseEntity<Void> cancelSubscription(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // Заглушка

        Optional<UserSubscription> subscription = subscriptionService.getUserSubscription(userId);
        if (subscription.isPresent()) {
            subscriptionService.cancelSubscription(subscription.get().getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/checkout")
    @Operation(summary = "Создать Stripe Checkout сессию", description = "Создать ссылку на оплату подписки через Stripe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout сессия создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка создания сессии")
    })
    public ResponseEntity<Map<String, Object>> createCheckoutSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        Long userId = 1L; // Заглушка
        String tier = request.getOrDefault("tier", "PREMIUM");

        try {
            Map<String, Object> session = stripeService.createCheckoutSession(userId, tier);
            return ResponseEntity.ok(session);
        } catch (StripeException e) {
            log.error("Failed to create checkout session: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/plans")
    @Operation(summary = "Тарифные планы", description = "Получить информацию о доступных тарифах")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ")
    })
    public ResponseEntity<Map<String, Object>> getSubscriptionPlans() {
        return ResponseEntity.ok(stripeService.getPlans());
    }

    @GetMapping("/checkout/status/{sessionId}")
    @Operation(summary = "Статус checkout сессии", description = "Проверить статус оплаты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    public ResponseEntity<Map<String, Object>> getCheckoutStatus(@PathVariable String sessionId) {
        try {
            var session = stripeService.retrieveSession(sessionId);
            Map<String, Object> status = new HashMap<>();
            status.put("status", session.getPaymentStatus());
            status.put("amountTotal", session.getAmountTotal());
            status.put("currency", session.getCurrency());
            status.put("customerId", session.getCustomer());
            return ResponseEntity.ok(status);
        } catch (StripeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private SubscriptionResponse mapToResponse(UserSubscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .tier(subscription.getTier().name())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .isActive(subscription.getIsActive())
                .dailyLimit(subscription.isPremium() ? -1 : 20)
                .build();
    }
}
