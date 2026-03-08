package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO для отображения изученного слова
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordLearningResponse {

    /**
     * ID записи
     */
    private Long id;

    /**
     * Слово
     */
    private String word;

    /**
     * Язык
     */
    private String language;

    /**
     * Дата изучения
     */
    private LocalDateTime learnedAt;

    /**
     * Количество повторений
     */
    private Integer repetitions;

    /**
     * Дата следующего повторения
     */
    private LocalDate nextReview;
}
