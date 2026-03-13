package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioResponse {
    private Long id;
    private String title;
    private String description;
    private String language;
    private String difficulty;
    private String characterName;

    @JsonProperty("characterSex")
    private String characterSex;
}
