package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.MemeExerciseRequest;
import com.bo4um.wordsappback.dto.MemeExerciseResponse;
import com.bo4um.wordsappback.dto.MemeResponse;
import com.bo4um.wordsappback.entity.LearningMeme;
import com.bo4um.wordsappback.repository.LearningMemeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemeService Unit Tests")
class MemeServiceTest {

    @Mock
    private LearningMemeRepository memeRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private MemeService memeService;

    private LearningMeme testMeme;

    @BeforeEach
    void setUp() {
        testMeme = LearningMeme.builder()
                .id(1L)
                .imageUrl("https://example.com/meme.jpg")
                .title("Drake Hotline Bling")
                .description("Drake rejecting formal English")
                .memeType("drake")
                .language("English")
                .difficulty("B1")
                .culturalContext("Popular meme format")
                .vocabularyWords("slang, informal, reject")
                .likes(150)
                .shares(45)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should get trending memes")
    void getTrendingMemes_Success() {
        // Given
        when(memeRepository.findByLanguageAndIsActiveOrderByLikesDesc("English", true))
                .thenReturn(Arrays.asList(testMeme));

        // When
        List<MemeResponse> result = memeService.getTrendingMemes("English", 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Drake Hotline Bling", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Should get memes by difficulty")
    void getMemesByDifficulty_Success() {
        // Given
        when(memeRepository.findByLanguageAndDifficultyAndIsActive("English", "B1", true))
                .thenReturn(Arrays.asList(testMeme));

        // When
        List<MemeResponse> result = memeService.getMemesByDifficulty("English", "B1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("B1", result.get(0).getDifficulty());
    }

    @Test
    @DisplayName("Should generate explain exercise")
    void generateExercise_Explain_Success() {
        // Given
        MemeExerciseRequest request = new MemeExerciseRequest(1L, null, "explain");
        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));

        // When
        MemeExerciseResponse result = memeService.generateExercise(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMemeId());
        assertEquals(10, result.getPointsEarned());
        assertTrue(result.getIsCorrect());
    }

    @Test
    @DisplayName("Should generate complete exercise")
    void generateExercise_Complete_Success() {
        // Given
        MemeExerciseRequest request = new MemeExerciseRequest(1L, null, "complete");
        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));

        // When
        MemeExerciseResponse result = memeService.generateExercise(request);

        // Then
        assertNotNull(result);
        assertEquals(15, result.getPointsEarned());
        assertNull(result.getIsCorrect());
    }

    @Test
    @DisplayName("Should generate translate exercise")
    void generateExercise_Translate_Success() {
        // Given
        MemeExerciseRequest request = new MemeExerciseRequest(1L, null, "translate");
        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));

        // When
        MemeExerciseResponse result = memeService.generateExercise(request);

        // Then
        assertNotNull(result);
        assertEquals(20, result.getPointsEarned());
        assertNull(result.getIsCorrect());
    }

    @Test
    @DisplayName("Should like a meme")
    void likeMeme_Success() {
        // Given
        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));
        when(memeRepository.save(any(LearningMeme.class))).thenReturn(testMeme);

        // When
        memeService.likeMeme(1L);

        // Then
        verify(memeRepository).save(testMeme);
        assertEquals(151, testMeme.getLikes());
    }

    @Test
    @DisplayName("Should share a meme")
    void shareMeme_Success() {
        // Given
        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));
        when(memeRepository.save(any(LearningMeme.class))).thenReturn(testMeme);

        // When
        memeService.shareMeme(1L);

        // Then
        verify(memeRepository).save(testMeme);
        assertEquals(46, testMeme.getShares());
    }

    @Test
    @DisplayName("Should throw exception when meme not found")
    void generateExercise_MemeNotFound() {
        // Given
        MemeExerciseRequest request = new MemeExerciseRequest(999L, null, "explain");
        when(memeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                memeService.generateExercise(request)
        );
    }
}
