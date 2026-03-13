package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private String type;
    private Integer targetValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer rewardPoints;

    @JsonProperty("isActive")
    private Boolean isActive;
}
