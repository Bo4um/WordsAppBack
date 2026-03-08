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
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Прогресс изучения словаря по языкам
 */
@Entity
@Table(name = "dictionary_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Название словаря (язык), например: "English", "Spanish"
     */
    private String dictionaryName;

    /**
     * Количество изученных слов в этом словаре
     */
    @Builder.Default
    private Integer wordsLearned = 0;

    /**
     * Общее количество слов в словаре (если известно)
     */
    private Integer totalWords;

    /**
     * Дата последнего обновления
     */
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
