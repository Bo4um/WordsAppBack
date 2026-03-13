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
public class DialogSessionResponse {
    private Long id;
    private Long scenarioId;
    private String scenarioTitle;
    private Long characterId;
    private String characterName;
    private String topic;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private boolean active;

    @JsonProperty("isActive")
    public boolean isActive() {
        return active;
    }
}
