package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.Recommendation;
import com.bo4um.wordsappback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserOrderByPriorityAscCreatedAtDesc(User user);
    List<Recommendation> findByUserAndIsRead(User user, Boolean isRead);
    List<Recommendation> findTop10ByUserOrderByPriorityAscCreatedAtDesc(User user);
    void deleteByUserAndIsRead(User user, Boolean isRead);
}
