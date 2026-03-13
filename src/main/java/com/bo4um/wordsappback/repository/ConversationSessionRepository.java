package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.ConversationSession;
import com.bo4um.wordsappback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationSessionRepository extends JpaRepository<ConversationSession, Long> {
    List<ConversationSession> findByUserOrderByStartedAtDesc(User user);
    List<ConversationSession> findByUserAndEndedAtIsNull(User user);
    Optional<ConversationSession> findByIdAndUser(Long id, User user);
}
