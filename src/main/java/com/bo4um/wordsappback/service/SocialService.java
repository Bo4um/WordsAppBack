package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.*;
import com.bo4um.wordsappback.entity.*;
import com.bo4um.wordsappback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialService {

    private final FriendshipRepository friendshipRepository;
    private final WeeklyChallengeRepository challengeRepository;
    private final UserChallengeProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;

    // ==================== FRIENDS ====================

    /**
     * Send friend request
     */
    @Transactional
    public void sendFriendRequest(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        // Check if already friends
        Friendship existing = friendshipRepository.findByUserAndFriend(user, friend);
        if (existing != null) {
            throw new IllegalArgumentException("Friendship already exists");
        }

        Friendship friendship = Friendship.builder()
                .user(user)
                .friend(friend)
                .status(Friendship.FriendshipStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        friendshipRepository.save(friendship);
        log.info("Friend request sent from {} to {}", userId, friendId);
    }

    /**
     * Accept friend request
     */
    @Transactional
    public void acceptFriendRequest(Long userId, Long requestId) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!friendship.getFriend().getId().equals(userId)) {
            throw new IllegalArgumentException("Not your friend request");
        }

        friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
        log.info("Friend request {} accepted", requestId);
    }

    /**
     * Get user's friends
     */
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Friendship> friendships = friendshipRepository.findByUser(user);

        return friendships.stream()
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.ACCEPTED)
                .map(f -> FriendResponse.builder()
                        .friendId(f.getFriend().getId())
                        .friendUsername(f.getFriend().getUsername())
                        .friendSince(f.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== LEADERBOARD ====================

    /**
     * Get global leaderboard
     */
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboard(int limit) {
        List<User> allUsers = userRepository.findAll();

        List<LeaderboardEntryResponse> leaderboard = allUsers.stream()
                .map(user -> {
                    UserProgress progress = userProgressRepository.findByUser(user).orElse(null);
                    int totalWords = progress != null ? progress.getTotalWordsLearned() : 0;
                    int streak = progress != null ? progress.getCurrentStreak() : 0;
                    int score = totalWords + (streak * 10); // Score calculation

                    return LeaderboardEntryResponse.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .totalWords(totalWords)
                            .currentStreak(streak)
                            .score(score)
                            .build();
                })
                .sorted(Comparator.comparingInt(LeaderboardEntryResponse::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // Add rank
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        return leaderboard;
    }

    /**
     * Get user's rank
     */
    @Transactional(readOnly = true)
    public RankResponse getUserRank(Long userId) {
        List<LeaderboardEntryResponse> leaderboard = getLeaderboard(1000);

        int userRank = 0;
        int userScore = 0;

        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getUserId().equals(userId)) {
                userRank = i + 1;
                userScore = leaderboard.get(i).getScore();
                break;
            }
        }

        return RankResponse.builder()
                .rank(userRank)
                .score(userScore)
                .totalUsers(leaderboard.size())
                .build();
    }

    // ==================== CHALLENGES ====================

    /**
     * Get active challenges
     */
    @Transactional(readOnly = true)
    public List<ChallengeResponse> getActiveChallenges() {
        LocalDate today = LocalDate.now();
        List<WeeklyChallenge> challenges = challengeRepository.findByEndDateAfterAndIsActiveTrue(today);

        return challenges.stream()
                .map(this::mapToChallengeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get user's challenge progress
     */
    @Transactional(readOnly = true)
    public List<ChallengeProgressResponse> getUserChallengeProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserChallengeProgress> progressList = progressRepository.findByUser(user);

        return progressList.stream()
                .map(progress -> {
                    WeeklyChallenge challenge = progress.getChallenge();
                    int percentage = challenge.getTargetValue() > 0 ?
                            (progress.getCurrentValue() * 100 / challenge.getTargetValue()) : 0;

                    return ChallengeProgressResponse.builder()
                            .challengeId(challenge.getId())
                            .challengeTitle(challenge.getTitle())
                            .challengeType(challenge.getType().name())
                            .targetValue(challenge.getTargetValue())
                            .currentValue(progress.getCurrentValue())
                            .progressPercentage(Math.min(100, percentage))
                            .isCompleted(progress.getIsCompleted())
                            .rewardClaimed(progress.getRewardClaimed())
                            .endDate(challenge.getEndDate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Update challenge progress (called when user completes relevant action)
     */
    @Transactional
    public void updateChallengeProgress(Long userId, WeeklyChallenge.ChallengeType type, int increment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        List<WeeklyChallenge> activeChallenges = challengeRepository.findByEndDateAfterAndIsActiveTrue(today);

        for (WeeklyChallenge challenge : activeChallenges) {
            if (challenge.getType() == type) {
                UserChallengeProgress progress = progressRepository.findByUserAndChallenge(user, challenge)
                        .orElse(null);

                if (progress == null) {
                    // Create new progress
                    progress = UserChallengeProgress.builder()
                            .user(user)
                            .challenge(challenge)
                            .currentValue(increment)
                            .isCompleted(false)
                            .rewardClaimed(false)
                            .build();
                } else {
                    progress.setCurrentValue(progress.getCurrentValue() + increment);

                    // Check if completed
                    if (!progress.getIsCompleted() && progress.getCurrentValue() >= challenge.getTargetValue()) {
                        progress.setIsCompleted(true);
                        progress.setCompletedAt(LocalDateTime.now());
                    }
                }

                progressRepository.save(progress);
            }
        }
    }

    /**
     * Claim challenge reward
     */
    @Transactional
    public void claimReward(Long userId, Long progressId) {
        UserChallengeProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new IllegalArgumentException("Progress not found"));

        if (!progress.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not your challenge progress");
        }

        if (!progress.getIsCompleted()) {
            throw new IllegalArgumentException("Challenge not completed yet");
        }

        if (progress.getRewardClaimed()) {
            throw new IllegalArgumentException("Reward already claimed");
        }

        progress.setRewardClaimed(true);
        progressRepository.save(progress);

        // TODO: Add points to user's account
        log.info("Reward claimed for challenge progress: {}", progressId);
    }

    private ChallengeResponse mapToChallengeResponse(WeeklyChallenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .type(challenge.getType().name())
                .targetValue(challenge.getTargetValue())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .rewardPoints(challenge.getRewardPoints())
                .isActive(challenge.getIsActive())
                .build();
    }
}
