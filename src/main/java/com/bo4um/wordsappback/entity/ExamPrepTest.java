package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IELTS/TOEFL practice test and results
 */
@Entity
@Table(name = "exam_prep_test")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamPrepTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String examType; // IELTS, TOEFL, PTE

    @Column(nullable = false)
    private String section; // Reading, Writing, Listening, Speaking

    @Column
    private Integer score;

    @Column
    private Integer maxScore;

    @Column
    private String feedback;

    @Column
    private String weakAreas; // Areas to improve

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime reviewedAt;
}
