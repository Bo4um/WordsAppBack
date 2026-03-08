package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.UserProgressResponse;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserProgress;
import com.bo4um.wordsappback.repository.UserProgressRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;

    /**
     * Создать прогресс для нового пользователя
     */
    @Transactional
    public UserProgress createProgress(User user) {
        log.info("Creating progress for user: {}", user.getUsername());

        UserProgress progress = UserProgress.builder()
                .user(user)
                .currentStreak(0)
                .longestStreak(0)
                .totalWordsLearned(0)
                .joinDate(LocalDate.now())
                .lastVisitDate(LocalDate.now())
                .build();

        UserProgress saved = userProgressRepository.save(progress);
        log.info("Progress created for user: {} with id: {}", user.getUsername(), saved.getId());
        return saved;
    }

    /**
     * Обновить streak при входе пользователя
     */
    @Transactional
    public UserProgressResponse updateStreak(Long userId) {
        log.info("Updating streak for user with id: {}", userId);

        UserProgress progress = userProgressRepository.findByUserId(userId)
                .orElse(null);

        // Если прогресса нет (старые пользователи) — создаём новый
        if (progress == null) {
            log.info("UserProgress not found for user with id: {}. Creating new progress.", userId);
            // Для этого нужно получить пользователя — передадим только userId
            // Создадим минимальный прогресс без ссылки на User (будет null)
            // В будущем нужно будет доработать
            return createMinimalProgress(userId);
        }

        LocalDate today = LocalDate.now();
        LocalDate lastVisit = progress.getLastVisitDate();

        if (lastVisit == null) {
            // Первый вход
            progress.setLastVisitDate(today);
            progress.setCurrentStreak(1);
            progress.setLongestStreak(1);
        } else if (lastVisit.isBefore(today.minusDays(1))) {
            // Пропустил день - сброс streak
            progress.setCurrentStreak(1);
            progress.setLastVisitDate(today);
        } else if (lastVisit.isEqual(today.minusDays(1))) {
            // Вчерашний визит - увеличиваем streak
            int newStreak = progress.getCurrentStreak() + 1;
            progress.setCurrentStreak(newStreak);
            if (newStreak > progress.getLongestStreak()) {
                progress.setLongestStreak(newStreak);
            }
            progress.setLastVisitDate(today);
        }
        // Если визит сегодня - ничего не меняем

        UserProgress updated = userProgressRepository.save(progress);
        log.info("Streak updated for user with id: {}. Current streak: {}", userId, updated.getCurrentStreak());
        return toResponse(updated);
    }

    /**
     * Получить прогресс пользователя
     */
    public UserProgressResponse getProgress(Long userId) {
        log.debug("Fetching progress for user with id: {}", userId);

        UserProgress progress = userProgressRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserProgress not found for user with id: " + userId));

        return toResponse(progress);
    }

    /**
     * Увеличить количество изученных слов
     */
    @Transactional
    public void incrementWordsLearned(Long userId, int count) {
        log.info("Incrementing words learned for user with id: {} by {}", userId, count);

        UserProgress progress = userProgressRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserProgress not found for user with id: " + userId));

        progress.setTotalWordsLearned(progress.getTotalWordsLearned() + count);
        userProgressRepository.save(progress);
    }

    /**
     * Получить общую статистику
     */
    public UserProgressResponse getStats(Long userId) {
        return getProgress(userId);
    }

    /**
     * Создать минимальный прогресс для существующего пользователя
     */
    private UserProgressResponse createMinimalProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserProgress progress = UserProgress.builder()
                .user(user)
                .currentStreak(1)
                .longestStreak(1)
                .totalWordsLearned(0)
                .joinDate(LocalDate.now())
                .lastVisitDate(LocalDate.now())
                .build();

        UserProgress saved = userProgressRepository.save(progress);
        log.info("Created minimal progress for user: {} with id: {}", user.getUsername(), saved.getId());
        return toResponse(saved);
    }

    private UserProgressResponse toResponse(UserProgress progress) {
        return UserProgressResponse.builder()
                .id(progress.getId())
                .currentStreak(progress.getCurrentStreak())
                .longestStreak(progress.getLongestStreak())
                .lastVisitDate(progress.getLastVisitDate())
                .totalWordsLearned(progress.getTotalWordsLearned())
                .joinDate(progress.getJoinDate())
                .build();
    }
}
