package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Corporate/B2B account for team learning
 */
@Entity
@Table(name = "corporate_account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column
    private String industry;

    @Column(nullable = false, unique = true)
    private String accountCode; // Unique code for employees to join

    @Column
    private Integer maxEmployees;

    @Column
    @Builder.Default
    private Integer currentEmployees = 0;

    @Column
    private LocalDateTime subscriptionEndDate;

    @Column
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
