package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для отображения прогресса пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressResponse {

    /**
     * ID прогресса
     */
    private Long id;

    /**
     * Текущая серия дней подряд
     */
    private Integer currentStreak;

    /**
     * Самая длинная серия
     */
    private Integer longestStreak;

    /**
     * Дата последнего визита
     */
    private LocalDate lastVisitDate;

    /**
     * Общее количество изученных слов
     */
    private Integer totalWordsLearned;

    /**
     * Дата регистрации
     */
    private LocalDate joinDate;
}
