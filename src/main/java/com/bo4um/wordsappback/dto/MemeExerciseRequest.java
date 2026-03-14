package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemeExerciseRequest {
    private Long memeId;
    private String answer;
    private String exerciseType; // "explain", "complete", "translate"
}
