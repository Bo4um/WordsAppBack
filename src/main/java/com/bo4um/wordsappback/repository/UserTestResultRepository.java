package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.UserTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTestResultRepository extends JpaRepository<UserTestResult, Long> {

    List<UserTestResult> findByUserId(Long userId);

    List<UserTestResult> findByUserIdOrderByCompletedAtDesc(Long userId);

    List<UserTestResult> findByUserIdAndTestId(Long userId, Long testId);
}
