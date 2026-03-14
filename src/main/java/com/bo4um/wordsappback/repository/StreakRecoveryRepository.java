package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.StreakRecovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreakRecoveryRepository extends JpaRepository<StreakRecovery, Long> {
    Optional<StreakRecovery> findByUserId(Long userId);
}
