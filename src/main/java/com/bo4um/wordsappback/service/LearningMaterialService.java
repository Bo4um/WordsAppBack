package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.LearningMaterialResponse;
import com.bo4um.wordsappback.entity.LearningMaterial;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.repository.LearningMaterialRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningMaterialService {

    private final LearningMaterialRepository materialRepository;
    private final UserRepository userRepository;

    // Max file size: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // Allowed file types
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    private static final List<String> ALLOWED_PDF_TYPES = List.of(
            "application/pdf"
    );

    /**
     * Upload learning material (image or PDF)
     */
    @Transactional
    public LearningMaterialResponse uploadMaterial(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate file
        validateFile(file);

        // Create material entity
        LearningMaterial material;
        try {
            material = LearningMaterial.builder()
                    .user(user)
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .content(file.getBytes())
                    .uploadedAt(LocalDateTime.now())
                    .status(LearningMaterial.MaterialStatus.UPLOADED)
                    .build();
        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file", e);
        }

        material = materialRepository.save(material);
        log.info("Uploaded material: {} for user: {}", material.getId(), userId);

        // Process asynchronously (in real app, use @Async or message queue)
        processMaterial(material);

        return mapToResponse(material);
    }

    /**
     * Get all materials for user
     */
    @Transactional(readOnly = true)
    public List<LearningMaterialResponse> getUserMaterials(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<LearningMaterial> materials = materialRepository.findByUserOrderByUploadedAtDesc(user);
        return materials.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get material by ID
     */
    @Transactional(readOnly = true)
    public LearningMaterialResponse getMaterial(Long materialId, Long userId) {
        LearningMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        if (!material.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Material does not belong to user");
        }

        return mapToResponse(material);
    }

    /**
     * Delete material
     */
    @Transactional
    public void deleteMaterial(Long materialId, Long userId) {
        LearningMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        if (!material.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Material does not belong to user");
        }

        materialRepository.delete(material);
        log.info("Deleted material: {}", materialId);
    }

    /**
     * Process material - extract text and analyze
     * In production, this would call OCR service and AI for analysis
     */
    @Transactional
    public void processMaterial(LearningMaterial material) {
        material.setStatus(LearningMaterial.MaterialStatus.PROCESSING);
        materialRepository.save(material);

        try {
            // Simulate OCR processing
            // In production: integrate with Tesseract, Google Vision, or Azure OCR
            String extractedText = simulateOCR(material.getContent(), material.getFileType());

            material.setExtractedText(extractedText);
            material.setStatus(LearningMaterial.MaterialStatus.PROCESSED);
            material.setProcessedAt(LocalDateTime.now());

            // TODO: Generate vocabulary and exercises from extracted text
            // This would call AI to analyze the text and create learning materials

            materialRepository.save(material);
            log.info("Processed material: {} - extracted {} characters", material.getId(), extractedText.length());

        } catch (Exception e) {
            log.error("Failed to process material: {}", material.getId(), e);
            material.setStatus(LearningMaterial.MaterialStatus.FAILED);
            material.setErrorMessage(e.getMessage());
            materialRepository.save(material);
        }
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds limit (max 10MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Cannot determine file type");
        }

        boolean isAllowed = ALLOWED_IMAGE_TYPES.contains(contentType) || ALLOWED_PDF_TYPES.contains(contentType);
        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed. Allowed: images (JPEG, PNG, GIF) and PDF");
        }
    }

    /**
     * Simulate OCR text extraction
     * In production, integrate with real OCR service
     */
    private String simulateOCR(byte[] content, String fileType) {
        // This is a placeholder - in production use:
        // - Tesseract (open source)
        // - Google Cloud Vision API
        // - Azure Computer Vision
        // - AWS Textract

        log.info("Simulating OCR for file type: {}", fileType);

        // Return sample text for demonstration
        return """
            [OCR Extracted Text]
            
            This is a sample text that would be extracted from the uploaded image.
            In production, this would use a real OCR service to extract actual text.
            
            The quick brown fox jumps over the lazy dog.
            Practice makes perfect!
            
            Vocabulary words:
            - enthusiastic (adj.): having or showing intense excitement
            - persistent (adj.): continuing firmly despite difficulty
            - achieve (v.): successfully bring about or reach
            """;
    }

    private LearningMaterialResponse mapToResponse(LearningMaterial material) {
        return LearningMaterialResponse.builder()
                .id(material.getId())
                .fileName(material.getFileName())
                .fileType(material.getFileType())
                .fileSize(material.getFileSize())
                .extractedText(material.getExtractedText())
                .language(material.getLanguage())
                .status(material.getStatus().name())
                .isProcessed(material.getStatus() == LearningMaterial.MaterialStatus.PROCESSED)
                .uploadedAt(material.getUploadedAt())
                .processedAt(material.getProcessedAt())
                .build();
    }
}
