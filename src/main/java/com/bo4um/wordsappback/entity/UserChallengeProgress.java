package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User progress in a challenge
 */
@Entity
@Table(name = "user_challenge_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "challenge_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private WeeklyChallenge challenge;

    @Column(nullable = false)
    private Integer currentValue;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column
    private LocalDateTime completedAt;

    @Column
    private Boolean rewardClaimed;
}
