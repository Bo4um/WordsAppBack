package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.Exercise;
import com.bo4um.wordsappback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByUserOrderByCreatedAtDesc(User user);
    List<Exercise> findByUserAndIsCompleted(User user, Boolean isCompleted);
    List<Exercise> findByLanguageAndDifficulty(String language, String difficulty);
}
