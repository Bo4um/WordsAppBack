package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.CommunityPost;
import com.bo4um.wordsappback.repository.CommunityPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityFeedService Unit Tests")
class CommunityFeedServiceTest {

    @Mock
    private CommunityPostRepository postRepository;

    @InjectMocks
    private CommunityFeedService feedService;

    private CommunityPost testPost;

    @BeforeEach
    void setUp() {
        testPost = CommunityPost.builder()
                .id(1L)
                .userId(1L)
                .username("testuser")
                .content("Test post")
                .mediaUrl("https://example.com/media.mp4")
                .mediaType("video")
                .language("English")
                .topic("Daily life")
                .likes(10)
                .comments(5)
                .shares(2)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should get trending posts")
    void getTrendingPosts_Success() {
        // Given
        when(postRepository.findTop10ByIsActiveOrderByLikesDesc(true))
                .thenReturn(Arrays.asList(testPost));

        // When
        List<CommunityPost> result = feedService.getTrendingPosts(10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getLikes());
    }

    @Test
    @DisplayName("Should get posts by language")
    void getPostsByLanguage_Success() {
        // Given
        when(postRepository.findByLanguageAndIsActiveOrderByLikesDesc("English", true))
                .thenReturn(Arrays.asList(testPost));

        // When
        List<CommunityPost> result = feedService.getPostsByLanguage("English");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("English", result.get(0).getLanguage());
    }

    @Test
    @DisplayName("Should create post")
    void createPost_Success() {
        // Given
        when(postRepository.save(any(CommunityPost.class))).thenReturn(testPost);

        // When
        CommunityPost result = feedService.createPost(
                1L, "testuser", "Test content",
                "https://example.com/media.mp4", "video",
                "English", "Daily life"
        );

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(postRepository).save(any(CommunityPost.class));
    }

    @Test
    @DisplayName("Should like post")
    void likePost_Success() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(CommunityPost.class))).thenReturn(testPost);

        // When
        feedService.likePost(1L);

        // Then
        verify(postRepository).save(testPost);
        assertEquals(11, testPost.getLikes());
    }

    @Test
    @DisplayName("Should increment comment count")
    void commentOnPost_Success() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(CommunityPost.class))).thenReturn(testPost);

        // When
        feedService.commentOnPost(1L);

        // Then
        verify(postRepository).save(testPost);
        assertEquals(6, testPost.getComments());
    }

    @Test
    @DisplayName("Should share post")
    void sharePost_Success() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(CommunityPost.class))).thenReturn(testPost);

        // When
        feedService.sharePost(1L);

        // Then
        verify(postRepository).save(testPost);
        assertEquals(3, testPost.getShares());
    }

    @Test
    @DisplayName("Should get user posts")
    void getUserPosts_Success() {
        // Given
        when(postRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(testPost));

        // When
        List<CommunityPost> result = feedService.getUserPosts(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should throw exception when post not found")
    void likePost_PostNotFound() {
        // Given
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                feedService.likePost(999L)
        );
    }
}
