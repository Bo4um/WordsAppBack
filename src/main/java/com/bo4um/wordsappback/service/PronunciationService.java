package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.PronunciationResponse;
import com.bo4um.wordsappback.entity.PronunciationAttempt;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.repository.PronunciationAttemptRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PronunciationService {

    private final PronunciationAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Value("${openai.api.key:}")
    private String openAiKey;

    @Value("${pronunciation.storage.path:./audio-uploads}")
    private String storagePath;

    // Max audio file size: 25MB (Whisper limit)
    private static final long MAX_AUDIO_SIZE = 25 * 1024 * 1024;

    // Allowed audio types
    private static final List<String> ALLOWED_AUDIO_TYPES = List.of(
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/webm", "audio/ogg", "audio/mp4"
    );

    /**
     * Submit pronunciation attempt for analysis
     */
    @Transactional
    public PronunciationResponse submitPronunciation(Long userId, MultipartFile audioFile, String targetPhrase) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        validateAudioFile(audioFile);

        // Create attempt record
        PronunciationAttempt attempt = PronunciationAttempt.builder()
                .user(user)
                .targetPhrase(targetPhrase)
                .status(PronunciationAttempt.PronunciationStatus.PENDING)
                .attemptedAt(LocalDateTime.now())
                .build();

        // Save audio file
        try {
            String audioPath = saveAudioFile(audioFile, userId);
            attempt.setAudioPath(audioPath);
        } catch (IOException e) {
            log.error("Failed to save audio file", e);
            throw new RuntimeException("Failed to save audio file", e);
        }

        attempt = attemptRepository.save(attempt);
        log.info("Submitted pronunciation attempt: {} for user: {}", attempt.getId(), userId);

        // Analyze pronunciation (in production, call Whisper API)
        analyzePronunciation(attempt, audioFile);

        return mapToResponse(attempt);
    }

    /**
     * Get pronunciation history for user
     */
    @Transactional(readOnly = true)
    public List<PronunciationResponse> getUserAttempts(Long userId) {
        List<PronunciationAttempt> attempts = attemptRepository.findByUserIdOrderByAttemptedAtDesc(userId);
        return attempts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get specific attempt by ID
     */
    @Transactional(readOnly = true)
    public PronunciationResponse getAttempt(Long attemptId, Long userId) {
        PronunciationAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Attempt does not belong to user");
        }

        return mapToResponse(attempt);
    }

    /**
     * Get pronunciation statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPronunciationStats(Long userId) {
        List<PronunciationAttempt> attempts = attemptRepository.findByUserIdOrderByAttemptedAtDesc(userId);

        long total = attempts.size();
        long completed = attempts.stream()
                .filter(a -> a.getStatus() == PronunciationAttempt.PronunciationStatus.COMPLETED)
                .count();

        double avgScore = attempts.stream()
                .filter(a -> a.getAccuracyScore() != null)
                .mapToInt(PronunciationAttempt::getAccuracyScore)
                .average()
                .orElse(0.0);

        long goodAttempts = attempts.stream()
                .filter(a -> a.getAccuracyScore() != null && a.getAccuracyScore() >= 80)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAttempts", total);
        stats.put("completedAttempts", completed);
        stats.put("averageScore", Math.round(avgScore * 10.0) / 10.0);
        stats.put("goodAttempts", goodAttempts);
        stats.put("improvementRate", total > 0 ? Math.round((double) goodAttempts / total * 100) : 0);

        return stats;
    }

    /**
     * Analyze pronunciation using Whisper API
     * In production, this calls OpenAI Whisper API
     */
    private void analyzePronunciation(PronunciationAttempt attempt, MultipartFile audioFile) {
        try {
            // In production: Call Whisper API
            // String recognizedText = callWhisperAPI(audioFile.getBytes());

            // For now: Simulate Whisper transcription
            String recognizedText = simulateWhisperTranscription(attempt.getTargetPhrase());

            attempt.setRecognizedText(recognizedText);
            attempt.setStatus(PronunciationAttempt.PronunciationStatus.COMPLETED);

            // Calculate accuracy
            int accuracy = calculateAccuracy(attempt.getTargetPhrase(), recognizedText);
            attempt.setAccuracyScore(accuracy);

            // Generate feedback
            attempt.setFeedback(generateFeedback(accuracy, attempt.getTargetPhrase(), recognizedText));

            attemptRepository.save(attempt);
            log.info("Analyzed pronunciation: {} - accuracy: {}%", attempt.getId(), accuracy);

        } catch (Exception e) {
            log.error("Failed to analyze pronunciation: {}", attempt.getId(), e);
            attempt.setStatus(PronunciationAttempt.PronunciationStatus.FAILED);
            attemptRepository.save(attempt);
        }
    }

    /**
     * Simulate Whisper API transcription
     * In production, integrate with real Whisper API
     */
    private String simulateWhisperTranscription(String targetPhrase) {
        log.info("Simulating Whisper transcription for: {}", targetPhrase);

        // In production:
        // return callWhisperAPI(audioBytes);

        // Simulate some variation in transcription
        return targetPhrase; // Perfect match for demo
    }

    /**
     * Calculate accuracy score between target and recognized text
     */
    private int calculateAccuracy(String target, String recognized) {
        if (target == null || recognized == null) return 0;

        String normalizedTarget = normalizeText(target);
        String normalizedRecognized = normalizeText(recognized);

        if (normalizedTarget.equals(normalizedRecognized)) return 100;

        // Calculate Levenshtein distance
        int distance = levenshteinDistance(normalizedTarget, normalizedRecognized);
        int maxLength = Math.max(normalizedTarget.length(), normalizedRecognized.length());

        return Math.max(0, 100 - (distance * 100 / maxLength));
    }

    /**
     * Generate feedback based on accuracy
     */
    private String generateFeedback(int accuracy, String target, String recognized) {
        if (accuracy >= 90) {
            return "Excellent pronunciation! 🎉";
        } else if (accuracy >= 80) {
            return "Good job! Keep practicing. 👍";
        } else if (accuracy >= 60) {
            return "Not bad, but there's room for improvement. Try again! 💪";
        } else {
            return "Keep practicing! Listen to the audio again and try to match the sounds. 📚";
        }
    }

    /**
     * Validate audio file
     */
    private void validateAudioFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Audio file is empty");
        }

        if (file.getSize() > MAX_AUDIO_SIZE) {
            throw new IllegalArgumentException("Audio file size exceeds limit (max 25MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_AUDIO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid audio format. Allowed: MP3, WAV, WebM, OGG, MP4");
        }
    }

    /**
     * Save audio file to storage
     */
    private String saveAudioFile(MultipartFile file, Long userId) throws IOException {
        Path userDir = Paths.get(storagePath, userId.toString());
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = userDir.resolve(fileName);
        file.transferTo(filePath);

        return filePath.toString();
    }

    /**
     * Normalize text for comparison
     */
    private String normalizeText(String text) {
        if (text == null) return "";
        return text.trim().toLowerCase()
                .replaceAll("[^a-zа-яё0-9\\s]", "")
                .replaceAll("\\s+", " ");
    }

    /**
     * Calculate Levenshtein distance
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[s1.length()][s2.length()];
    }

    private PronunciationResponse mapToResponse(PronunciationAttempt attempt) {
        return PronunciationResponse.builder()
                .id(attempt.getId())
                .targetPhrase(attempt.getTargetPhrase())
                .recognizedText(attempt.getRecognizedText())
                .accuracyScore(attempt.getAccuracyScore())
                .feedback(attempt.getFeedback())
                .isGood(attempt.getAccuracyScore() != null && attempt.getAccuracyScore() >= 80)
                .status(attempt.getStatus().name())
                .attemptedAt(attempt.getAttemptedAt())
                .build();
    }
}
