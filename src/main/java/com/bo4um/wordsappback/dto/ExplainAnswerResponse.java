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
public class ExplainAnswerResponse {
    private String explanation;
    private String grammarRule;
    private List<String> grammarTips;
    private String correctedSentence;
    private List<String> examples;

    @JsonProperty("isCorrect")
    private Boolean isCorrect;

    private String confidence; // HIGH, MEDIUM, LOW
}
