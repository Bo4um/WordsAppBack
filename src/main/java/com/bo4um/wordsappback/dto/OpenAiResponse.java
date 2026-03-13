package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class OpenAiResponse {

    private List<Output> output;

    @Data
@Builder
    public static class Output {
        private String role;
        private List<Content> content;
    }

    @Data
@Builder
    public static class Content {
        private String type;
        private String text;
    }
}
