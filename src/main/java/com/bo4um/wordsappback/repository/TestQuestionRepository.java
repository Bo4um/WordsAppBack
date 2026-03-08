package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.LanguageTest;
import com.bo4um.wordsappback.entity.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

    List<TestQuestion> findByTestIdOrderByOrderNumberAsc(Long testId);

    List<TestQuestion> findByTestOrderByOrderNumberAsc(LanguageTest test);
}
