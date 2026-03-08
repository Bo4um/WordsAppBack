package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для отображения прогресса словаря
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryProgressResponse {

    /**
     * ID прогресса
     */
    private Long id;

    /**
     * Название словаря (язык)
     */
    private String dictionaryName;

    /**
     * Количество изученных слов
     */
    private Integer wordsLearned;

    /**
     * Общее количество слов в словаре
     */
    private Integer totalWords;

    /**
     * Процент изучения
     */
    private Integer progressPercentage;

    /**
     * Дата последнего обновления
     */
    private LocalDateTime lastUpdated;
}
