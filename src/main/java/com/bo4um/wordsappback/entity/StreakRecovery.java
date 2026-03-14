package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Streak recovery tokens for users who miss a day
 */
@Entity
@Table(name = "streak_recovery")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreakRecovery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private Integer tokensCount;

    @Column
    private Integer maxTokens;

    @Column
    private LocalDate lastUsedDate;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column
    private LocalDate expiresAt;
}
