package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemeResponse {
    private Long id;
    private String imageUrl;
    private String title;
    private String description;
    private String memeType;
    private String language;
    private String difficulty;
    private String culturalContext;
    private List<String> vocabularyWords;
    private Integer likes;
    private Integer shares;

    @JsonProperty("isActive")
    private Boolean isActive;

    private LocalDateTime createdAt;
}
