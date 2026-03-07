package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания персонажа
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterRequest {

    /**
     * Имя персонажа
     */
    private String name;

    /**
     * Пол персонажа: "male" или "female"
     */
    private String sex;
}
