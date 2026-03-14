package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.StreakRecovery;
import com.bo4um.wordsappback.entity.UserProgress;
import com.bo4um.wordsappback.repository.StreakRecoveryRepository;
import com.bo4um.wordsappback.repository.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreakRecoveryService {

    private final StreakRecoveryRepository recoveryRepository;
    private final UserProgressRepository userProgressRepository;

    private static final int MAX_RECOVERY_TOKENS = 3;
    private static final int TOKENS_REGENERATED_PER_WEEK = 1;

    /**
     * Use recovery token to save streak
     */
    @Transactional
    public boolean useRecoveryToken(Long userId) {
        Optional<StreakRecovery> recoveryOpt = recoveryRepository.findByUserId(userId);

        if (recoveryOpt.isEmpty()) {
            // Create new recovery record with 1 token
            StreakRecovery recovery = StreakRecovery.builder()
                    .userId(userId)
                    .tokensCount(1)
                    .maxTokens(MAX_RECOVERY_TOKENS)
                    .createdAt(LocalDate.now())
                    .expiresAt(LocalDate.now().plusMonths(1))
                    .build();
            recoveryRepository.save(recovery);
            return false; // No token to use, but created record
        }

        StreakRecovery recovery = recoveryOpt.get();

        // Regenerate tokens weekly
        if (recovery.getLastUsedDate() != null &&
            recovery.getLastUsedDate().plusWeeks(1).isBefore(LocalDate.now())) {
            int newTokens = Math.min(MAX_RECOVERY_TOKENS,
                    recovery.getTokensCount() + TOKENS_REGENERATED_PER_WEEK);
            recovery.setTokensCount(newTokens);
        }

        if (recovery.getTokensCount() <= 0) {
            log.warn("User {} has no recovery tokens", userId);
            return false;
        }

        // Use token
        recovery.setTokensCount(recovery.getTokensCount() - 1);
        recovery.setLastUsedDate(LocalDate.now());
        recoveryRepository.save(recovery);

        log.info("User {} used recovery token. Remaining: {}", userId, recovery.getTokensCount());
        return true;
    }

    /**
     * Get recovery token count
     */
    @Transactional(readOnly = true)
    public int getRecoveryTokens(Long userId) {
        return recoveryRepository.findByUserId(userId)
                .map(StreakRecovery::getTokensCount)
                .orElse(0);
    }

    /**
     * Check if user can recover streak
     */
    @Transactional(readOnly = true)
    public boolean canRecover(Long userId) {
        return getRecoveryTokens(userId) > 0;
    }

    /**
     * Award recovery token (for achievements, purchases, etc.)
     */
    @Transactional
    public void awardToken(Long userId) {
        StreakRecovery recovery = recoveryRepository.findByUserId(userId)
                .orElse(StreakRecovery.builder()
                        .userId(userId)
                        .tokensCount(0)
                        .maxTokens(MAX_RECOVERY_TOKENS)
                        .createdAt(LocalDate.now())
                        .build());

        if (recovery.getTokensCount() < MAX_RECOVERY_TOKENS) {
            recovery.setTokensCount(recovery.getTokensCount() + 1);
            recoveryRepository.save(recovery);
            log.info("Awarded recovery token to user {}. Total: {}", userId, recovery.getTokensCount());
        }
    }
}
