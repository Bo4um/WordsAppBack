package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для теста с вопросами
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestWithQuestionsResponse {

    private Long id;

    private String name;

    private String description;

    private String language;

    private Integer totalQuestions;

    private Integer passingScore;

    private List<TestQuestionResponse> questions;
}
