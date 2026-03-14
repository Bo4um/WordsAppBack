package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.EnhancedScenarioResponse;
import com.bo4um.wordsappback.entity.EnhancedScenario;
import com.bo4um.wordsappback.repository.EnhancedScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnhancedScenarioService {

    private final EnhancedScenarioRepository scenarioRepository;

    /**
     * Get scenarios by category (relocation, business, travel, etc.)
     */
    @Transactional(readOnly = true)
    public List<EnhancedScenarioResponse> getScenariosByCategory(String category) {
        List<EnhancedScenario> scenarios = scenarioRepository.findByCategoryAndIsActive(category, true);
        return scenarios.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get relocation-specific scenarios
     */
    @Transactional(readOnly = true)
    public List<EnhancedScenarioResponse> getRelocationScenarios(String language) {
        List<EnhancedScenario> scenarios = scenarioRepository.findByCategoryAndLanguageAndIsActive(
                "relocation", language, true);
        return scenarios.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get scenarios with emotion simulation
     */
    @Transactional(readOnly = true)
    public List<EnhancedScenarioResponse> getScenariosWithEmotions(String language, String difficulty) {
        List<EnhancedScenario> scenarios = scenarioRepository.findByLanguageAndDifficultyAndIsActive(
                language, difficulty, true);
        return scenarios.stream()
                .filter(s -> s.getEmotions() != null && !s.getEmotions().isEmpty())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get trending scenarios (by completion count)
     */
    @Transactional(readOnly = true)
    public List<EnhancedScenarioResponse> getTrendingScenarios(Integer limit) {
        List<EnhancedScenario> scenarios = scenarioRepository.findByIsActiveOrderByCompletionCountDesc(true);
        return scenarios.stream()
                .limit(limit != null ? limit : 10)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Increment completion count
     */
    @Transactional
    public void incrementCompletionCount(Long scenarioId) {
        EnhancedScenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found"));

        scenario.setCompletionCount(scenario.getCompletionCount() != null ? 
                scenario.getCompletionCount() + 1 : 1);
        scenarioRepository.save(scenario);
    }

    /**
     * Rate a scenario
     */
    @Transactional
    public void rateScenario(Long scenarioId, Integer rating) {
        EnhancedScenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found"));

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Double currentRating = scenario.getAverageRating();
        Integer count = scenario.getCompletionCount() != null ? scenario.getCompletionCount() : 1;

        // Calculate new average
        Double newRating = ((currentRating != null ? currentRating : 0) * (count - 1) + rating) / count;
        scenario.setAverageRating(newRating);
        scenarioRepository.save(scenario);

        log.info("Rated scenario {} with {} stars", scenarioId, rating);
    }

    private EnhancedScenarioResponse mapToResponse(EnhancedScenario scenario) {
        return EnhancedScenarioResponse.builder()
                .id(scenario.getId())
                .title(scenario.getTitle())
                .description(scenario.getDescription())
                .category(scenario.getCategory())
                .language(scenario.getLanguage())
                .difficulty(scenario.getDifficulty())
                .estimatedDuration(scenario.getEstimatedDuration())
                .learningObjectives(scenario.getLearningObjectives())
                .emotions(scenario.getEmotions())
                .completionCount(scenario.getCompletionCount())
                .averageRating(scenario.getAverageRating())
                .isActive(scenario.getIsActive())
                .build();
    }
}
