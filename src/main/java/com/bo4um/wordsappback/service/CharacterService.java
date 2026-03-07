package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.CharacterRequest;
import com.bo4um.wordsappback.dto.CharacterResponse;
import com.bo4um.wordsappback.entity.Character;
import com.bo4um.wordsappback.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Character character = Character.builder()
                .name(request.getName())
                .sex(request.getSex())
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

        character.setName(request.getName());
        character.setSex(request.getSex());

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
        
        if (!characterRepository.existsById(id)) {
            throw new IllegalArgumentException("Character not found with id: " + id);
        }

        characterRepository.deleteById(id);
        log.info("Character deleted with id: {}", id);
    }

    private CharacterResponse toResponse(Character character) {
        return CharacterResponse.builder()
                .id(character.getId())
                .name(character.getName())
                .sex(character.getSex())
                .build();
    }
}
