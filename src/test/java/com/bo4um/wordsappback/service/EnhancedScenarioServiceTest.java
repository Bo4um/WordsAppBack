package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.EnhancedScenarioResponse;
import com.bo4um.wordsappback.entity.EnhancedScenario;
import com.bo4um.wordsappback.repository.EnhancedScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnhancedScenarioService Unit Tests")
class EnhancedScenarioServiceTest {

    @Mock
    private EnhancedScenarioRepository scenarioRepository;

    @InjectMocks
    private EnhancedScenarioService scenarioService;

    private EnhancedScenario testScenario;

    @BeforeEach
    void setUp() {
        testScenario = EnhancedScenario.builder()
                .id(1L)
                .title("Apartment Hunting")
                .description("Find and rent an apartment")
                .category("relocation")
                .language("English")
                .difficulty("B1")
                .estimatedDuration(15)
                .learningObjectives("Learn housing vocabulary")
                .emotions(Arrays.asList("impatient", "helpful"))
                .isActive(true)
                .completionCount(0)
                .averageRating(null)
                .build();
    }

    @Test
    @DisplayName("Should get scenarios by category")
    void getScenariosByCategory_Success() {
        // Given
        when(scenarioRepository.findByCategoryAndIsActive("relocation", true))
                .thenReturn(Arrays.asList(testScenario));

        // When
        List<EnhancedScenarioResponse> result = scenarioService.getScenariosByCategory("relocation");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Apartment Hunting", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Should get relocation scenarios")
    void getRelocationScenarios_Success() {
        // Given
        when(scenarioRepository.findByCategoryAndLanguageAndIsActive("relocation", "English", true))
                .thenReturn(Arrays.asList(testScenario));

        // When
        List<EnhancedScenarioResponse> result = scenarioService.getRelocationScenarios("English");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("relocation", result.get(0).getCategory());
    }

    @Test
    @DisplayName("Should get trending scenarios")
    void getTrendingScenarios_Success() {
        // Given
        when(scenarioRepository.findByIsActiveOrderByCompletionCountDesc(true))
                .thenReturn(Arrays.asList(testScenario));

        // When
        List<EnhancedScenarioResponse> result = scenarioService.getTrendingScenarios(10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should increment completion count")
    void incrementCompletionCount_Success() {
        // Given
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(testScenario));
        when(scenarioRepository.save(any(EnhancedScenario.class))).thenReturn(testScenario);

        // When
        scenarioService.incrementCompletionCount(1L);

        // Then
        verify(scenarioRepository).save(testScenario);
        assertEquals(1, testScenario.getCompletionCount());
    }

    @Test
    @DisplayName("Should rate scenario")
    void rateScenario_Success() {
        // Given
        EnhancedScenario scenario = EnhancedScenario.builder()
                .id(1L)
                .title("Test")
                .category("relocation")
                .language("English")
                .difficulty("B1")
                .isActive(true)
                .completionCount(5)
                .averageRating(4.0)
                .build();

        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(scenario));
        when(scenarioRepository.save(any(EnhancedScenario.class))).thenReturn(scenario);

        // When
        scenarioService.rateScenario(1L, 5);

        // Then
        verify(scenarioRepository).save(scenario);
        assertNotNull(scenario.getAverageRating());
    }

    @Test
    @DisplayName("Should throw exception for invalid rating")
    void rateScenario_InvalidRating() {
        // Given
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(testScenario));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                scenarioService.rateScenario(1L, 6)
        );
    }

    @Test
    @DisplayName("Should throw exception when scenario not found")
    void incrementCompletionCount_ScenarioNotFound() {
        // Given
        when(scenarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                scenarioService.incrementCompletionCount(999L)
        );
    }
}
