package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.NanoLearningSession;
import com.bo4um.wordsappback.repository.NanoLearningSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NanoLearningService Unit Tests")
class NanoLearningServiceTest {

    @Mock
    private NanoLearningSessionRepository sessionRepository;

    @InjectMocks
    private NanoLearningService nanoLearningService;

    private NanoLearningSession testSession;

    @BeforeEach
    void setUp() {
        testSession = NanoLearningSession.builder()
                .id(1L)
                .title("Nano Lesson: article")
                .content("Test content")
                .contentType("article")
                .durationMinutes(3)
                .language("English")
                .difficulty("B1")
                .vocabularyList("word1, word2")
                .isCompleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should generate nano session")
    void generateNanoSession_Success() {
        // Given
        when(sessionRepository.save(any(NanoLearningSession.class))).thenReturn(testSession);

        // When
        NanoLearningSession result = nanoLearningService.generateNanoSession(
                "content", "article", "English", "B1");

        // Then
        assertNotNull(result);
        assertEquals("Nano Lesson: article", result.getTitle());
        assertEquals(3, result.getDurationMinutes());
        assertFalse(result.getIsCompleted());
    }

    @Test
    @DisplayName("Should complete session with quiz score")
    void completeSession_Success() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(NanoLearningSession.class))).thenReturn(testSession);

        // When
        NanoLearningSession result = nanoLearningService.completeSession(1L, 85);

        // Then
        assertNotNull(result);
        assertTrue(result.getIsCompleted());
        assertEquals(85, result.getQuizScore());
        assertNotNull(result.getCompletedAt());
    }

    @Test
    @DisplayName("Should get incomplete sessions")
    void getIncompleteSessions_Success() {
        // Given
        when(sessionRepository.findByIsCompletedOrderByCreatedAtDesc(false))
                .thenReturn(Arrays.asList(testSession));

        // When
        List<NanoLearningSession> result = nanoLearningService.getIncompleteSessions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsCompleted());
    }

    @Test
    @DisplayName("Should get sessions by level")
    void getSessionsByLevel_Success() {
        // Given
        when(sessionRepository.findByLanguageAndDifficulty("English", "B1"))
                .thenReturn(Arrays.asList(testSession));

        // When
        List<NanoLearningSession> result = nanoLearningService.getSessionsByLevel("English", "B1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("B1", result.get(0).getDifficulty());
    }

    @Test
    @DisplayName("Should throw exception when session not found")
    void completeSession_SessionNotFound() {
        // Given
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                nanoLearningService.completeSession(999L, 85)
        );
    }
}
