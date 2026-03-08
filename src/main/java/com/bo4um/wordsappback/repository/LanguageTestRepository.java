package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.LanguageTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageTestRepository extends JpaRepository<LanguageTest, Long> {

    List<LanguageTest> findByIsActiveTrue();

    List<LanguageTest> findByLanguage(String language);
}
