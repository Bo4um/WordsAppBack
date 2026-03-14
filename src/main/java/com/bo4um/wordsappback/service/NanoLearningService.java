package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.NanoLearningSession;
import com.bo4um.wordsappback.repository.NanoLearningSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NanoLearningService {

    private final NanoLearningSessionRepository sessionRepository;

    /**
     * Generate nano-learning session from content
     */
    @Transactional
    public NanoLearningSession generateNanoSession(String content, String contentType,
                                                    String language, String difficulty) {
        log.info("Generating nano-session from {} content", contentType);

        // In production, this would call AI to generate structured lesson
        // For now, create a basic session

        NanoLearningSession session = NanoLearningSession.builder()
                .title("Nano Lesson: " + contentType)
                .content(content)
                .contentType(contentType)
                .durationMinutes(3) // 2-5 minutes
                .language(language)
                .difficulty(difficulty)
                .vocabularyList("word1, word2, word3")
                .isCompleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        return sessionRepository.save(session);
    }

    /**
     * Complete nano-session with quiz score
     */
    @Transactional
    public NanoLearningSession completeSession(Long sessionId, Integer quizScore) {
        NanoLearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        session.setIsCompleted(true);
        session.setCompletedAt(LocalDateTime.now());
        session.setQuizScore(quizScore);

        return sessionRepository.save(session);
    }

    /**
     * Get incomplete sessions for user
     */
    @Transactional(readOnly = true)
    public List<NanoLearningSession> getIncompleteSessions() {
        return sessionRepository.findByIsCompletedOrderByCreatedAtDesc(false);
    }

    /**
     * Get sessions by language and difficulty
     */
    @Transactional(readOnly = true)
    public List<NanoLearningSession> getSessionsByLevel(String language, String difficulty) {
        return sessionRepository.findByLanguageAndDifficulty(language, difficulty);
    }
}
