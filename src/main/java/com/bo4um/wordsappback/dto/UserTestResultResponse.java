package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO для результата теста
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTestResultResponse {

    private Long id;

    private Long testId;

    private String testName;

    private Integer score;

    private Integer maxScore;

    private Integer percentage;

    private String determinedLevel;

    private LocalDateTime completedAt;

    /**
     * Детализация по вопросам (опционально)
     */
    private Map<Long, Boolean> questionResults;
}
