package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.ExamPrepTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPrepTestRepository extends JpaRepository<ExamPrepTest, Long> {
    List<ExamPrepTest> findByUserIdAndExamTypeOrderByCompletedAtDesc(Long userId, String examType);
    List<ExamPrepTest> findByUserIdAndExamTypeAndSection(Long userId, String examType, String section);
}
