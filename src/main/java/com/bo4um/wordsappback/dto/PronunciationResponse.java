package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PronunciationResponse {
    private Long id;
    private String targetPhrase;
    private String recognizedText;
    private Integer accuracyScore;
    private String feedback;

    @JsonProperty("isGood")
    private Boolean isGood; // true if accuracy >= 80

    private String status;
    private LocalDateTime attemptedAt;
}
