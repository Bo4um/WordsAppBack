package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);
    List<CommunityPost> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<CommunityPost> findByLanguageAndIsActiveOrderByLikesDesc(String language, Boolean isActive);
    List<CommunityPost> findTop10ByIsActiveOrderByLikesDesc(Boolean isActive);
}
