package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.ApiUsageStats;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserSubscription;
import com.bo4um.wordsappback.repository.ApiUsageStatsRepository;
import com.bo4um.wordsappback.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;
    private final ApiUsageStatsRepository usageStatsRepository;

    // Лимиты для FREE tier (в день)
    private static final int DIALOG_MESSAGES_FREE_LIMIT = 20;
    private static final int WORD_EXPLANATIONS_FREE_LIMIT = 10;
    private static final int EXERCISES_FREE_LIMIT = 5;

    @Transactional(readOnly = true)
    public Optional<UserSubscription> getUserSubscription(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserSubscription.SubscriptionTier getUserTier(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .map(UserSubscription::getTier)
                .orElse(UserSubscription.SubscriptionTier.FREE);
    }

    @Transactional(readOnly = true)
    public boolean isPremium(Long userId) {
        return getUserTier(userId) != UserSubscription.SubscriptionTier.FREE;
    }

    /**
     * Проверка лимита использования API
     * @return true если лимит превышен
     */
    @Transactional
    public boolean isRateLimitExceeded(Long userId, String endpoint) {
        UserSubscription.SubscriptionTier tier = getUserTier(userId);
        int limit = getLimitForEndpoint(endpoint, tier);

        if (limit == -1) { // Безлимитно для PREMIUM
            return false;
        }

        int currentCount = getCurrentUsage(userId, endpoint);
        return currentCount >= limit;
    }

    /**
     * Запись использования API
     */
    @Transactional
    public void recordUsage(Long userId, String endpoint) {
        LocalDate today = LocalDate.now();

        Optional<ApiUsageStats> existingStats = usageStatsRepository
                .findByUserIdAndEndpointAndUsageDate(userId, endpoint, today);

        if (existingStats.isPresent()) {
            usageStatsRepository.incrementCount(userId, endpoint, today);
        } else {
            ApiUsageStats stats = new ApiUsageStats();
            stats.setUser(createUserWithId(userId));
            stats.setEndpoint(endpoint);
            stats.setUsageDate(today);
            stats.setRequestCount(1);

            usageStatsRepository.save(stats);
        }

        log.debug("Recorded API usage: user={}, endpoint={}, date={}", userId, endpoint, today);
    }

    /**
     * Получение текущей статистики использования
     */
    @Transactional(readOnly = true)
    public int getCurrentUsage(Long userId, String endpoint) {
        LocalDate today = LocalDate.now();

        return usageStatsRepository
                .findByUserIdAndEndpointAndUsageDate(userId, endpoint, today)
                .map(ApiUsageStats::getRequestCount)
                .orElse(0);
    }

    /**
     * Получение оставшегося лимита
     */
    @Transactional(readOnly = true)
    public int getRemainingLimit(Long userId, String endpoint) {
        UserSubscription.SubscriptionTier tier = getUserTier(userId);
        int limit = getLimitForEndpoint(endpoint, tier);

        if (limit == -1) {
            return Integer.MAX_VALUE; // Безлимитно
        }

        int currentUsage = getCurrentUsage(userId, endpoint);
        return Math.max(0, limit - currentUsage);
    }

    /**
     * Создание подписки для пользователя
     */
    @Transactional
    public UserSubscription createSubscription(Long userId, UserSubscription.SubscriptionTier tier,
                                                LocalDateTime startDate, LocalDateTime endDate) {
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(createUserWithId(userId));
        subscription.setTier(tier);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setIsActive(true);

        return subscriptionRepository.save(subscription);
    }

    /**
     * Обновление подписки (например, после оплаты через Stripe)
     */
    @Transactional
    public UserSubscription updateSubscription(Long subscriptionId, String stripeCustomerId,
                                                String stripeSubscriptionId, LocalDateTime endDate) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (stripeCustomerId != null) {
            subscription.setStripeCustomerId(stripeCustomerId);
        }
        if (stripeSubscriptionId != null) {
            subscription.setStripeSubscriptionId(stripeSubscriptionId);
        }
        if (endDate != null) {
            subscription.setEndDate(endDate);
        }

        return subscriptionRepository.save(subscription);
    }

    /**
     * Отмена подписки
     */
    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.setIsActive(false);
        subscriptionRepository.save(subscription);
    }

    private int getLimitForEndpoint(String endpoint, UserSubscription.SubscriptionTier tier) {
        if (tier == UserSubscription.SubscriptionTier.PREMIUM ||
            tier == UserSubscription.SubscriptionTier.LIFETIME) {
            return -1; // Безлимитно для premium
        }

        // Лимиты для FREE tier
        if (endpoint.contains("/dialog/")) {
            return DIALOG_MESSAGES_FREE_LIMIT;
        } else if (endpoint.contains("/word")) {
            return WORD_EXPLANATIONS_FREE_LIMIT;
        } else if (endpoint.contains("/exercise")) {
            return EXERCISES_FREE_LIMIT;
        }

        return Integer.MAX_VALUE; // Остальные эндпоинты без лимита
    }

    private User createUserWithId(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}
