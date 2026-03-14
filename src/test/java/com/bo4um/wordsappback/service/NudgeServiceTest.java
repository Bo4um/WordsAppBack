package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.UserNudge;
import com.bo4um.wordsappback.repository.UserNudgeRepository;
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
@DisplayName("NudgeService Unit Tests")
class NudgeServiceTest {

    @Mock
    private UserNudgeRepository nudgeRepository;

    @InjectMocks
    private NudgeService nudgeService;

    private UserNudge testNudge;

    @BeforeEach
    void setUp() {
        testNudge = UserNudge.builder()
                .id(1L)
                .userId(1L)
                .nudgeType("encouragement")
                .message("Great job!")
                .context("behavior_based")
                .isRead(false)
                .isActioned(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should send nudge")
    void sendNudge_Success() {
        // Given
        UserNudge newNudge = UserNudge.builder()
                .id(2L)
                .userId(1L)
                .nudgeType("reminder")
                .message("Test message")
                .context("time_based")
                .build();
        when(nudgeRepository.save(any(UserNudge.class))).thenReturn(newNudge);

        // When
        UserNudge result = nudgeService.sendNudge(1L, "reminder", "Test message", "time_based");

        // Then
        assertNotNull(result);
        assertEquals("reminder", result.getNudgeType());
    }

    @Test
    @DisplayName("Should send encouragement")
    void sendEncouragement_Success() {
        // Given
        UserNudge encouragementNudge = UserNudge.builder()
                .id(3L)
                .userId(1L)
                .nudgeType("encouragement")
                .message("🎉 Great job! 7-day streak Keep up the momentum!")
                .context("behavior_based")
                .build();
        when(nudgeRepository.save(any(UserNudge.class))).thenReturn(encouragementNudge);

        // When
        UserNudge result = nudgeService.sendEncouragement(1L, "7-day streak");

        // Then
        assertNotNull(result);
        assertTrue(result.getMessage().contains("Great job"));
    }

    @Test
    @DisplayName("Should send reminder")
    void sendReminder_Success() {
        // Given
        UserNudge reminderNudge = UserNudge.builder()
                .id(4L)
                .userId(1L)
                .nudgeType("reminder")
                .message("💡 Time for your daily vocabulary! Just 3 minutes can make a difference.")
                .context("time_based")
                .build();
        when(nudgeRepository.save(any(UserNudge.class))).thenReturn(reminderNudge);

        // When
        UserNudge result = nudgeService.sendReminder(1L, "vocabulary");

        // Then
        assertNotNull(result);
        assertEquals("reminder", result.getNudgeType());
    }

    @Test
    @DisplayName("Should send challenge")
    void sendChallenge_Success() {
        // Given
        UserNudge challengeNudge = UserNudge.builder()
                .id(5L)
                .userId(1L)
                .nudgeType("challenge")
                .message("🏆 New challenge available: Learn 50 words today. Think you can do it?")
                .context("milestone")
                .build();
        when(nudgeRepository.save(any(UserNudge.class))).thenReturn(challengeNudge);

        // When
        UserNudge result = nudgeService.sendChallenge(1L, "Learn 50 words");

        // Then
        assertNotNull(result);
        assertEquals("challenge", result.getNudgeType());
    }

    @Test
    @DisplayName("Should get unread nudges")
    void getUnreadNudges_Success() {
        // Given
        when(nudgeRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(1L, false))
                .thenReturn(Arrays.asList(testNudge));

        // When
        List<UserNudge> result = nudgeService.getUnreadNudges(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsRead());
    }

    @Test
    @DisplayName("Should mark nudge as read")
    void markAsRead_Success() {
        // Given
        when(nudgeRepository.findById(1L)).thenReturn(Optional.of(testNudge));
        when(nudgeRepository.save(any(UserNudge.class))).thenReturn(testNudge);

        // When
        nudgeService.markAsRead(1L);

        // Then
        verify(nudgeRepository).save(testNudge);
        assertTrue(testNudge.getIsRead());
    }

    @Test
    @DisplayName("Should mark nudge as actioned")
    void markAsActioned_Success() {
        // Given
        when(nudgeRepository.findById(1L)).thenReturn(Optional.of(testNudge));
        when(nudgeRepository.save(any(UserNudge.class))).thenReturn(testNudge);

        // When
        nudgeService.markAsActioned(1L);

        // Then
        verify(nudgeRepository).save(testNudge);
        assertTrue(testNudge.getIsActioned());
    }

    @Test
    @DisplayName("Should get nudge history")
    void getNudgeHistory_Success() {
        // Given
        when(nudgeRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(testNudge));

        // When
        List<UserNudge> result = nudgeService.getNudgeHistory(1L, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should throw exception when nudge not found")
    void markAsRead_NudgeNotFound() {
        // Given
        when(nudgeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                nudgeService.markAsRead(999L)
        );
    }
}
