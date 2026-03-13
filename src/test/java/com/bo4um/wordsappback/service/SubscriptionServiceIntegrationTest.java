package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.AbstractIntegrationTest;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserSubscription;
import com.bo4um.wordsappback.repository.UserRepository;
import com.bo4um.wordsappback.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SubscriptionService with PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SubscriptionService Integration Tests")
class SubscriptionServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserSubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Очищаем базу перед каждым тестом
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();

        // Создаём тестового пользователя
        testUser = User.builder()
                .username("testuser_" + System.currentTimeMillis())
                .password("password")
                .role(User.Role.USER)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should create and retrieve subscription")
    @Transactional
    void createAndRetrieveSubscription() {
        // Given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(1);

        // When
        UserSubscription subscription = subscriptionService.createSubscription(
                testUser.getId(),
                UserSubscription.SubscriptionTier.PREMIUM,
                startDate,
                endDate
        );

        // Then
        assertNotNull(subscription.getId());
        assertEquals(UserSubscription.SubscriptionTier.PREMIUM, subscription.getTier());
        assertEquals(testUser.getId(), subscription.getUser().getId());
        assertTrue(subscription.getIsActive());

        // Verify retrieval
        var retrieved = subscriptionService.getUserSubscription(testUser.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(UserSubscription.SubscriptionTier.PREMIUM, retrieved.get().getTier());
    }

    @Test
    @DisplayName("Should return FREE tier when no subscription exists")
    @Transactional
    void getUserTier_NoSubscription() {
        // When
        var tier = subscriptionService.getUserTier(testUser.getId());

        // Then
        assertEquals(UserSubscription.SubscriptionTier.FREE, tier);
        assertFalse(subscriptionService.isPremium(testUser.getId()));
    }

    @Test
    @DisplayName("Should upgrade from FREE to PREMIUM")
    @Transactional
    void upgradeSubscription() {
        // Given - создаём FREE подписку
        UserSubscription freeSub = subscriptionService.createSubscription(
                testUser.getId(),
                UserSubscription.SubscriptionTier.FREE,
                LocalDateTime.now(),
                null
        );

        // When - обновляем до PREMIUM
        String stripeCustomerId = "cus_test123";
        String stripeSubscriptionId = "sub_test456";
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);

        UserSubscription updated = subscriptionService.updateSubscription(
                freeSub.getId(),
                stripeCustomerId,
                stripeSubscriptionId,
                endDate
        );

        // Then
        assertEquals(stripeCustomerId, updated.getStripeCustomerId());
        assertEquals(stripeSubscriptionId, updated.getStripeSubscriptionId());
        assertEquals(endDate, updated.getEndDate());
    }

    @Test
    @DisplayName("Should cancel subscription")
    @Transactional
    void cancelSubscription() {
        // Given
        UserSubscription subscription = subscriptionService.createSubscription(
                testUser.getId(),
                UserSubscription.SubscriptionTier.PREMIUM,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1)
        );

        // When
        subscriptionService.cancelSubscription(subscription.getId());

        // Then
        var retrieved = subscriptionService.getUserSubscription(testUser.getId());
        assertTrue(retrieved.isPresent());
        assertFalse(retrieved.get().getIsActive());
    }

    @Test
    @DisplayName("Should track API usage")
    @Transactional
    void trackApiUsage() {
        // Given
        String endpoint = "POST:/api/dialog/message";

        // When - записываем использование
        subscriptionService.recordUsage(testUser.getId(), endpoint);
        subscriptionService.recordUsage(testUser.getId(), endpoint);
        subscriptionService.recordUsage(testUser.getId(), endpoint);

        // Then
        int usage = subscriptionService.getCurrentUsage(testUser.getId(), endpoint);
        assertEquals(3, usage);

        int remaining = subscriptionService.getRemainingLimit(testUser.getId(), endpoint);
        // FREE tier limit for dialog is 20
        assertEquals(17, remaining);
    }

    @Test
    @DisplayName("Should detect rate limit exceeded")
    @Transactional
    void rateLimitExceeded() {
        // Given - FREE tier с лимитом 20
        String endpoint = "POST:/api/dialog/message";

        // When - превышаем лимит
        for (int i = 0; i < 25; i++) {
            subscriptionService.recordUsage(testUser.getId(), endpoint);
        }

        // Then
        boolean exceeded = subscriptionService.isRateLimitExceeded(testUser.getId(), endpoint);
        assertTrue(exceeded);

        int remaining = subscriptionService.getRemainingLimit(testUser.getId(), endpoint);
        assertEquals(0, remaining);
    }

    @Test
    @DisplayName("Should not have rate limit for PREMIUM")
    @Transactional
    void noRateLimitForPremium() {
        // Given - PREMIUM подписка
        subscriptionService.createSubscription(
                testUser.getId(),
                UserSubscription.SubscriptionTier.PREMIUM,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1)
        );

        String endpoint = "POST:/api/dialog/message";

        // When - много запросов
        for (int i = 0; i < 100; i++) {
            subscriptionService.recordUsage(testUser.getId(), endpoint);
        }

        // Then - лимита нет
        boolean exceeded = subscriptionService.isRateLimitExceeded(testUser.getId(), endpoint);
        assertFalse(exceeded);

        int remaining = subscriptionService.getRemainingLimit(testUser.getId(), endpoint);
        assertEquals(Integer.MAX_VALUE, remaining);
    }

    @Test
    @DisplayName("Should handle LIFETIME subscription")
    @Transactional
    void lifetimeSubscription() {
        // Given
        UserSubscription lifetimeSub = subscriptionService.createSubscription(
                testUser.getId(),
                UserSubscription.SubscriptionTier.LIFETIME,
                LocalDateTime.now(),
                null // No end date
        );

        // Then
        assertFalse(lifetimeSub.hasExpired());
        assertTrue(lifetimeSub.isPremium());

        // Even after "end date" check, LIFETIME should not expire
        var retrieved = subscriptionService.getUserSubscription(testUser.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(UserSubscription.SubscriptionTier.LIFETIME, retrieved.get().getTier());
    }
}
