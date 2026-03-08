package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.DictionaryProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryProgressRepository extends JpaRepository<DictionaryProgress, Long> {

    List<DictionaryProgress> findByUserId(Long userId);

    Optional<DictionaryProgress> findByUserIdAndDictionaryName(Long userId, String dictionaryName);

    boolean existsByUserIdAndDictionaryName(Long userId, String dictionaryName);
}
