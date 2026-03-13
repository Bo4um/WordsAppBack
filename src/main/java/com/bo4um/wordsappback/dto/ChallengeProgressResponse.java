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
public class ChallengeProgressResponse {
    private Long challengeId;
    private String challengeTitle;
    private String challengeType;
    private Integer targetValue;
    private Integer currentValue;
    private Integer progressPercentage;

    @JsonProperty("isCompleted")
    private Boolean isCompleted;

    @JsonProperty("rewardClaimed")
    private Boolean rewardClaimed;

    private LocalDate endDate;
}
