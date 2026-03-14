package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.AdhdLearningProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdhdLearningProfileRepository extends JpaRepository<AdhdLearningProfile, Long> {
    Optional<AdhdLearningProfile> findByUserId(Long userId);
}
