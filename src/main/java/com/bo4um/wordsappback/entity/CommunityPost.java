package com.bo4um.wordsappback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Community post for social learning (audio/video circles)
 */
@Entity
@Table(name = "community_post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String username;

    @Column(length = 1000)
    private String content; // Text caption

    @Column
    private String mediaUrl; // Audio/video URL

    @Column
    private String mediaType; // audio, video

    @Column
    private Integer durationSeconds; // For audio/video

    @Column
    private String language;

    @Column
    private String topic; // What they're practicing

    @Column
    @Builder.Default
    private Integer likes = 0;

    @Column
    @Builder.Default
    private Integer comments = 0;

    @Column
    @Builder.Default
    private Integer shares = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime reviewedAt; // For moderation
}
