package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.AdhdLearningProfile;
import com.bo4um.wordsappback.repository.AdhdLearningProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdhdLearningService {

    private final AdhdLearningProfileRepository profileRepository;

    /**
     * Get or create ADHD learning profile for user
     */
    @Transactional(readOnly = true)
    public AdhdLearningProfile getOrCreateProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    AdhdLearningProfile newProfile = AdhdLearningProfile.builder()
                            .userId(userId)
                            .adhdModeEnabled(false)
                            .preferredSessionDuration(3) // 3 minutes default
                            .frequentBreaks(true)
                            .breakFrequency(5)
                            .focusMode("flexible")
                            .visualReminders(true)
                            .gamification(true)
                            .gentleReminders(true)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return profileRepository.save(newProfile);
                });
    }

    /**
     * Enable ADHD mode
     */
    @Transactional
    public AdhdLearningProfile enableAdhdMode(Long userId) {
        AdhdLearningProfile profile = getOrCreateProfile(userId);
        profile.setAdhdModeEnabled(true);
        profile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    /**
     * Disable ADHD mode
     */
    @Transactional
    public AdhdLearningProfile disableAdhdMode(Long userId) {
        AdhdLearningProfile profile = getOrCreateProfile(userId);
        profile.setAdhdModeEnabled(false);
        profile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    /**
     * Update session duration preference
     */
    @Transactional
    public AdhdLearningProfile updateSessionDuration(Long userId, Integer durationMinutes) {
        if (durationMinutes < 2 || durationMinutes > 10) {
            throw new IllegalArgumentException("Duration must be between 2 and 10 minutes");
        }

        AdhdLearningProfile profile = getOrCreateProfile(userId);
        profile.setPreferredSessionDuration(durationMinutes);
        profile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    /**
     * Update focus mode
     */
    @Transactional
    public AdhdLearningProfile updateFocusMode(Long userId, String focusMode) {
        AdhdLearningProfile profile = getOrCreateProfile(userId);
        profile.setFocusMode(focusMode);
        profile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    /**
     * Get recommended session length based on profile
     */
    @Transactional(readOnly = true)
    public int getRecommendedSessionLength(Long userId) {
        AdhdLearningProfile profile = getOrCreateProfile(userId);
        return profile.getAdhdModeEnabled() ? 
                profile.getPreferredSessionDuration() : 10; // Default 10 min for non-ADHD
    }

    /**
     * Should show break reminder
     */
    @Transactional(readOnly = true)
    public boolean shouldShowBreakReminder(Long userId, int minutesSinceLastBreak) {
        AdhdLearningProfile profile = getOrCreateProfile(userId);
        return profile.getFrequentBreaks() && 
               minutesSinceLastBreak >= profile.getBreakFrequency();
    }
}
