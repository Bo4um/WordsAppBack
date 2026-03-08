package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.WordLearning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WordLearningRepository extends JpaRepository<WordLearning, Long> {

    List<WordLearning> findByUserId(Long userId);

    List<WordLearning> findByUserIdAndLanguage(Long userId, String language);

    boolean existsByUserIdAndWordAndLanguage(Long userId, String word, String language);

    WordLearning findByUserIdAndWordAndLanguage(Long userId, String word, String language);

    List<WordLearning> findByUserIdAndNextReviewBefore(Long userId, LocalDate date);

    long countByUserId(Long userId);
}
