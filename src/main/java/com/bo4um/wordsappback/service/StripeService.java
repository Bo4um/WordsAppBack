package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.UserSubscription;
import com.bo4um.wordsappback.repository.UserSubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.api.key:}")
    private String stripeApiKey;

    @Value("${stripe.price.id.premium:}")
    private String premiumPriceId;

    @Value("${stripe.price.id.lifetime:}")
    private String lifetimePriceId;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private final UserSubscriptionRepository subscriptionRepository;

    @PostConstruct
    public void init() {
        if (stripeApiKey != null && !stripeApiKey.isEmpty()) {
            Stripe.apiKey = stripeApiKey;
            log.info("Stripe initialized successfully");
        } else {
            log.warn("Stripe API key not configured - payments will not work");
        }
    }

    /**
     * Create Stripe Checkout Session for subscription
     */
    public Map<String, Object> createCheckoutSession(Long userId, String tier) throws StripeException {
        String priceId = UserSubscription.SubscriptionTier.PREMIUM.name().equalsIgnoreCase(tier) 
            ? premiumPriceId 
            : lifetimePriceId;

        String successUrl = frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = frontendUrl + "/payment/cancel";

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build())
                .putMetadata("user_id", userId.toString())
                .putMetadata("tier", tier);

        SessionCreateParams params = paramsBuilder.build();

        Session session = Session.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("url", session.getUrl());
        response.put("expiresAt", session.getExpiresAt());

        log.info("Created checkout session for user {}: {}", userId, session.getId());

        return response;
    }

    /**
     * Retrieve checkout session details
     */
    public Session retrieveSession(String sessionId) throws StripeException {
        SessionRetrieveParams params = SessionRetrieveParams.builder()
                .addExpand("line_items")
                .addExpand("payment_intent")
                .build();

        return Session.retrieve(sessionId, params, null);
    }

    /**
     * Handle Stripe webhook - payment completed
     */
    @Transactional
    public void handlePaymentCompleted(Session session) {
        String userIdStr = session.getMetadata().get("user_id");
        String tier = session.getMetadata().get("tier");

        if (userIdStr == null || tier == null) {
            log.error("Missing metadata in checkout session: {}", session.getId());
            return;
        }

        Long userId = Long.parseLong(userIdStr);
        UserSubscription.SubscriptionTier subscriptionTier = UserSubscription.SubscriptionTier.valueOf(tier);

        // Find existing subscription or create new one
        Optional<UserSubscription> existingSub = subscriptionRepository.findByUserId(userId);

        if (existingSub.isPresent()) {
            // Update existing subscription
            UserSubscription subscription = existingSub.get();
            subscription.setTier(subscriptionTier);
            subscription.setIsActive(true);
            subscription.setStripeSubscriptionId(session.getSubscription());
            subscription.setStripeCustomerId(session.getCustomer());

            if (subscriptionTier != UserSubscription.SubscriptionTier.LIFETIME) {
                // Set end date for non-lifetime subscriptions (1 month from now)
                subscription.setStartDate(LocalDateTime.now());
                subscription.setEndDate(LocalDateTime.now().plusMonths(1));
            }

            subscriptionRepository.save(subscription);
            log.info("Updated subscription for user {}: {}", userId, subscription.getId());

        } else {
            // Create new subscription
            UserSubscription newSubscription = UserSubscription.builder()
                    .user(com.bo4um.wordsappback.entity.User.builder().id(userId).build())
                    .tier(subscriptionTier)
                    .startDate(LocalDateTime.now())
                    .isActive(true)
                    .stripeSubscriptionId(session.getSubscription())
                    .stripeCustomerId(session.getCustomer())
                    .build();

            subscriptionRepository.save(newSubscription);
            log.info("Created new subscription for user {}: {}", userId, newSubscription.getId());
        }
    }

    /**
     * Handle subscription cancellation webhook
     */
    @Transactional
    public void handleSubscriptionCancelled(String stripeSubscriptionId) {
        Optional<UserSubscription> subscriptionOpt = subscriptionRepository.findAll().stream()
                .filter(s -> stripeSubscriptionId.equals(s.getStripeSubscriptionId()))
                .findFirst();

        if (subscriptionOpt.isPresent()) {
            UserSubscription subscription = subscriptionOpt.get();
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
            log.info("Cancelled subscription: {}", subscription.getId());
        }
    }

    /**
     * Get subscription plans info
     */
    public Map<String, Object> getPlans() {
        Map<String, Object> plans = new HashMap<>();

        Map<String, Object> premium = new HashMap<>();
        premium.put("id", "premium");
        premium.put("name", "Premium");
        premium.put("price", "$9.99");
        premium.put("interval", "month");
        premium.put("features", java.util.Arrays.asList(
                "Unlimited AI dialogs",
                "Unlimited word explanations",
                "Unlimited exercises",
                "Pronunciation analysis",
                "No ads"
        ));

        Map<String, Object> lifetime = new HashMap<>();
        lifetime.put("id", "lifetime");
        lifetime.put("name", "Lifetime");
        lifetime.put("price", "$399");
        lifetime.put("interval", "one-time");
        lifetime.put("features", java.util.Arrays.asList(
                "All Premium features",
                "Lifetime access",
                "Priority support",
                "Early access to new features"
        ));

        plans.put("premium", premium);
        plans.put("lifetime", lifetime);

        return plans;
    }
}
