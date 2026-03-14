package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.PragmaticCorrectionResponse;
import com.bo4um.wordsappback.entity.PragmaticError;
import com.bo4um.wordsappback.repository.PragmaticErrorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PragmaticCorrectionService Unit Tests")
class PragmaticCorrectionServiceTest {

    @Mock
    private PragmaticErrorRepository errorRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private PragmaticCorrectionService correctionService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should analyze pragmatics - appropriate text")
    void analyzePragmatics_Appropriate_Success() {
        // Given
        String text = "Could you please send me the report?";
        String context = "business";

        // When
        PragmaticCorrectionResponse result = correctionService.analyzePragmatics(
                text, context, "English");

        // Then
        assertNotNull(result);
        assertEquals(text, result.getOriginalText());
        assertTrue(result.getIsFormalAppropriate());
        assertTrue(result.getIsToneAppropriate());
    }

    @Test
    @DisplayName("Should analyze pragmatics - inappropriate tone")
    void analyzePragmatics_InappropriateTone() {
        // Given
        String text = "Shut up and send the report!";
        String context = "business";

        // When
        PragmaticCorrectionResponse result = correctionService.analyzePragmatics(
                text, context, "English");

        // Then
        assertNotNull(result);
        assertFalse(result.getIsToneAppropriate());
        assertEquals("tone", result.getErrorType());
        assertNotNull(result.getExplanation());
    }

    @Test
    @DisplayName("Should analyze pragmatics - informal in business")
    void analyzePragmatics_InformalInBusiness() {
        // Given
        String text = "Hey, wanna send me that doc?";
        String context = "business";

        // When
        PragmaticCorrectionResponse result = correctionService.analyzePragmatics(
                text, context, "English");

        // Then
        assertNotNull(result);
        assertFalse(result.getIsFormalAppropriate());
        assertEquals("formality", result.getErrorType());
    }

    @Test
    @DisplayName("Should get recent errors")
    void getRecentErrors_Success() {
        // Given
        PragmaticError error = PragmaticError.builder()
                .id(1L)
                .userUtterance("Test")
                .correctedVersion("Corrected")
                .errorType("tone")
                .explanation("Too direct")
                .severityLevel(3)
                .build();

        when(errorRepository.findTop10ByOrderByCreatedAtDesc())
                .thenReturn(Arrays.asList(error));

        // When
        List<PragmaticCorrectionResponse> result = correctionService.getRecentErrors(10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("tone", result.get(0).getErrorType());
    }

    @Test
    @DisplayName("Should get errors by type")
    void getErrorsByType_Success() {
        // Given
        PragmaticError error = PragmaticError.builder()
                .id(1L)
                .userUtterance("Test")
                .errorType("formality")
                .build();

        when(errorRepository.findByErrorTypeOrderByCreatedAtDesc("formality"))
                .thenReturn(Arrays.asList(error));

        // When
        List<PragmaticCorrectionResponse> result = correctionService.getErrorsByType("formality");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should mark error as helpful")
    void markErrorAsHelpful_Success() {
        // Given
        PragmaticError error = PragmaticError.builder()
                .id(1L)
                .userUtterance("Test")
                .errorType("tone")
                .isHelpful(false)
                .build();

        when(errorRepository.findById(1L)).thenReturn(Optional.of(error));
        when(errorRepository.save(any(PragmaticError.class))).thenReturn(error);

        // When
        correctionService.markErrorAsHelpful(1L);

        // Then
        verify(errorRepository).save(error);
        assertTrue(error.getIsHelpful());
    }

    @Test
    @DisplayName("Should throw exception when error not found")
    void markErrorAsHelpful_ErrorNotFound() {
        // Given
        when(errorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                correctionService.markErrorAsHelpful(999L)
        );
    }
}
