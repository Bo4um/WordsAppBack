package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

    Optional<Character> findByName(String name);

    List<Character> findBySex(String sex);
}
