package com.bo4um.wordsappback.entity;

import jakarta.persistence.Column;
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
import lombok.NoArgsConstructor;

/**
 * Вопрос теста
 */
@Entity
@Table(name = "test_question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private LanguageTest test;

    /**
     * Текст вопроса
     */
    @Column(length = 2000)
    private String questionText;

    /**
     * Вариант A
     */
    private String optionA;

    /**
     * Вариант B
     */
    private String optionB;

    /**
     * Вариант C
     */
    private String optionC;

    /**
     * Вариант D
     */
    private String optionD;

    /**
     * Правильный ответ (A, B, C или D)
     */
    private String correctAnswer;

    /**
     * Уровень вопроса (A1, A2, B1, B2, C1, C2)
     */
    private String level;

    /**
     * Баллы за вопрос
     */
    @Builder.Default
    private Integer points = 1;

    /**
     * Порядок вопроса в тесте
     */
    private Integer orderNumber;
}
