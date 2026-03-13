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
public class DialogMessageResponse {
    private Long id;
    private String role;
    private String content;
    private LocalDateTime timestamp;
    private Integer tokenUsage;

    @JsonProperty("isComplete")
    private Boolean isComplete = true; // false для SSE streaming ответов
}
