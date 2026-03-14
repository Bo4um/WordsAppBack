package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Pragmatic error correction for soft skills coaching
 */
@Entity
@Table(name = "pragmatic_error")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PragmaticError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String userUtterance;

    @Column(nullable = false, length = 1000)
    private String correctedVersion;

    @Column(length = 500)
    private String errorType; // grammar, tone, formality, cultural

    @Column(length = 1000)
    private String explanation;

    @Column
    private String context; // situation where this occurred

    @Column
    private String suggestedAlternatives;

    @Column
    private Integer severityLevel; // 1-5 (5 = most severe)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime reviewedAt;

    @Column
    private Boolean isHelpful;
}
