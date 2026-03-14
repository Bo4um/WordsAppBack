package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced roleplay scenario with emotion simulation
 */
@Entity
@Table(name = "enhanced_scenario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String category; // relocation, business, travel, daily_life

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String difficulty; // A1, A2, B1, B2, C1, C2

    @Column
    private Integer estimatedDuration; // minutes

    @Column(length = 1000)
    private String learningObjectives;

    @ElementCollection
    @CollectionTable(name = "scenario_emotions", joinColumns = @JoinColumn(name = "scenario_id"))
    @Column(name = "emotion")
    @Builder.Default
    private List<String> emotions = new ArrayList<>();

    @Column(length = 3000)
    private String systemPrompt;

    @Column(nullable = false)
    private Boolean isActive;

    @Column
    private Integer completionCount;

    @Column
    private Double averageRating;
}
