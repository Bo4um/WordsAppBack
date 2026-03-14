package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.StreakRecovery;
import com.bo4um.wordsappback.repository.StreakRecoveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StreakRecoveryService Unit Tests")
class StreakRecoveryServiceTest {

    @Mock
    private StreakRecoveryRepository recoveryRepository;

    @InjectMocks
    private StreakRecoveryService recoveryService;

    private StreakRecovery testRecovery;

    @BeforeEach
    void setUp() {
        testRecovery = StreakRecovery.builder()
                .id(1L)
                .userId(1L)
                .tokensCount(2)
                .maxTokens(3)
                .createdAt(LocalDate.now())
                .expiresAt(LocalDate.now().plusMonths(1))
                .build();
    }

    @Test
    @DisplayName("Should use recovery token successfully")
    void useRecoveryToken_Success() {
        // Given
        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(testRecovery));
        when(recoveryRepository.save(any(StreakRecovery.class))).thenReturn(testRecovery);

        // When
        boolean result = recoveryService.useRecoveryToken(1L);

        // Then
        assertTrue(result);
        assertEquals(1, testRecovery.getTokensCount());
        assertNotNull(testRecovery.getLastUsedDate());
    }

    @Test
    @DisplayName("Should create recovery record if not exists")
    void useRecoveryToken_CreateNew() {
        // Given
        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(recoveryRepository.save(any(StreakRecovery.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        boolean result = recoveryService.useRecoveryToken(1L);

        // Then
        assertFalse(result); // No token to use
        verify(recoveryRepository).save(any(StreakRecovery.class));
    }

    @Test
    @DisplayName("Should return false when no tokens available")
    void useRecoveryToken_NoTokens() {
        // Given
        StreakRecovery noTokens = StreakRecovery.builder()
                .userId(1L)
                .tokensCount(0)
                .maxTokens(3)
                .build();

        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(noTokens));

        // When
        boolean result = recoveryService.useRecoveryToken(1L);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should get recovery tokens count")
    void getRecoveryTokens_Success() {
        // Given
        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(testRecovery));

        // When
        int result = recoveryService.getRecoveryTokens(1L);

        // Then
        assertEquals(2, result);
    }

    @Test
    @DisplayName("Should return 0 when no recovery record")
    void getRecoveryTokens_NoRecord() {
        // Given
        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // When
        int result = recoveryService.getRecoveryTokens(1L);

        // Then
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Should check if user can recover")
    void canRecover_WithTokens() {
        // Given
        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(testRecovery));

        // When
        boolean result = recoveryService.canRecover(1L);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should check if user cannot recover")
    void canRecover_NoTokens() {
        // Given
        StreakRecovery noTokens = StreakRecovery.builder()
                .userId(1L)
                .tokensCount(0)
                .build();

        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(noTokens));

        // When
        boolean result = recoveryService.canRecover(1L);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should award token")
    void awardToken_Success() {
        // Given
        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(testRecovery));
        when(recoveryRepository.save(any(StreakRecovery.class))).thenReturn(testRecovery);

        // When
        recoveryService.awardToken(1L);

        // Then
        verify(recoveryRepository).save(testRecovery);
        assertEquals(3, testRecovery.getTokensCount()); // Was 2, awarded 1
    }

    @Test
    @DisplayName("Should not award token if at max")
    void awardToken_AtMax() {
        // Given
        StreakRecovery atMax = StreakRecovery.builder()
                .userId(1L)
                .tokensCount(3)
                .maxTokens(3)
                .build();

        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(atMax));

        // When
        recoveryService.awardToken(1L);

        // Then - save should NOT be called when at max
        verify(recoveryRepository, never()).save(any(StreakRecovery.class));
        assertEquals(3, atMax.getTokensCount()); // Still 3, at max
    }

    @Test
    @DisplayName("Should regenerate tokens weekly")
    void useRecoveryToken_RegenerateWeekly() {
        // Given
        StreakRecovery oldRecovery = StreakRecovery.builder()
                .userId(1L)
                .tokensCount(1)
                .maxTokens(3)
                .lastUsedDate(LocalDate.now().minusWeeks(2))
                .build();

        when(recoveryRepository.findByUserId(1L)).thenReturn(Optional.of(oldRecovery));
        when(recoveryRepository.save(any(StreakRecovery.class))).thenReturn(oldRecovery);

        // When
        boolean result = recoveryService.useRecoveryToken(1L);

        // Then
        assertTrue(result);
        assertEquals(1, oldRecovery.getTokensCount()); // Was 1, regenerated to 2, used 1
    }
}
