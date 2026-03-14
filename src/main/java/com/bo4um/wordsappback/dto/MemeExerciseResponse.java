package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemeExerciseResponse {
    private Long memeId;
    private String question;
    private String correctAnswer;
    private String explanation;
    private List<String> vocabularyWords;
    private String culturalContext;

    @JsonProperty("isCorrect")
    private Boolean isCorrect;

    private Integer pointsEarned;
}
