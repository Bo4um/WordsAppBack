package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.UserChallengeProgress;
import com.bo4um.wordsappback.entity.WeeklyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallengeProgressRepository extends JpaRepository<UserChallengeProgress, Long> {
    List<UserChallengeProgress> findByUser(User user);
    Optional<UserChallengeProgress> findByUserAndChallenge(User user, WeeklyChallenge challenge);
    List<UserChallengeProgress> findByChallenge(WeeklyChallenge challenge);
}
