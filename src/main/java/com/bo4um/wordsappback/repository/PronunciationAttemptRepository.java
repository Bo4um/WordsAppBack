package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.PronunciationAttempt;
import com.bo4um.wordsappback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PronunciationAttemptRepository extends JpaRepository<PronunciationAttempt, Long> {
    List<PronunciationAttempt> findByUserOrderByAttemptedAtDesc(User user);
    List<PronunciationAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);
    long countByUserId(Long userId);
}
