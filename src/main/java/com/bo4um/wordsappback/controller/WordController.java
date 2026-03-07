package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.dto.WordMeaningResponse;
import com.bo4um.wordsappback.dto.WordRequest;
import com.bo4um.wordsappback.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WordController {

    private final OpenAiService openAiService;

    /**
     * Get word meaning and translation
     *
     * @param request request body with input word and parameters
     * @return word meaning with definitions and examples
     */
    @PostMapping("/word")
    public ResponseEntity<WordMeaningResponse> getWordMeaning(@RequestBody WordRequest request) {
        WordMeaningResponse response = openAiService.getWordMeaning(
                request.getInput(),
                request.getDefLanguage(),
                request.getCharacterSex(),
                request.getCharacterName(),
                request.getStyle()
        );
        return ResponseEntity.ok(response);
    }
}
