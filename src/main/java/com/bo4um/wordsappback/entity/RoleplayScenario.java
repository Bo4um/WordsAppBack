package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roleplay_scenario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleplayScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String difficulty; // A1, A2, B1, B2, C1, C2

    @Column(length = 4000, nullable = false)
    private String scenarioPrompt; // AI prompt для этого сценария

    private Boolean isActive = true;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
