package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.ConversationMessage;
import com.bo4um.wordsappback.entity.ConversationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    List<ConversationMessage> findBySessionOrderByTimestampAsc(ConversationSession session);
    List<ConversationMessage> findBySessionIdOrderByTimestampAsc(Long sessionId);
    void deleteBySessionId(Long sessionId);
}
