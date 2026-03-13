package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscription")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionTier tier;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean isActive;

    private String stripeCustomerId;

    private String stripeSubscriptionId;

    public enum SubscriptionTier {
        FREE,
        PREMIUM,
        LIFETIME
    }

    public boolean isPremium() {
        return tier == SubscriptionTier.PREMIUM || tier == SubscriptionTier.LIFETIME;
    }

    public boolean hasExpired() {
        if (tier == SubscriptionTier.LIFETIME) {
            return false;
        }
        return endDate != null && endDate.isBefore(LocalDateTime.now());
    }
}
