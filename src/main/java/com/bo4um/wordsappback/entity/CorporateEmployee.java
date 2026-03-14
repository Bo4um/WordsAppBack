package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Employee link to corporate account
 */
@Entity
@Table(name = "corporate_employee")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long corporateAccountId;

    @Column
    private String department;

    @Column
    private String position;

    @Column
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column
    private LocalDateTime lastActiveAt;
}
