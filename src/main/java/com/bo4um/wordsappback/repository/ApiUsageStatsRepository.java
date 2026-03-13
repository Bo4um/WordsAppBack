package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.ApiUsageStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ApiUsageStatsRepository extends JpaRepository<ApiUsageStats, Long> {
    Optional<ApiUsageStats> findByUserIdAndEndpointAndUsageDate(Long userId, String endpoint, LocalDate date);

    @Modifying
    @Query("UPDATE ApiUsageStats a SET a.requestCount = a.requestCount + 1 WHERE a.user.id = :userId AND a.endpoint = :endpoint AND a.usageDate = :date")
    void incrementCount(Long userId, String endpoint, LocalDate date);
}
