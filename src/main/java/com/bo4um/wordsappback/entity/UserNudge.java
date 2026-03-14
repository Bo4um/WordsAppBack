package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Personalized nudge/notification for user engagement
 */
@Entity
@Table(name = "user_nudge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNudge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String nudgeType; // reminder, encouragement, challenge, tip

    @Column(nullable = false, length = 500)
    private String message;

    @Column
    private String context; // time_based, behavior_based, milestone

    @Column
    @Builder.Default
    private Boolean isRead = false;

    @Column
    @Builder.Default
    private Boolean isActioned = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime readAt;

    @Column
    private LocalDateTime actionedAt;
}
