package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для информации о тесте
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageTestResponse {

    private Long id;

    private String name;

    private String description;

    private String language;

    private Integer totalQuestions;

    private Integer passingScore;

    private Boolean isActive;
}
