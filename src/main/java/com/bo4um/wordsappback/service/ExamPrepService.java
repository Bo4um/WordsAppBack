package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.ExamPrepTest;
import com.bo4um.wordsappback.repository.ExamPrepTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamPrepService {

    private final ExamPrepTestRepository testRepository;

    /**
     * Submit practice test
     */
    @Transactional
    public ExamPrepTest submitTest(Long userId, String examType, String section,
                                    Integer score, Integer maxScore) {
        String feedback = generateFeedback(score, maxScore);
        String weakAreas = identifyWeakAreas(score, maxScore, section);

        ExamPrepTest test = ExamPrepTest.builder()
                .userId(userId)
                .examType(examType)
                .section(section)
                .score(score)
                .maxScore(maxScore)
                .feedback(feedback)
                .weakAreas(weakAreas)
                .completedAt(LocalDateTime.now())
                .build();

        return testRepository.save(test);
    }

    /**
     * Get user's test history
     */
    @Transactional(readOnly = true)
    public List<ExamPrepTest> getUserTests(Long userId, String examType) {
        return testRepository.findByUserIdAndExamTypeOrderByCompletedAtDesc(userId, examType);
    }

    /**
     * Get average score by section
     */
    @Transactional(readOnly = true)
    public double getAverageScore(Long userId, String examType, String section) {
        List<ExamPrepTest> tests = testRepository.findByUserIdAndExamTypeAndSection(userId, examType, section);
        return tests.stream()
                .mapToInt(ExamPrepTest::getScore)
                .average()
                .orElse(0.0);
    }

    private String generateFeedback(Integer score, Integer maxScore) {
        double percentage = (double) score / maxScore * 100;
        if (percentage >= 80) return "Excellent! You're ready for the exam.";
        if (percentage >= 60) return "Good progress. Focus on weak areas.";
        return "Keep practicing. Review study materials.";
    }

    private String identifyWeakAreas(Integer score, Integer maxScore, String section) {
        double percentage = (double) score / maxScore * 100;
        if (percentage >= 80) return "No significant weak areas";
        return section + " needs improvement";
    }
}
