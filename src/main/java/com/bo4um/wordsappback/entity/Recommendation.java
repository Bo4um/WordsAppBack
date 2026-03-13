package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Recommendation for user - suggests what to learn next
 */
@Entity
@Table(name = "recommendation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationType type;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column
    private String language;

    @Column
    private String difficulty;

    @Column
    private Integer priority; // 1 = highest priority

    @Column
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum RecommendationType {
        REVIEW_WORDS,           // Время повторить слова
        NEW_EXERCISE,           // Новое упражнение доступно
        CONTINUE_SCENARIO,      // Продолжить диалог
        WEAK_SKILL,             // Слабый навык (грамматика/словарь)
        DAILY_GOAL,             // Дневная цель
        ACHIEVEMENT,            // Достижение
        STREAK_MAINTENANCE      // Поддержание streak
    }
}
