package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.CharacterRequest;
import com.bo4um.wordsappback.dto.CharacterResponse;
import com.bo4um.wordsappback.entity.Character;
import com.bo4um.wordsappback.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterService {

    private final CharacterRepository characterRepository;

    /**
     * Получить всех персонажей
     */
    public List<CharacterResponse> getAllCharacters() {
        log.debug("Fetching all characters");
        return characterRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Получить только активных персонажей
     */
    public List<CharacterResponse> getActiveCharacters() {
        log.debug("Fetching active characters");
        return characterRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Получить персонажа по ID
     */
    public CharacterResponse getCharacterById(Long id) {
        log.debug("Fetching character with id: {}", id);
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + id));
        return toResponse(character);
    }

    /**
     * Получить персонажа по имени
     */
    public CharacterResponse getCharacterByName(String name) {
        log.debug("Fetching character with name: {}", name);
        Character character = characterRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with name: " + name));
        return toResponse(character);
    }

    /**
     * Создать нового персонажа
     */
    @Transactional
    public CharacterResponse createCharacter(CharacterRequest request) {
        log.info("Creating character: {} ({})", request.getName(), request.getSex());

        if (characterRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Character with name '" + request.getName() + "' already exists");
        }

        validateCharacter(request);

        Character character = Character.builder()
                .name(request.getName())
                .sex(request.getSex())
                .description(request.getDescription())
                .isSystem(request.getIsSystem() != null ? request.getIsSystem() : false)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .sortOrder(request.getSortOrder())
                .build();

        Character saved = characterRepository.save(character);
        log.info("Character created with id: {}", saved.getId());
        return toResponse(saved);
    }

    /**
     * Обновить персонажа
     */
    @Transactional
    public CharacterResponse updateCharacter(Long id, CharacterRequest request) {
        log.info("Updating character with id: {}", id);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + id));

        // Защита системных персонажей от изменения некоторых полей
        if (character.getIsSystem() != null && character.getIsSystem()) {
            // Можно менять только описание и активность, но не имя и пол
            character.setDescription(request.getDescription());
            character.setIsActive(request.getIsActive() != null ? request.getIsActive() : character.getIsActive());
            character.setSortOrder(request.getSortOrder());
        } else {
            character.setName(request.getName());
            character.setSex(request.getSex());
            character.setDescription(request.getDescription());
            character.setIsSystem(request.getIsSystem() != null ? request.getIsSystem() : false);
            character.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
            character.setSortOrder(request.getSortOrder());
        }

        Character updated = characterRepository.save(character);
        log.info("Character updated with id: {}", updated.getId());
        return toResponse(updated);
    }

    /**
     * Удалить персонажа
     */
    @Transactional
    public void deleteCharacter(Long id) {
        log.info("Deleting character with id: {}", id);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + id));

        if (character.getIsSystem() != null && character.getIsSystem()) {
            throw new IllegalArgumentException("Cannot delete system character: " + character.getName());
        }

        characterRepository.deleteById(id);
        log.info("Character deleted with id: {}", id);
    }

    /**
     * Загрузить изображение для персонажа
     */
    @Transactional
    public CharacterResponse uploadCharacterImage(Long id, MultipartFile image) throws IOException {
        log.info("Uploading image for character with id: {}", id);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + id));

        validateImage(image);

        character.setImage(image.getBytes());
        Character updated = characterRepository.save(character);

        log.info("Image uploaded for character with id: {}", updated.getId());
        return toResponse(updated);
    }

    /**
     * Получить изображение персонажа
     */
    public byte[] getCharacterImage(Long id) {
        log.debug("Fetching image for character with id: {}", id);
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + id));
        return character.getImage();
    }

    /**
     * Удалить изображение персонажа
     */
    @Transactional
    public void deleteCharacterImage(Long id) {
        log.info("Deleting image for character with id: {}", id);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + id));

        character.setImage(null);
        characterRepository.save(character);

        log.info("Image deleted for character with id: {}", id);
    }

    private void validateCharacter(CharacterRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Character name is required");
        }
        if (request.getSex() == null || request.getSex().isBlank()) {
            throw new IllegalArgumentException("Character sex is required");
        }
        if (!request.getSex().equalsIgnoreCase("male") && !request.getSex().equalsIgnoreCase("female")) {
            throw new IllegalArgumentException("Character sex must be 'male' or 'female'");
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Проверка размера (максимум 5MB)
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Image size must not exceed 5MB");
        }
    }

    private CharacterResponse toResponse(Character character) {
        return CharacterResponse.builder()
                .id(character.getId())
                .name(character.getName())
                .sex(character.getSex())
                .description(character.getDescription())
                .isSystem(character.getIsSystem())
                .isActive(character.getIsActive())
                .sortOrder(character.getSortOrder())
                .hasImage(character.getImage() != null && character.getImage().length > 0)
                .build();
    }
}
