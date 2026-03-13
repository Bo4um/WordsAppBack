package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания/обновления персонажа
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

    /**
     * Описание персонажа
     */
    private String description;

    /**
     * Флаг системного персонажа (защищён от удаления)
     */
    private Boolean isSystem = false;

    /**
     * Флаг активности персонажа
     */
    private Boolean isActive = true;

    /**
     * Порядок сортировки персонажей
     */
    private Integer sortOrder;
}
