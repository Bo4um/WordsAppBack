package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercise")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType type;

    @Column(nullable = false, length = 2000)
    private String question;

    @Column(length = 2000)
    private String correctAnswer;

    private String hint;

    @Column(length = 2000)
    private String explanation;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String difficulty; // A1, A2, B1, B2, C1, C2

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private Boolean isCompleted;

    public enum ExerciseType {
        FILL_IN_BLANK,      // Вставить пропущенное слово
        TRANSLATION,        // Перевести предложение
        SENTENCE_BUILDING,  // Составить предложение из слов
        VOCABULARY_QUIZ,    // Выбрать правильное значение
        GRAMMAR_CHOICE,     // Выбрать правильную грамматическую форму
        LISTENING,          // Написать услышанное
        SPEAKING            // Произнести фразу
    }
}
