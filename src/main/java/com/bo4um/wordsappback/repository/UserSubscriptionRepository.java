package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByUser(User user);
    Optional<UserSubscription> findByUserId(Long userId);
}
