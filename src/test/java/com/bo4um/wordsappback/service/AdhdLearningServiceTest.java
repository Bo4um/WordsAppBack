package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.AdhdLearningProfile;
import com.bo4um.wordsappback.repository.AdhdLearningProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdhdLearningService Unit Tests")
class AdhdLearningServiceTest {

    @Mock
    private AdhdLearningProfileRepository profileRepository;

    @InjectMocks
    private AdhdLearningService adhdService;

    private AdhdLearningProfile testProfile;

    @BeforeEach
    void setUp() {
        testProfile = AdhdLearningProfile.builder()
                .id(1L)
                .userId(1L)
                .adhdModeEnabled(false)
                .preferredSessionDuration(3)
                .frequentBreaks(true)
                .breakFrequency(5)
                .focusMode("flexible")
                .visualReminders(true)
                .gamification(true)
                .gentleReminders(true)
                .build();
    }

    @Test
    @DisplayName("Should get or create profile")
    void getOrCreateProfile_Success() {
        // Given
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        // When
        AdhdLearningProfile result = adhdService.getOrCreateProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
    }

    @Test
    @DisplayName("Should enable ADHD mode")
    void enableAdhdMode_Success() {
        // Given
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(AdhdLearningProfile.class))).thenReturn(testProfile);

        // When
        AdhdLearningProfile result = adhdService.enableAdhdMode(1L);

        // Then
        assertTrue(result.getAdhdModeEnabled());
    }

    @Test
    @DisplayName("Should update session duration")
    void updateSessionDuration_Success() {
        // Given
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(AdhdLearningProfile.class))).thenReturn(testProfile);

        // When
        AdhdLearningProfile result = adhdService.updateSessionDuration(1L, 5);

        // Then
        assertEquals(5, result.getPreferredSessionDuration());
    }

    @Test
    @DisplayName("Should throw exception for invalid duration")
    void updateSessionDuration_InvalidDuration() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                adhdService.updateSessionDuration(1L, 15)
        );
    }

    @Test
    @DisplayName("Should get recommended session length")
    void getRecommendedSessionLength_Success() {
        // Given - profile with ADHD mode enabled
        AdhdLearningProfile adhdProfile = AdhdLearningProfile.builder()
                .userId(1L)
                .adhdModeEnabled(true)
                .preferredSessionDuration(5)
                .build();
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(adhdProfile));

        // When
        int result = adhdService.getRecommendedSessionLength(1L);

        // Then - should return preferred duration when ADHD mode is enabled
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Should show break reminder")
    void shouldShowBreakReminder_True() {
        // Given
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        // When
        boolean result = adhdService.shouldShowBreakReminder(1L, 6);

        // Then
        assertTrue(result);
    }
}
