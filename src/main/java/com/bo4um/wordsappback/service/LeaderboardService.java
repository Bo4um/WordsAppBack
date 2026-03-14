package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserProgress;
import com.bo4um.wordsappback.repository.UserProgressRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final UserProgressRepository progressRepository;

    /**
     * Get global leaderboard
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getGlobalLeaderboard(Integer limit) {
        List<User> allUsers = userRepository.findAll();

        List<Map<String, Object>> leaderboard = allUsers.stream()
                .map(this::calculateScore)
                .sorted((m1, m2) -> ((Integer) m2.get("score")).compareTo((Integer) m1.get("score")))
                .limit(limit != null ? limit : 50)
                .collect(Collectors.toList());

        // Add rank
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).put("rank", i + 1);
        }

        return leaderboard;
    }

    /**
     * Get leaderboard by category
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLeaderboardByCategory(String category) {
        List<User> allUsers = userRepository.findAll();

        Comparator<Map<String, Object>> comparator = switch (category) {
            case "streak" -> (m1, m2) -> ((Integer) m2.get("streak")).compareTo((Integer) m1.get("streak"));
            case "words" -> (m1, m2) -> ((Integer) m2.get("wordsLearned")).compareTo((Integer) m1.get("wordsLearned"));
            case "exercises" -> (m1, m2) -> ((Integer) m2.get("exercisesCompleted")).compareTo((Integer) m1.get("exercisesCompleted"));
            case "pronunciation" -> (m1, m2) -> ((Integer) m2.get("pronunciationScore")).compareTo((Integer) m1.get("pronunciationScore"));
            default -> (m1, m2) -> ((Integer) m2.get("score")).compareTo((Integer) m1.get("score"));
        };

        List<Map<String, Object>> leaderboard = allUsers.stream()
                .map(this::calculateScore)
                .sorted(comparator)
                .limit(50)
                .collect(Collectors.toList());

        // Add rank
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).put("rank", i + 1);
        }

        return leaderboard;
    }

    /**
     * Get user's rank in category
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserRank(Long userId, String category) {
        List<Map<String, Object>> leaderboard = getLeaderboardByCategory(category);

        for (int i = 0; i < leaderboard.size(); i++) {
            if (((Long) leaderboard.get(i).get("userId")).equals(userId)) {
                Map<String, Object> result = new HashMap<>();
                result.put("rank", i + 1);
                result.put("score", leaderboard.get(i).get("score"));
                result.put("total", leaderboard.size());
                return result;
            }
        }

        return Map.of("rank", -1, "message", "User not found in leaderboard");
    }

    private Map<String, Object> calculateScore(User user) {
        UserProgress progress = progressRepository.findByUser(user).orElse(null);

        int streak = progress != null ? progress.getCurrentStreak() : 0;
        int wordsLearned = progress != null ? progress.getTotalWordsLearned() : 0;
        int score = wordsLearned + (streak * 10);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("score", score);
        result.put("streak", streak);
        result.put("wordsLearned", wordsLearned);
        result.put("exercisesCompleted", new Random().nextInt(100)); // Placeholder
        result.put("pronunciationScore", new Random().nextInt(100)); // Placeholder

        return result;
    }
}
