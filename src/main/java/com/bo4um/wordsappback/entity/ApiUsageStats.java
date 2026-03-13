package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "api_usage_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "endpoint", "usage_date"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiUsageStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String endpoint; // e.g., "/api/dialog/message"

    @Column(nullable = false)
    private LocalDate usageDate;

    @Column(nullable = false)
    private Integer requestCount;

    public void incrementCount() {
        this.requestCount = (this.requestCount != null ? this.requestCount : 0) + 1;
    }
}
