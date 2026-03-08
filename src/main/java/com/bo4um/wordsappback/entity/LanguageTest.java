package com.bo4um.wordsappback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Тест для определения уровня языка
 */
@Entity
@Table(name = "language_test")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название теста
     */
    private String name;

    /**
     * Описание теста
     */
    @Column(length = 1000)
    private String description;

    /**
     * Язык теста (например, "English")
     */
    private String language;

    /**
     * Количество вопросов в тесте
     */
    private Integer totalQuestions;

    /**
     * Минимальный балл для прохождения
     */
    private Integer passingScore;

    /**
     * Флаг активности теста
     */
    @Builder.Default
    private Boolean isActive = true;
}
