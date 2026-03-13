package com.bo4um.wordsappback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Изученное слово пользователя
 */
@Entity
@Table(name = "word_learning")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordLearning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Само слово или фраза
     */
    private String word;

    /**
     * Язык слова, например: "English", "Spanish"
     */
    private String language;

    /**
     * Дата изучения
     */
    private LocalDateTime learnedAt = LocalDateTime.now();

    /**
     * Количество повторений
     */
    private Integer repetitions = 0;

    /**
     * Дата следующего повторения (для интервального повторения)
     */
    private LocalDate nextReview;
}
