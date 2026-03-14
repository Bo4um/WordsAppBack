package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
@Hidden // Hide from Swagger - webhook endpoint
public class StripeWebhookController {

    private final StripeService stripeService;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        if (webhookSecret == null || webhookSecret.isEmpty()) {
            log.warn("Webhook secret not configured - skipping signature verification");
            return handleEvent(payload);
        }

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            log.info("Received Stripe event: {}", event.getType());

            return handleEvent(event);

        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature: {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid signature");
        }
    }

    private ResponseEntity<String> handleEvent(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        
        if (dataObjectDeserializer.getObject().isEmpty()) {
            log.warn("Empty event data object");
            return ResponseEntity.ok("OK");
        }

        StripeObject stripeObject = dataObjectDeserializer.getObject().get();

        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) stripeObject;
                log.info("Payment completed for session: {}", session.getId());
                stripeService.handlePaymentCompleted(session);
                break;

            case "customer.subscription.deleted":
                // Handle subscription cancellation
                com.stripe.model.Subscription subscription = 
                    (com.stripe.model.Subscription) stripeObject;
                log.info("Subscription cancelled: {}", subscription.getId());
                stripeService.handleSubscriptionCancelled(subscription.getId());
                break;

            case "invoice.payment_failed":
                // Handle payment failure - could downgrade user to FREE
                log.warn("Payment failed: {}", event.getId());
                break;

            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("OK");
    }

    /**
     * Fallback handler for testing without signature
     */
    private ResponseEntity<String> handleEvent(String payload) {
        // In production, always verify signature
        log.info("Received webhook (no signature verification)");
        return ResponseEntity.ok("OK");
    }
}
