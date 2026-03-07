package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа с информацией о персонаже
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {

    /**
     * ID персонажа
     */
    private Long id;

    /**
     * Имя персонажа
     */
    private String name;

    /**
     * Пол персонажа: "male" или "female"
     */
    private String sex;
}
