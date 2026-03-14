package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Meme entity for meme-based learning
 */
@Entity
@Table(name = "learning_meme")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningMeme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column
    private String memeType; // e.g., "drake", "distracted_boyfriend", "two_buttons"

    @Column
    private String language;

    @Column
    private String difficulty; // A1, A2, B1, B2, C1, C2

    @Column(length = 500)
    private String culturalContext;

    @Column(length = 1000)
    private String vocabularyWords;

    @Column
    private Integer likes;

    @Column
    private Integer shares;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastUsedAt;
}
