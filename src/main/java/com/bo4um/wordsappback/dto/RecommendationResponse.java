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
public class RecommendationResponse {
    private Long id;
    private String type;
    private String title;
    private String description;
    private String language;
    private String difficulty;
    private Integer priority;

    @JsonProperty("isRead")
    private Boolean isRead;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
