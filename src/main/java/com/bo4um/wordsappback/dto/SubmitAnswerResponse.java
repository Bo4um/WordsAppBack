package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerResponse {
    private Long exerciseId;

    @JsonProperty("isCorrect")
    private Boolean isCorrect;

    private String userAnswer;
    private String correctAnswer;
    private String explanation;
    private Integer points;
}
