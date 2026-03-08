package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO для загрузки изображения персонажа
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterImageRequest {

    /**
     * Файл изображения
     */
    private MultipartFile image;
}
