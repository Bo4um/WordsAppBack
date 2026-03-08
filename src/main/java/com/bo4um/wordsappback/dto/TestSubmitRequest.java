package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO для отправки ответов на тест
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSubmitRequest {

    /**
     * Ответы пользователя: ключ - ID вопроса, значение - выбранный вариант (A, B, C или D)
     */
    private Map<Long, String> answers;
}
