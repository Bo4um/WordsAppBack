package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateExerciseRequest {
    private String language;
    private String difficulty; // A1, A2, B1, B2, C1, C2
    private String exerciseType; // FILL_IN_BLANK, TRANSLATION, etc.
    private String topic; // тема для упражнения (опционально)
    private Integer count; // количество упражнений (по умолчанию 5)
}
