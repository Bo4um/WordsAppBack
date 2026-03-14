package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ADHD-friendly learning settings and preferences
 */
@Entity
@Table(name = "adhd_learning_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdhdLearningProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column
    @Builder.Default
    private Boolean adhdModeEnabled = false;

    @Column
    private Integer preferredSessionDuration; // 2-5 minutes

    @Column
    @Builder.Default
    private Boolean frequentBreaks = true;

    @Column
    @Builder.Default
    private Integer breakFrequency = 5; // minutes

    @Column
    private String focusMode; // pomodoro, flow, flexible

    @Column
    @Builder.Default
    private Boolean visualReminders = true;

    @Column
    @Builder.Default
    private Boolean gamification = true;

    @Column
    @Builder.Default
    private Boolean gentleReminders = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
