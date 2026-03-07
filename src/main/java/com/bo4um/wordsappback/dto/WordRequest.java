package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for word lookup API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordRequest {

    /**
     * Word or phrase to look up
     */
    private String input;

    /**
     * Target language for definitions and translations (e.g., "Russian", "Spanish")
     */
    @JsonProperty("def_language")
    private String defLanguage;

    /**
     * Character sex: "male" or "female"
     */
    @JsonProperty("character_sex")
    private String characterSex;

    /**
     * Character name for examples
     */
    @JsonProperty("character_name")
    private String characterName;

    /**
     * Style of response: "Normal" or "Creative"
     */
    private String style;

    /**
     * Get default style if not specified
     */
    public String getStyle() {
        return style != null ? style : "Normal";
    }
}
