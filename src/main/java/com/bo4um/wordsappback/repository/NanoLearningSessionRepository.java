package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.NanoLearningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NanoLearningSessionRepository extends JpaRepository<NanoLearningSession, Long> {
    List<NanoLearningSession> findByIsCompletedOrderByCreatedAtDesc(Boolean isCompleted);
    List<NanoLearningSession> findByLanguageAndDifficulty(String language, String difficulty);
}
