package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.WeeklyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeeklyChallengeRepository extends JpaRepository<WeeklyChallenge, Long> {
    List<WeeklyChallenge> findByIsActiveTrueOrderByStartDateDesc();
    List<WeeklyChallenge> findByEndDateAfterAndIsActiveTrue(LocalDate date);
}
