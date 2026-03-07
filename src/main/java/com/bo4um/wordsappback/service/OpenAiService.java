package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.config.OpenAiProperties;
import com.bo4um.wordsappback.dto.OpenAiRequest;
import com.bo4um.wordsappback.dto.OpenAiResponse;
import com.bo4um.wordsappback.dto.WordMeaningResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final WebClient webClient;
    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;

    /**
     * Get word meaning and translation from OpenAI API
     * Results are cached for 24 hours
     *
     * @param input          the word or phrase to look up
     * @param targetLanguage the language for translation
     * @param characterSex   character sex: "male" or "female"
     * @param characterName  character name for examples
     * @param style          response style (optional)
     * @return WordMeaningResponse with definitions and translations
     */
    @Cacheable(value = "wordCache", key = "'v3:' + #input + ':' + #targetLanguage + ':' + #characterSex + ':' + #characterName + ':' + #style")
    public WordMeaningResponse getWordMeaning(String input, String targetLanguage, String characterSex, String characterName, String style) {
        log.info("Fetching meaning for input: '{}' in language: {}, character: {} ({})", input, targetLanguage, characterName, characterSex);

        OpenAiRequest request = buildRequest(input, targetLanguage, characterSex, characterName, style);

        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            log.debug("OpenAI Request JSON: {}", jsonRequest);

            OpenAiResponse response = webClient.post()
                    .uri(openAiProperties.getUrl())
                    .header("Authorization", "Bearer " + openAiProperties.getKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAiResponse.class)
                    .block();

            if (response == null || response.getOutput() == null || response.getOutput().isEmpty()) {
                log.error("Empty response from OpenAI API for input: {}", input);
                throw new RuntimeException("Empty response from OpenAI API");
            }

            String responseText = response.getOutput().get(0).getContent().get(0).getText();
            return objectMapper.readValue(responseText, WordMeaningResponse.class);

        } catch (WebClientResponseException e) {
            log.error("OpenAI API error for input '{}': {} - {}", input, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI API error: " + e.getStatusCode(), e);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse OpenAI response for input '{}': {}", input, e.getMessage());
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    private OpenAiRequest buildRequest(String input, String targetLanguage, String characterSex, String characterName, String style) {
        String normalizedStyle = style != null ? style : "Normal";
        
        Map<String, String> variables = Map.of(
                "def_language", targetLanguage,
                "character_sex", characterSex != null ? characterSex : "male",
                "character_name", characterName != null ? characterName : "Dimas",
                "style", normalizedStyle
        );

        return OpenAiRequest.builder()
                .prompt(OpenAiRequest.Prompt.builder()
                        .id("pmpt_691ed7d2d87c8197aea94829244af12d03db0390c49f6e3b")
                        .version("28")
                        .variables(variables)
                        .build())
                .input(List.of(
                        OpenAiRequest.InputMessage.builder()
                                .role("user")
                                .content(List.of(OpenAiRequest.Content.builder()
                                        .type("input_text")
                                        .text(input)
                                        .build()))
                                .build(),
                        OpenAiRequest.InputMessage.builder()
                                .role("assistant")
                                .content(List.of(OpenAiRequest.Content.builder()
                                        .type("output_text")
                                        .text(buildExampleResponse(targetLanguage))
                                        .build()))
                                .build()
                ))
                .text(OpenAiRequest.Text.builder()
                        .format(OpenAiRequest.Format.builder()
                                .type("json_schema")
                                .name("bilingual_meaning_entry")
                                .strict(true)
                                .schema(OpenAiRequest.JsonSchema.builder()
                                        .type("object")
                                        .properties(Map.of(
                                                "input", OpenAiRequest.Property.builder()
                                                        .type("string")
                                                        .description("The original word or phrase in the source language.")
                                                        .build(),
                                                "output_language", OpenAiRequest.Property.builder()
                                                        .type("string")
                                                        .description("The language for output definitions and examples.")
                                                        .build(),
                                                "type", OpenAiRequest.Property.builder()
                                                        .type("string")
                                                        .description("Indicates whether the input is a 'phrase' or 'word'.")
                                                        .enums(List.of("phrase", "word"))
                                                        .build(),
                                                "meanings", OpenAiRequest.Property.builder()
                                                        .type("array")
                                                        .description("List of distinct meanings of the word or phrase.")
                                                        .items(OpenAiRequest.Items.builder()
                                                                .type("object")
                                                                .properties(Map.of(
                                                                        "definition", OpenAiRequest.Property.builder()
                                                                                .type("string")
                                                                                .description("Meaning or explanation of the word or phrase in English.")
                                                                                .build(),
                                                                        "level", OpenAiRequest.Property.builder()
                                                                                .type("string")
                                                                                .description("CEFR proficiency level for this meaning (e.g., A2, B1, B2, C1).")
                                                                                .pattern("^[ABC][12]$")
                                                                                .build(),
                                                                        "example_input", OpenAiRequest.Property.builder()
                                                                                .type("string")
                                                                                .description("An example input (often a definition or phrase in the native language before translation).")
                                                                                .build(),
                                                                        "example_output", OpenAiRequest.Property.builder()
                                                                                .type("string")
                                                                                .description("An illustrative example or translated definition for this meaning in the target language.")
                                                                                .build()
                                                                ))
                                                                .required(List.of("definition", "level", "example_input", "example_output"))
                                                                .additionalProperties(false)
                                                                .build())
                                                        .build()
                                        ))
                                        .required(List.of("input", "output_language", "type", "meanings"))
                                        .additionalProperties(false)
                                        .build())
                                .build())
                        .build())
                .reasoning(new OpenAiRequest.Reasoning())
                .maxOutputTokens(2048)
                .store(true)
                .include(List.of("web_search_call.action.sources"))
                .build();
    }

    private String buildExampleResponse(String targetLanguage) {
        return """
                {
                  "input": "Suck",
                  "output_language": "%s",
                  "type": "word",
                  "meanings": [
                    {
                      "definition": "To draw something into the mouth by creating a vacuum with the lips and mouth.",
                      "level": "A2",
                      "example_input": "Dimas likes to suck on a lollipop.",
                      "example_output": "Димас любит сосать леденец."
                    },
                    {
                      "definition": "To be very bad or unpleasant (informal).",
                      "level": "B1",
                      "example_input": "Dimas said the movie sucks because it was boring.",
                      "example_output": "Димас сказал, что фильм отстой, потому что он был скучным."
                    }
                  ]
                }
                """.formatted(targetLanguage);
    }
}
