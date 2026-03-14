package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.PragmaticError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PragmaticErrorRepository extends JpaRepository<PragmaticError, Long> {
    List<PragmaticError> findByErrorTypeOrderByCreatedAtDesc(String errorType);
    List<PragmaticError> findTop10ByOrderByCreatedAtDesc();
    List<PragmaticError> findBySeverityLevelGreaterThanEqualOrderByCreatedAtDesc(Integer severity);
}
