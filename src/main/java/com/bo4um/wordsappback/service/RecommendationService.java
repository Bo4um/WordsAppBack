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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserProgressRepository userProgressRepository;
    private final WordLearningRepository wordLearningRepository;
    private final UserRepository userRepository;

    /**
     * Get personalized recommendations for user
     */
    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Generate recommendations based on user activity
        generateRecommendations(user);

        // Get and return recommendations
        List<Recommendation> recommendations = recommendationRepository.findTop10ByUserOrderByPriorityAscCreatedAtDesc(user);
        return recommendations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get unread recommendations
     */
    @Transactional(readOnly = true)
    public List<RecommendationResponse> getUnreadRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Recommendation> recommendations = recommendationRepository.findByUserAndIsRead(user, false);
        return recommendations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mark recommendation as read
     */
    @Transactional
    public void markAsRead(Long recommendationId, Long userId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found"));

        if (!recommendation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Recommendation does not belong to user");
        }

        recommendation.setIsRead(true);
        recommendationRepository.save(recommendation);
    }

    /**
     * Mark all recommendations as read
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Recommendation> recommendations = recommendationRepository.findByUserAndIsRead(user, false);
        for (Recommendation recommendation : recommendations) {
            recommendation.setIsRead(true);
        }
        recommendationRepository.saveAll(recommendations);
    }

    /**
     * Delete old read recommendations
     */
    @Transactional
    public void cleanupOldRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        recommendationRepository.deleteByUserAndIsRead(user, true);
    }

    /**
     * Generate recommendations based on user activity
     */
    @Transactional
    public void generateRecommendations(User user) {
        List<Recommendation> recommendations = new ArrayList<>();

        // Check streak
        UserProgress progress = userProgressRepository.findByUser(user).orElse(null);
        if (progress != null && progress.getCurrentStreak() > 0) {
            recommendations.add(Recommendation.builder()
                    .user(user)
                    .type(Recommendation.RecommendationType.STREAK_MAINTENANCE)
                    .title("Поддерживай серию! 🔥")
                    .description("Твоя текущая серия: " + progress.getCurrentStreak() + " дней. Зайди завтра, чтобы не потерять прогресс!")
                    .priority(1)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        // Check words for review
        LocalDate today = LocalDate.now();
        List<WordLearning> wordsForReview = wordLearningRepository.findByNextReviewBefore(today);
        if (!wordsForReview.isEmpty()) {
            recommendations.add(Recommendation.builder()
                    .user(user)
                    .type(Recommendation.RecommendationType.REVIEW_WORDS)
                    .title("Пора повторить слова! 📚")
                    .description("У тебя " + wordsForReview.size() + " слов для повторения сегодня")
                    .priority(2)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        // Check weak skills (users who haven't completed exercises)
        recommendations.add(Recommendation.builder()
                .user(user)
                .type(Recommendation.RecommendationType.NEW_EXERCISE)
                .title("Новые упражнения доступны! ✨")
                .description("Попробуй новые упражнения для практики")
                .priority(3)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build());

        // Save recommendations (avoid duplicates)
        for (Recommendation recommendation : recommendations) {
            // Check if similar recommendation exists and is unread
            boolean exists = recommendationRepository.findByUserAndIsRead(user, false).stream()
                    .anyMatch(r -> r.getType() == recommendation.getType());

            if (!exists) {
                recommendationRepository.save(recommendation);
            }
        }
    }

    private RecommendationResponse mapToResponse(Recommendation recommendation) {
        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .type(recommendation.getType().name())
                .title(recommendation.getTitle())
                .description(recommendation.getDescription())
                .language(recommendation.getLanguage())
                .difficulty(recommendation.getDifficulty())
                .priority(recommendation.getPriority())
                .isRead(recommendation.getIsRead())
                .createdAt(recommendation.getCreatedAt())
                .expiresAt(recommendation.getExpiresAt())
                .build();
    }
}
