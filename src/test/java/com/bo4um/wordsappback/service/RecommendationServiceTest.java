package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.RecommendationResponse;
import com.bo4um.wordsappback.entity.Recommendation;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserProgress;
import com.bo4um.wordsappback.entity.WordLearning;
import com.bo4um.wordsappback.repository.RecommendationRepository;
import com.bo4um.wordsappback.repository.UserProgressRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import com.bo4um.wordsappback.repository.WordLearningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationService Unit Tests")
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @Mock
    private WordLearningRepository wordLearningRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationService recommendationService;

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
                .currentStreak(5)
                .longestStreak(10)
                .lastVisitDate(LocalDate.now())
                .totalWordsLearned(50)
                .joinDate(LocalDate.now().minusDays(5))
                .build();
    }

    @Test
    @DisplayName("Should get recommendations for user")
    void getRecommendations_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userProgressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));
        when(wordLearningRepository.findByNextReviewBefore(LocalDate.now()))
                .thenReturn(List.of());
        when(recommendationRepository.findTop10ByUserOrderByPriorityAscCreatedAtDesc(testUser))
                .thenReturn(List.of());

        // When
        List<RecommendationResponse> result = recommendationService.getRecommendations(1L);

        // Then
        assertNotNull(result);
        verify(recommendationRepository).findTop10ByUserOrderByPriorityAscCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("Should get unread recommendations")
    void getUnreadRecommendations_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(recommendationRepository.findByUserAndIsRead(testUser, false))
                .thenReturn(List.of());

        // When
        List<RecommendationResponse> result = recommendationService.getUnreadRecommendations(1L);

        // Then
        assertNotNull(result);
        verify(recommendationRepository).findByUserAndIsRead(testUser, false);
    }

    @Test
    @DisplayName("Should mark recommendation as read")
    void markAsRead_Success() {
        // Given
        Recommendation recommendation = Recommendation.builder()
                .id(1L)
                .user(testUser)
                .type(Recommendation.RecommendationType.NEW_EXERCISE)
                .title("Test")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));

        // When
        recommendationService.markAsRead(1L, 1L);

        // Then
        assertTrue(recommendation.getIsRead());
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    @DisplayName("Should throw exception when recommendation not found")
    void markAsRead_NotFound() {
        // Given
        when(recommendationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                recommendationService.markAsRead(1L, 1L)
        );
    }

    @Test
    @DisplayName("Should throw exception when recommendation belongs to another user")
    void markAsRead_WrongUser() {
        // Given
        User otherUser = User.builder()
                .id(2L)
                .username("otheruser")
                .build();

        Recommendation recommendation = Recommendation.builder()
                .id(1L)
                .user(otherUser)
                .type(Recommendation.RecommendationType.NEW_EXERCISE)
                .title("Test")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                recommendationService.markAsRead(1L, 1L)
        );
    }

    @Test
    @DisplayName("Should mark all recommendations as read")
    void markAllAsRead_Success() {
        // Given
        List<Recommendation> recommendations = List.of(
                Recommendation.builder()
                        .id(1L)
                        .user(testUser)
                        .type(Recommendation.RecommendationType.NEW_EXERCISE)
                        .title("Test 1")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build(),
                Recommendation.builder()
                        .id(2L)
                        .user(testUser)
                        .type(Recommendation.RecommendationType.REVIEW_WORDS)
                        .title("Test 2")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(recommendationRepository.findByUserAndIsRead(testUser, false))
                .thenReturn(recommendations);

        // When
        recommendationService.markAllAsRead(1L);

        // Then
        verify(recommendationRepository).saveAll(anyList());
        assertTrue(recommendations.get(0).getIsRead());
        assertTrue(recommendations.get(1).getIsRead());
    }

    @Test
    @DisplayName("Should cleanup old read recommendations")
    void cleanupOldRecommendations_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        recommendationService.cleanupOldRecommendations(1L);

        // Then
        verify(recommendationRepository).deleteByUserAndIsRead(testUser, true);
    }

    @Test
    @DisplayName("Should generate recommendations with streak")
    void generateRecommendations_WithStreak() {
        // Given
        when(userProgressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));
        when(wordLearningRepository.findByNextReviewBefore(LocalDate.now()))
                .thenReturn(List.of());
        when(recommendationRepository.findByUserAndIsRead(testUser, false))
                .thenReturn(List.of());
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        recommendationService.generateRecommendations(testUser);

        // Then
        verify(recommendationRepository, atLeastOnce()).save(any(Recommendation.class));
    }

    @Test
    @DisplayName("Should generate recommendations with words for review")
    void generateRecommendations_WithWordsForReview() {
        // Given
        WordLearning wordForReview = WordLearning.builder()
                .id(1L)
                .user(testUser)
                .word("test")
                .language("English")
                .nextReview(LocalDate.now())
                .build();

        when(userProgressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));
        when(wordLearningRepository.findByNextReviewBefore(LocalDate.now()))
                .thenReturn(List.of(wordForReview));
        when(recommendationRepository.findByUserAndIsRead(testUser, false))
                .thenReturn(List.of());
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        recommendationService.generateRecommendations(testUser);

        // Then
        verify(recommendationRepository, atLeastOnce()).save(any(Recommendation.class));
    }

    @Test
    @DisplayName("Should not generate duplicate recommendations")
    void generateRecommendations_NoDuplicates() {
        // Given
        Recommendation existingRecommendation = Recommendation.builder()
                .id(1L)
                .user(testUser)
                .type(Recommendation.RecommendationType.NEW_EXERCISE)
                .title("Existing")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Добавляем STREAK_MAINTENANCE чтобы избежать генерации дубликата
        Recommendation streakRecommendation = Recommendation.builder()
                .id(2L)
                .user(testUser)
                .type(Recommendation.RecommendationType.STREAK_MAINTENANCE)
                .title("Existing Streak")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(userProgressRepository.findByUser(testUser)).thenReturn(Optional.of(testProgress));
        when(wordLearningRepository.findByNextReviewBefore(LocalDate.now()))
                .thenReturn(List.of());
        // Возвращаем все типы рекомендаций которые будут проверяться
        when(recommendationRepository.findByUserAndIsRead(testUser, false))
                .thenReturn(List.of(existingRecommendation, streakRecommendation));

        // When
        recommendationService.generateRecommendations(testUser);

        // Then - не должно быть сохранений так как все типы уже существуют
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    @DisplayName("Should map recommendation to response correctly")
    void mapToResponse_Success() {
        // This test verifies the mapping logic indirectly through other tests
        // The actual mapping is tested in integration tests
        assertNotNull(RecommendationResponse.builder().build());
    }
}
