package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.CharacterRequest;
import com.bo4um.wordsappback.dto.CharacterResponse;
import com.bo4um.wordsappback.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * Получить только активных персонажей
     */
    @GetMapping("/active")
    public ResponseEntity<List<CharacterResponse>> getActiveCharacters() {
        return ResponseEntity.ok(characterService.getActiveCharacters());
    }

    /**
     * Получить персонажа по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(characterService.getCharacterById(id));
    }

    /**
     * Получить изображение персонажа
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getCharacterImage(@PathVariable Long id) {
        byte[] image = characterService.getCharacterImage(id);
        
        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(image);
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
     * Загрузить изображение для персонажа (только ADMIN)
     */
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CharacterResponse> uploadCharacterImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(characterService.uploadCharacterImage(id, image));
    }

    /**
     * Удалить изображение персонажа (только ADMIN)
     */
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCharacterImage(@PathVariable Long id) {
        characterService.deleteCharacterImage(id);
        return ResponseEntity.noContent().build();
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
