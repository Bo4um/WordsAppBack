package com.bo4um.wordsappback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Результат прохождения теста пользователем
 */
@Entity
@Table(name = "user_test_result")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private LanguageTest test;

    /**
     * Набранные баллы
     */
    private Integer score;

    /**
     * Максимальный балл
     */
    private Integer maxScore;

    /**
     * Определённый уровень (A1, A2, B1, B2, C1, C2)
     */
    private String determinedLevel;

    /**
     * Процент правильных ответов
     */
    private Integer percentage;

    /**
     * Дата завершения теста
     */
    @Builder.Default
    private LocalDateTime completedAt = LocalDateTime.now();

    /**
     * Ответы пользователя (JSON формат: {"1": "A", "2": "B", ...})
     */
    @Column(length = 2000)
    private String answers;
}
