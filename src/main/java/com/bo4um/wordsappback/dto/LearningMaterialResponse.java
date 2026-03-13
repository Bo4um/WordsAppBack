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
public class LearningMaterialResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String extractedText;
    private String language;
    private String status;

    @JsonProperty("isProcessed")
    private Boolean isProcessed;

    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
}
