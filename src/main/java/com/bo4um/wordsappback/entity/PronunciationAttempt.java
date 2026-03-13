package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User pronunciation attempt for practice
 */
@Entity
@Table(name = "pronunciation_attempt")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PronunciationAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String targetPhrase;

    @Column
    private String recognizedText;

    @Column
    private Integer accuracyScore; // 0-100

    @Column
    private String feedback;

    @Column(length = 500)
    private String audioPath; // Path to stored audio file

    @Column
    private LocalDateTime attemptedAt;

    @Enumerated(EnumType.STRING)
    private PronunciationStatus status;

    public enum PronunciationStatus {
        PENDING,      // Audio uploaded, processing
        COMPLETED,    // Analyzed
        FAILED        // Analysis failed
    }
}
