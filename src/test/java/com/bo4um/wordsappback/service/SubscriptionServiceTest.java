package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.ApiUsageStats;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserSubscription;
import com.bo4um.wordsappback.repository.ApiUsageStatsRepository;
import com.bo4um.wordsappback.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService Unit Tests")
class SubscriptionServiceTest {

    @Mock
    private UserSubscriptionRepository subscriptionRepository;

    @Mock
    private ApiUsageStatsRepository usageStatsRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private UserSubscription freeSubscription;
    private UserSubscription premiumSubscription;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role(User.Role.USER)
                .build();

        freeSubscription = UserSubscription.builder()
                .id(1L)
                .user(testUser)
                .tier(UserSubscription.SubscriptionTier.FREE)
                .startDate(LocalDateTime.now())
                .isActive(true)
                .build();

        premiumSubscription = UserSubscription.builder()
                .id(2L)
                .user(testUser)
                .tier(UserSubscription.SubscriptionTier.PREMIUM)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should get user subscription")
    void getUserSubscription_Success() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(freeSubscription));

        // When
        Optional<UserSubscription> result = subscriptionService.getUserSubscription(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(UserSubscription.SubscriptionTier.FREE, result.get().getTier());
        verify(subscriptionRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should return FREE tier when no subscription exists")
    void getUserTier_NoSubscription_ReturnsFree() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // When
        UserSubscription.SubscriptionTier result = subscriptionService.getUserTier(1L);

        // Then
        assertEquals(UserSubscription.SubscriptionTier.FREE, result);
    }

    @Test
    @DisplayName("Should return PREMIUM tier when subscription exists")
    void getUserTier_WithPremiumSubscription_ReturnsPremium() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(premiumSubscription));

        // When
        UserSubscription.SubscriptionTier result = subscriptionService.getUserTier(1L);

        // Then
        assertEquals(UserSubscription.SubscriptionTier.PREMIUM, result);
    }

    @Test
    @DisplayName("Should return true for premium user")
    void isPremium_WithPremiumSubscription_ReturnsTrue() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(premiumSubscription));

        // When
        boolean result = subscriptionService.isPremium(1L);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for free user")
    void isPremium_WithFreeSubscription_ReturnsFalse() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(freeSubscription));

        // When
        boolean result = subscriptionService.isPremium(1L);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should not exceed rate limit for premium user")
    void isRateLimitExceeded_PremiumUser_ReturnsFalse() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(premiumSubscription));

        // When
        boolean result = subscriptionService.isRateLimitExceeded(1L, "POST:/api/dialog/message");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should exceed rate limit for free user")
    void isRateLimitExceeded_FreeUser_LimitExceeded() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(freeSubscription));
        when(usageStatsRepository.findByUserIdAndEndpointAndUsageDate(
                eq(1L), anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(ApiUsageStats.builder()
                        .requestCount(20) // FREE limit for dialog
                        .build()));

        // When
        boolean result = subscriptionService.isRateLimitExceeded(1L, "POST:/api/dialog/message");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not exceed rate limit for free user within limits")
    void isRateLimitExceeded_FreeUser_WithinLimit() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(freeSubscription));
        when(usageStatsRepository.findByUserIdAndEndpointAndUsageDate(
                eq(1L), anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(ApiUsageStats.builder()
                        .requestCount(5) // Within limit
                        .build()));

        // When
        boolean result = subscriptionService.isRateLimitExceeded(1L, "POST:/api/dialog/message");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should record API usage - new record")
    void recordUsage_NewRecord() {
        // Given
        LocalDate today = LocalDate.now();
        when(usageStatsRepository.findByUserIdAndEndpointAndUsageDate(1L, "POST:/api/test", today))
                .thenReturn(Optional.empty());

        // When
        subscriptionService.recordUsage(1L, "POST:/api/test");

        // Then
        verify(usageStatsRepository).save(any(ApiUsageStats.class));
    }

    @Test
    @DisplayName("Should record API usage - increment existing")
    void recordUsage_ExistingRecord() {
        // Given
        LocalDate today = LocalDate.now();
        when(usageStatsRepository.findByUserIdAndEndpointAndUsageDate(1L, "POST:/api/test", today))
                .thenReturn(Optional.of(ApiUsageStats.builder()
                        .requestCount(1)
                        .build()));

        // When
        subscriptionService.recordUsage(1L, "POST:/api/test");

        // Then
        verify(usageStatsRepository).incrementCount(1L, "POST:/api/test", today);
    }

    @Test
    @DisplayName("Should get current usage")
    void getCurrentUsage_Success() {
        // Given
        LocalDate today = LocalDate.now();
        when(usageStatsRepository.findByUserIdAndEndpointAndUsageDate(1L, "POST:/api/test", today))
                .thenReturn(Optional.of(ApiUsageStats.builder()
                        .requestCount(5)
                        .build()));

        // When
        int result = subscriptionService.getCurrentUsage(1L, "POST:/api/test");

        // Then
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Should get remaining limit")
    void getRemainingLimit_Success() {
        // Given
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(freeSubscription));
        when(usageStatsRepository.findByUserIdAndEndpointAndUsageDate(
                eq(1L), anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(ApiUsageStats.builder()
                        .requestCount(10)
                        .build()));

        // When
        int result = subscriptionService.getRemainingLimit(1L, "POST:/api/dialog/message");

        // Then
        assertEquals(10, result); // 20 - 10 = 10
    }

    @Test
    @DisplayName("Should create subscription")
    void createSubscription_Success() {
        // Given
        when(subscriptionRepository.save(any(UserSubscription.class)))
                .thenReturn(premiumSubscription);

        // When
        UserSubscription result = subscriptionService.createSubscription(
                1L,
                UserSubscription.SubscriptionTier.PREMIUM,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1)
        );

        // Then
        assertNotNull(result);
        assertEquals(UserSubscription.SubscriptionTier.PREMIUM, result.getTier());
        verify(subscriptionRepository).save(any(UserSubscription.class));
    }

    @Test
    @DisplayName("Should cancel subscription")
    void cancelSubscription_Success() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(freeSubscription));

        // When
        subscriptionService.cancelSubscription(1L);

        // Then
        verify(subscriptionRepository).save(freeSubscription);
        assertFalse(freeSubscription.getIsActive());
    }
}
