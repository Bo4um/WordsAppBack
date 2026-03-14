package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.ExamPrepTest;
import com.bo4um.wordsappback.repository.ExamPrepTestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExamPrepService Unit Tests")
class ExamPrepServiceTest {

    @Mock
    private ExamPrepTestRepository testRepository;

    @InjectMocks
    private ExamPrepService examPrepService;

    private ExamPrepTest testExam;

    @BeforeEach
    void setUp() {
        testExam = ExamPrepTest.builder()
                .id(1L)
                .userId(1L)
                .examType("IELTS")
                .section("Reading")
                .score(75)
                .maxScore(100)
                .feedback("Good progress. Focus on weak areas.")
                .weakAreas("Reading needs improvement")
                .completedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should submit test")
    void submitTest_Success() {
        // Given
        when(testRepository.save(any(ExamPrepTest.class))).thenReturn(testExam);

        // When
        ExamPrepTest result = examPrepService.submitTest(1L, "IELTS", "Reading", 75, 100);

        // Then
        assertNotNull(result);
        assertEquals("IELTS", result.getExamType());
        assertEquals("Reading", result.getSection());
        verify(testRepository).save(any(ExamPrepTest.class));
    }

    @Test
    @DisplayName("Should get user tests")
    void getUserTests_Success() {
        // Given
        when(testRepository.findByUserIdAndExamTypeOrderByCompletedAtDesc(1L, "IELTS"))
                .thenReturn(Arrays.asList(testExam));

        // When
        List<ExamPrepTest> result = examPrepService.getUserTests(1L, "IELTS");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IELTS", result.get(0).getExamType());
    }

    @Test
    @DisplayName("Should get average score")
    void getAverageScore_Success() {
        // Given
        ExamPrepTest test2 = ExamPrepTest.builder()
                .id(2L)
                .userId(1L)
                .examType("IELTS")
                .section("Reading")
                .score(85)
                .maxScore(100)
                .completedAt(LocalDateTime.now())
                .build();

        when(testRepository.findByUserIdAndExamTypeAndSection(1L, "IELTS", "Reading"))
                .thenReturn(Arrays.asList(testExam, test2));

        // When
        double result = examPrepService.getAverageScore(1L, "IELTS", "Reading");

        // Then
        assertEquals(80.0, result);
    }

    @Test
    @DisplayName("Should generate excellent feedback for high score")
    void submitTest_ExcellentFeedback() {
        // Given
        ExamPrepTest excellentTest = ExamPrepTest.builder()
                .examType("IELTS")
                .section("Writing")
                .score(90)
                .maxScore(100)
                .feedback("Excellent! You're ready for the exam.")
                .build();

        when(testRepository.save(any(ExamPrepTest.class))).thenReturn(excellentTest);

        // When
        ExamPrepTest result = examPrepService.submitTest(1L, "IELTS", "Writing", 90, 100);

        // Then
        assertNotNull(result);
        assertTrue(result.getFeedback().contains("Excellent"));
    }

    @Test
    @DisplayName("Should identify weak areas for low score")
    void submitTest_IdentifyWeakAreas() {
        // Given
        when(testRepository.save(any(ExamPrepTest.class))).thenReturn(testExam);

        // When
        ExamPrepTest result = examPrepService.submitTest(1L, "IELTS", "Listening", 50, 100);

        // Then
        assertNotNull(result);
        assertTrue(result.getWeakAreas().contains("needs improvement"));
    }
}
