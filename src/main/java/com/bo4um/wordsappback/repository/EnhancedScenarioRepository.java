package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.EnhancedScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnhancedScenarioRepository extends JpaRepository<EnhancedScenario, Long> {
    List<EnhancedScenario> findByCategoryAndIsActive(String category, Boolean isActive);
    List<EnhancedScenario> findByLanguageAndDifficultyAndIsActive(String language, String difficulty, Boolean isActive);
    List<EnhancedScenario> findByIsActiveOrderByCompletionCountDesc(Boolean isActive);
    List<EnhancedScenario> findByCategoryAndLanguageAndIsActive(String category, String language, Boolean isActive);
}
