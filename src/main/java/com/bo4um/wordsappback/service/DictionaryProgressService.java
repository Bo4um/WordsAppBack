package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.DictionaryProgressResponse;
import com.bo4um.wordsappback.entity.DictionaryProgress;
import com.bo4um.wordsappback.repository.DictionaryProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DictionaryProgressService {

    private final DictionaryProgressRepository dictionaryProgressRepository;

    /**
     * Получить прогресс по всем словарям пользователя
     */
    public List<DictionaryProgressResponse> getDictionaryProgress(Long userId) {
        log.debug("Fetching dictionary progress for user with id: {}", userId);

        List<DictionaryProgress> progressList = dictionaryProgressRepository.findByUserId(userId);
        return progressList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получить прогресс по конкретному словарю
     */
    public DictionaryProgressResponse getDictionaryProgress(Long userId, String dictionaryName) {
        log.debug("Fetching dictionary progress for user: {} and dictionary: {}", userId, dictionaryName);

        DictionaryProgress progress = dictionaryProgressRepository.findByUserIdAndDictionaryName(userId, dictionaryName)
                .orElseThrow(() -> new IllegalArgumentException("DictionaryProgress not found for user: " + userId + " and dictionary: " + dictionaryName));

        return toResponse(progress);
    }

    /**
     * Обновить прогресс словаря
     */
    @Transactional
    public DictionaryProgressResponse updateDictionaryProgress(Long userId, String dictionaryName, Integer wordsCount) {
        log.info("Updating dictionary progress for user: {} and dictionary: {}", userId, dictionaryName);

        DictionaryProgress progress = dictionaryProgressRepository.findByUserIdAndDictionaryName(userId, dictionaryName)
                .orElse(null);

        if (progress == null) {
            // Создаём новый прогресс
            progress = DictionaryProgress.builder()
                    .dictionaryName(dictionaryName)
                    .wordsLearned(wordsCount)
                    .lastUpdated(LocalDateTime.now())
                    .build();
            // Нужно установить userId через setter, так как нет связи в конструкторе
            // Это будет сделано в сервисе пользователя или через отдельный метод
        } else {
            progress.setWordsLearned(wordsCount);
            progress.setLastUpdated(LocalDateTime.now());
        }

        DictionaryProgress updated = dictionaryProgressRepository.save(progress);
        log.info("Dictionary progress updated for user: {} and dictionary: {}", userId, dictionaryName);
        return toResponse(updated);
    }

    /**
     * Увеличить количество слов в словаре
     */
    @Transactional
    public DictionaryProgressResponse incrementWords(Long userId, String dictionaryName, int count) {
        log.info("Incrementing words for user: {} and dictionary: {} by {}", userId, dictionaryName, count);

        DictionaryProgress progress = dictionaryProgressRepository.findByUserIdAndDictionaryName(userId, dictionaryName)
                .orElseThrow(() -> new IllegalArgumentException("DictionaryProgress not found for user: " + userId + " and dictionary: " + dictionaryName));

        progress.setWordsLearned(progress.getWordsLearned() + count);
        progress.setLastUpdated(LocalDateTime.now());

        DictionaryProgress updated = dictionaryProgressRepository.save(progress);
        return toResponse(updated);
    }

    /**
     * Создать прогресс для словаря
     */
    @Transactional
    public DictionaryProgress createDictionaryProgress(Long userId, String dictionaryName) {
        log.info("Creating dictionary progress for user: {} and dictionary: {}", userId, dictionaryName);

        DictionaryProgress progress = DictionaryProgress.builder()
                .dictionaryName(dictionaryName)
                .wordsLearned(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        DictionaryProgress saved = dictionaryProgressRepository.save(progress);
        log.info("Dictionary progress created for user: {} and dictionary: {} with id: {}", userId, dictionaryName, saved.getId());
        return saved;
    }

    private DictionaryProgressResponse toResponse(DictionaryProgress progress) {
        int percentage = 0;
        if (progress.getTotalWords() != null && progress.getTotalWords() > 0) {
            percentage = (int) ((progress.getWordsLearned() * 100.0) / progress.getTotalWords());
        }

        return DictionaryProgressResponse.builder()
                .id(progress.getId())
                .dictionaryName(progress.getDictionaryName())
                .wordsLearned(progress.getWordsLearned())
                .totalWords(progress.getTotalWords())
                .progressPercentage(percentage)
                .lastUpdated(progress.getLastUpdated())
                .build();
    }
}
