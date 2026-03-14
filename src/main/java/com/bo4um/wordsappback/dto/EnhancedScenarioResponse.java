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
public class EnhancedScenarioResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String language;
    private String difficulty;
    private Integer estimatedDuration;
    private String learningObjectives;
    private List<String> emotions;
    private Integer completionCount;
    private Double averageRating;

    @JsonProperty("isActive")
    private Boolean isActive;
}
