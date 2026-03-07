package com.bo4um.wordsappback.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai.api")
public class OpenAiProperties {

    /**
     * OpenAI API key for authentication
     */
    private String key;

    /**
     * OpenAI API base URL
     */
    private String url = "https://api.openai.com/v1/responses";
}
