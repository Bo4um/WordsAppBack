package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.CharacterRequest;
import com.bo4um.wordsappback.dto.CharacterResponse;
import com.bo4um.wordsappback.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    /**
     * Получить всех персонажей
     */
    @GetMapping
    public ResponseEntity<List<CharacterResponse>> getAllCharacters() {
        return ResponseEntity.ok(characterService.getAllCharacters());
    }

    /**
     * Получить персонажа по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(characterService.getCharacterById(id));
    }

    /**
     * Создать нового персонажа (только ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CharacterResponse> createCharacter(@RequestBody CharacterRequest request) {
        return ResponseEntity.ok(characterService.createCharacter(request));
    }

    /**
     * Обновить персонажа (только ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CharacterResponse> updateCharacter(
            @PathVariable Long id,
            @RequestBody CharacterRequest request
    ) {
        return ResponseEntity.ok(characterService.updateCharacter(id, request));
    }

    /**
     * Удалить персонажа (только ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }
}
