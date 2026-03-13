package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.Friendship;
import com.bo4um.wordsappback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUser(User user);
    List<Friendship> findByFriend(User user);
    Friendship findByUserAndFriend(User user, User friend);
    void deleteByUserAndFriend(User user, User friend);
}
