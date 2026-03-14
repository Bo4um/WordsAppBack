package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserProgress;
import com.bo4um.wordsappback.repository.UserProgressRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeaderboardService Unit Tests")
class LeaderboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProgressRepository progressRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private User testUser;
    private UserProgress testProgress;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role(User.Role.USER)
                .build();

        testProgress = UserProgress.builder()
                .id(1L)
                .user(testUser)
                .currentStreak(10)
                .longestStreak(20)
                .totalWordsLearned(500)
                .build();
    }

    @Test
    @DisplayName("Should get global leaderboard")
    void getGlobalLeaderboard_Success() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(progressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));

        // When
        List<Map<String, Object>> result = leaderboardService.getGlobalLeaderboard(50);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).containsKey("rank"));
        assertTrue(result.get(0).containsKey("score"));
    }

    @Test
    @DisplayName("Should get leaderboard by streak category")
    void getLeaderboardByCategory_Streak() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(progressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));

        // When
        List<Map<String, Object>> result = leaderboardService.getLeaderboardByCategory("streak");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).containsKey("streak"));
    }

    @Test
    @DisplayName("Should get leaderboard by words category")
    void getLeaderboardByCategory_Words() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(progressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));

        // When
        List<Map<String, Object>> result = leaderboardService.getLeaderboardByCategory("words");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).containsKey("wordsLearned"));
    }

    @Test
    @DisplayName("Should get user rank")
    void getUserRank_Success() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(progressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));

        // When
        Map<String, Object> result = leaderboardService.getUserRank(1L, "streak");

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("rank"));
        assertEquals(1, result.get("rank"));
    }

    @Test
    @DisplayName("Should return -1 rank for non-existent user")
    void getUserRank_UserNotFound() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(progressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));

        // When
        Map<String, Object> result = leaderboardService.getUserRank(999L, "streak");

        // Then
        assertNotNull(result);
        assertEquals(-1, result.get("rank"));
    }
}
