package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.WordLearningRequest;
import com.bo4um.wordsappback.dto.WordLearningResponse;
import com.bo4um.wordsappback.entity.DictionaryProgress;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.entity.WordLearning;
import com.bo4um.wordsappback.repository.DictionaryProgressRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import com.bo4um.wordsappback.repository.WordLearningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordLearningService {

    private final WordLearningRepository wordLearningRepository;
    private final DictionaryProgressRepository dictionaryProgressRepository;
    private final UserRepository userRepository;
    private final UserProgressService userProgressService;

    /**
     * Отметить слово как изученное
     */
    @Transactional
    public WordLearningResponse markWordAsLearned(Long userId, WordLearningRequest request) {
        log.info("Marking word as learned for user: {}: '{}' in {}", userId, request.getWord(), request.getLanguage());

        // Проверяем, есть ли уже такая запись
        if (wordLearningRepository.existsByUserIdAndWordAndLanguage(userId, request.getWord(), request.getLanguage())) {
            WordLearning existing = wordLearningRepository.findByUserIdAndWordAndLanguage(userId, request.getWord(), request.getLanguage());
            existing.setRepetitions(existing.getRepetitions() + 1);
            existing.setNextReview(calculateNextReview(existing.getRepetitions()));
            existing.setLearnedAt(java.time.LocalDateTime.now());
            
            WordLearning updated = wordLearningRepository.save(existing);
            log.info("Word repetition updated for user: {}: '{}'", userId, request.getWord());
            return toResponse(updated);
        }

        // Создаём новую запись
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        WordLearning wordLearning = WordLearning.builder()
                .user(user)
                .word(request.getWord())
                .language(request.getLanguage())
                .repetitions(1)
                .nextReview(calculateNextReview(1))
                .build();

        WordLearning saved = wordLearningRepository.save(wordLearning);

        // Обновляем прогресс словаря
        updateDictionaryProgress(userId, request.getLanguage());
        
        // Увеличиваем общее количество изученных слов
        userProgressService.incrementWordsLearned(userId, 1);

        log.info("Word marked as learned for user: {}: '{}'", userId, request.getWord());
        return toResponse(saved);
    }

    /**
     * Получить все изученные слова пользователя
     */
    public List<WordLearningResponse> getLearnedWords(Long userId) {
        log.debug("Fetching learned words for user with id: {}", userId);

        List<WordLearning> words = wordLearningRepository.findByUserId(userId);
        return words.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Получить изученные слова по языку
     */
    public List<WordLearningResponse> getLearnedWords(Long userId, String language) {
        log.debug("Fetching learned words for user: {} and language: {}", userId, language);

        List<WordLearning> words = wordLearningRepository.findByUserIdAndLanguage(userId, language);
        return words.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Получить слова для повторения
     */
    public List<WordLearningResponse> getWordsForReview(Long userId) {
        log.debug("Fetching words for review for user with id: {}", userId);

        List<WordLearning> words = wordLearningRepository.findByUserIdAndNextReviewBefore(userId, LocalDate.now());
        return words.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Получить количество изученных слов
     */
    public long getLearnedWordsCount(Long userId) {
        return wordLearningRepository.countByUserId(userId);
    }

    /**
     * Рассчитать дату следующего повторения на основе количества повторений
     * Используем упрощённую систему интервального повторения
     */
    private LocalDate calculateNextReview(int repetitions) {
        // Простая формула: 1 день * repetitions
        return LocalDate.now().plusDays(repetitions);
    }

    /**
     * Обновить прогресс словаря после изучения нового слова
     */
    private void updateDictionaryProgress(Long userId, String language) {
        DictionaryProgress progress = dictionaryProgressRepository.findByUserIdAndDictionaryName(userId, language)
                .orElse(null);

        if (progress == null) {
            // Создаём новый прогресс для этого языка
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

            progress = DictionaryProgress.builder()
                    .user(user)
                    .dictionaryName(language)
                    .wordsLearned(1)
                    .build();
        } else {
            progress.setWordsLearned(progress.getWordsLearned() + 1);
            progress.setLastUpdated(java.time.LocalDateTime.now());
        }

        dictionaryProgressRepository.save(progress);
    }

    private WordLearningResponse toResponse(WordLearning wordLearning) {
        return WordLearningResponse.builder()
                .id(wordLearning.getId())
                .word(wordLearning.getWord())
                .language(wordLearning.getLanguage())
                .learnedAt(wordLearning.getLearnedAt())
                .repetitions(wordLearning.getRepetitions())
                .nextReview(wordLearning.getNextReview())
                .build();
    }
}
