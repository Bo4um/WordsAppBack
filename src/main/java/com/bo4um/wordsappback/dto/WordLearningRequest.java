package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для отметки слова как изученного
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordLearningRequest {

    /**
     * Слово или фраза
     */
    private String word;

    /**
     * Язык слова
     */
    private String language;
}
