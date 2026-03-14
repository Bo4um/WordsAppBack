package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.UserNudge;
import com.bo4um.wordsappback.repository.UserNudgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NudgeService {

    private final UserNudgeRepository nudgeRepository;

    /**
     * Send contextual nudge to user
     */
    @Transactional
    public UserNudge sendNudge(Long userId, String nudgeType, String message, String context) {
        UserNudge nudge = UserNudge.builder()
                .userId(userId)
                .nudgeType(nudgeType)
                .message(message)
                .context(context)
                .isRead(false)
                .isActioned(false)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Sending nudge to user {}: {}", userId, nudgeType);
        return nudgeRepository.save(nudge);
    }

    /**
     * Send encouragement nudge
     */
    @Transactional
    public UserNudge sendEncouragement(Long userId, String achievement) {
        return sendNudge(userId, "encouragement",
                "🎉 Great job! " + achievement + " Keep up the momentum!",
                "behavior_based");
    }

    /**
     * Send reminder nudge
     */
    @Transactional
    public UserNudge sendReminder(Long userId, String activity) {
        return sendNudge(userId, "reminder",
                "💡 Time for your daily " + activity + "! Just 3 minutes can make a difference.",
                "time_based");
    }

    /**
     * Send challenge nudge
     */
    @Transactional
    public UserNudge sendChallenge(Long userId, String challenge) {
        return sendNudge(userId, "challenge",
                "🏆 New challenge available: " + challenge + ". Think you can do it?",
                "milestone");
    }

    /**
     * Send learning tip
     */
    @Transactional
    public UserNudge sendTip(Long userId, String tip) {
        return sendNudge(userId, "tip",
                "📚 Pro tip: " + tip,
                "behavior_based");
    }

    /**
     * Get unread nudges for user
     */
    @Transactional(readOnly = true)
    public List<UserNudge> getUnreadNudges(Long userId) {
        return nudgeRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
    }

    /**
     * Mark nudge as read
     */
    @Transactional
    public void markAsRead(Long nudgeId) {
        UserNudge nudge = nudgeRepository.findById(nudgeId)
                .orElseThrow(() -> new IllegalArgumentException("Nudge not found"));

        nudge.setIsRead(true);
        nudge.setReadAt(LocalDateTime.now());
        nudgeRepository.save(nudge);
    }

    /**
     * Mark nudge as actioned
     */
    @Transactional
    public void markAsActioned(Long nudgeId) {
        UserNudge nudge = nudgeRepository.findById(nudgeId)
                .orElseThrow(() -> new IllegalArgumentException("Nudge not found"));

        nudge.setIsRead(true);
        nudge.setIsActioned(true);
        nudge.setReadAt(LocalDateTime.now());
        nudge.setActionedAt(LocalDateTime.now());
        nudgeRepository.save(nudge);
    }

    /**
     * Get nudges history
     */
    @Transactional(readOnly = true)
    public List<UserNudge> getNudgeHistory(Long userId, Integer limit) {
        return nudgeRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(limit != null ? limit : 20)
                .toList();
    }
}
