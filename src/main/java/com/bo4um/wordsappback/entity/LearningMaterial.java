package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User uploaded learning material (image, PDF, etc.)
 */
@Entity
@Table(name = "learning_material")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType; // image/png, application/pdf, etc.

    @Column(nullable = false)
    private Long fileSize; // in bytes

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] content;

    @Column(length = 2000)
    private String extractedText; // Text extracted via OCR

    @Column
    private String language;

    @Column
    private LocalDateTime processedAt;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialStatus status;

    @Column(length = 500)
    private String errorMessage;

    public enum MaterialStatus {
        UPLOADED,       // Just uploaded
        PROCESSING,     // Being processed
        PROCESSED,      // Successfully processed
        FAILED          // Processing failed
    }
}
