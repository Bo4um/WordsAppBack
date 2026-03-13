package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainAnswerRequest {
    private String userAnswer;
    private String correctAnswer;
    private String language;
    private String context; // контекст вопроса (например, полное предложение)
    private String questionType; // grammar, vocabulary, translation и т.д.
}
