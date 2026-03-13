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
public class SubscriptionResponse {
    private Long id;
    private String tier; // FREE, PREMIUM, LIFETIME

    @JsonProperty("isActive")
    private Boolean isActive;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @JsonProperty("dailyLimit")
    private Integer dailyLimit; // -1 для безлимита
}
