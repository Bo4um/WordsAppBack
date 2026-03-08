package com.bo4um.wordsappback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Прогресс пользователя: streak, общая статистика
 */
@Entity
@Table(name = "user_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Текущая серия дней подряд
     */
    @Builder.Default
    private Integer currentStreak = 0;

    /**
     * Самая длинная серия
     */
    @Builder.Default
    private Integer longestStreak = 0;

    /**
     * Дата последнего визита
     */
    private LocalDate lastVisitDate;

    /**
     * Общее количество изученных слов
     */
    @Builder.Default
    private Integer totalWordsLearned = 0;

    /**
     * Дата регистрации
     */
    private LocalDate joinDate;
}
