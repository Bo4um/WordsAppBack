package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Prompt prompt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<InputMessage> input;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Text text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Reasoning reasoning;

    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean store;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> include;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prompt {
        private String id;
        private String version;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, String> variables;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputMessage {
        private String role;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<Content> content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        @JsonProperty("type")
        private String type;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Text {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Format format;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Format {
        private String type;
        private String name;
        private Boolean strict;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JsonSchema schema;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JsonSchema {
        private String type;

        @JsonProperty("properties")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Property> properties;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> required;

        @JsonProperty("additionalProperties")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean additionalProperties;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Property {
        private String type;

        @JsonProperty("description")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String description;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String pattern;

        @JsonProperty("enum")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> enums;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Items items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Items {
        private String type;

        @JsonProperty("properties")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, Property> properties;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> required;

        @JsonProperty("additionalProperties")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean additionalProperties;
    }

    @Data
    public static class Reasoning {
    }
}
