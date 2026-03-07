package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO representing the parsed response from OpenAI API
 * Contains word meanings with definitions and translations
 */
@Data
public class WordMeaningResponse {

    private String input;

    @JsonProperty("output_language")
    private String outputLanguage;

    private String type;

    private List<Meaning> meanings;

    @Data
    public static class Meaning {
        private String definition;
        private String level;

        @JsonProperty("example_input")
        private String exampleInput;

        @JsonProperty("example_output")
        private String exampleOutput;
    }
}
