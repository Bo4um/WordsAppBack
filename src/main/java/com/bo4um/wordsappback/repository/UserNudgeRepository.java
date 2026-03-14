package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.UserNudge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNudgeRepository extends JpaRepository<UserNudge, Long> {
    List<UserNudge> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead);
    List<UserNudge> findByUserIdOrderByCreatedAtDesc(Long userId);
}
