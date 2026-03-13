package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {
    private Long id;
    private String type;
    private String question;
    private String hint;
    private String explanation;
    private String language;
    private String difficulty;

    @JsonProperty("isCompleted")
    private Boolean isCompleted;

    private LocalDateTime createdAt;

    // Для множественного выбора
    private List<String> options;
}
