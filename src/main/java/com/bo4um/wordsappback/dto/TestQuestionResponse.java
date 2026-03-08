package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для вопроса теста
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionResponse {

    private Long id;

    private String questionText;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String level;

    private Integer points;

    private Integer orderNumber;
}
