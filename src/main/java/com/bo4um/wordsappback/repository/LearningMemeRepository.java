package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.LearningMeme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningMemeRepository extends JpaRepository<LearningMeme, Long> {
    List<LearningMeme> findByLanguageAndIsActiveOrderByLikesDesc(String language, Boolean isActive);
    List<LearningMeme> findByLanguageAndDifficultyAndIsActive(String language, String difficulty, Boolean isActive);
    List<LearningMeme> findTop10ByIsActiveOrderByCreatedAtDesc(Boolean isActive);
    List<LearningMeme> findByMemeTypeAndIsActive(String memeType, Boolean isActive);
}
