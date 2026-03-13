package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Weekly challenge for users
 */
@Entity
@Table(name = "weekly_challenge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeType type;

    @Column(nullable = false)
    private Integer targetValue; // e.g., 100 words, 7 days streak

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer rewardPoints;

    @Column(nullable = false)
    private Boolean isActive;

    public enum ChallengeType {
        WORDS_LEARNED,     // Learn X words
        STREAK_DAYS,       // Maintain X days streak
        EXERCISES_DONE,    // Complete X exercises
        DIALOGS_COMPLETED, // Complete X dialog sessions
        PRONUNCIATION      // Practice pronunciation X times
    }
}
