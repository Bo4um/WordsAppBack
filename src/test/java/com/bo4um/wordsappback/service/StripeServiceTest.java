package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.UserSubscription;
import com.bo4um.wordsappback.repository.UserSubscriptionRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StripeService Unit Tests")
class StripeServiceTest {

    @Mock
    private UserSubscriptionRepository subscriptionRepository;

    @InjectMocks
    private StripeService stripeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripeService, "stripeApiKey", "sk_test_123");
        ReflectionTestUtils.setField(stripeService, "premiumPriceId", "price_premium");
        ReflectionTestUtils.setField(stripeService, "lifetimePriceId", "price_lifetime");
        ReflectionTestUtils.setField(stripeService, "frontendUrl", "http://localhost:3000");
    }

    @Test
    @DisplayName("Should get subscription plans")
    void getPlans_Success() {
        // When
        Map<String, Object> result = stripeService.getPlans();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("premium"));
        assertTrue(result.containsKey("lifetime"));

        @SuppressWarnings("unchecked")
        Map<String, Object> premium = (Map<String, Object>) result.get("premium");
        assertEquals("Premium", premium.get("name"));
        assertEquals("$9.99", premium.get("price"));
    }

    @Test
    @DisplayName("Should handle payment completed - new subscription")
    void handlePaymentCompleted_NewSubscription() {
        // Given
        Session session = mock(Session.class);
        when(session.getMetadata()).thenReturn(Map.of(
                "user_id", "1",
                "tier", "PREMIUM"
        ));
        when(session.getSubscription()).thenReturn("sub_123");
        when(session.getCustomer()).thenReturn("cus_123");

        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(UserSubscription.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        stripeService.handlePaymentCompleted(session);

        // Then
        verify(subscriptionRepository).save(any(UserSubscription.class));
    }

    @Test
    @DisplayName("Should handle payment completed - update existing")
    void handlePaymentCompleted_UpdateExisting() {
        // Given
        Session session = mock(Session.class);
        when(session.getMetadata()).thenReturn(Map.of(
                "user_id", "1",
                "tier", "PREMIUM"
        ));
        when(session.getSubscription()).thenReturn("sub_123");
        when(session.getCustomer()).thenReturn("cus_123");

        UserSubscription existingSub = UserSubscription.builder()
                .id(1L)
                .tier(UserSubscription.SubscriptionTier.FREE)
                .isActive(true)
                .build();

        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(existingSub));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenReturn(existingSub);

        // When
        stripeService.handlePaymentCompleted(session);

        // Then
        verify(subscriptionRepository).save(existingSub);
        assertEquals(UserSubscription.SubscriptionTier.PREMIUM, existingSub.getTier());
    }

    @Test
    @DisplayName("Should handle missing metadata gracefully")
    void handlePaymentCompleted_MissingMetadata() {
        // Given
        Session session = mock(Session.class);
        when(session.getMetadata()).thenReturn(new HashMap<>());

        // When
        stripeService.handlePaymentCompleted(session);

        // Then
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle subscription cancellation")
    void handleSubscriptionCancelled_Success() {
        // Given
        UserSubscription subscription = UserSubscription.builder()
                .id(1L)
                .tier(UserSubscription.SubscriptionTier.PREMIUM)
                .isActive(true)
                .stripeSubscriptionId("sub_123")
                .build();

        when(subscriptionRepository.findAll()).thenReturn(java.util.Arrays.asList(subscription));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenReturn(subscription);

        // When
        stripeService.handleSubscriptionCancelled("sub_123");

        // Then
        verify(subscriptionRepository).save(subscription);
        assertFalse(subscription.getIsActive());
    }
}
