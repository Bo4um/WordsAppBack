package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Nano-learning session (2-5 minute micro-lessons)
 */
@Entity
@Table(name = "nanolearning_session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NanoLearningSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String content;

    @Column
    private String contentType; // article, video, pdf, audio

    @Column
    private Integer durationMinutes; // 2-5 minutes

    @Column
    private String language;

    @Column
    private String difficulty;

    @Column(length = 1000)
    private String vocabularyList;

    @Column
    private Integer quizScore;

    @Column
    private Boolean isCompleted;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;
}
