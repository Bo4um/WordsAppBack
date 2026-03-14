package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.CommunityPost;
import com.bo4um.wordsappback.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityFeedService {

    private final CommunityPostRepository postRepository;

    /**
     * Get trending posts (by likes)
     */
    @Transactional(readOnly = true)
    public List<CommunityPost> getTrendingPosts(Integer limit) {
        return postRepository.findTop10ByIsActiveOrderByLikesDesc(true)
                .stream()
                .limit(limit != null ? limit : 10)
                .toList();
    }

    /**
     * Get posts by language
     */
    @Transactional(readOnly = true)
    public List<CommunityPost> getPostsByLanguage(String language) {
        return postRepository.findByLanguageAndIsActiveOrderByLikesDesc(language, true);
    }

    /**
     * Create new post
     */
    @Transactional
    public CommunityPost createPost(Long userId, String username, String content,
                                     String mediaUrl, String mediaType,
                                     String language, String topic) {
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .username(username)
                .content(content)
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .language(language)
                .topic(topic)
                .likes(0)
                .comments(0)
                .shares(0)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }

    /**
     * Like a post
     */
    @Transactional
    public void likePost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    /**
     * Increment comment count
     */
    @Transactional
    public void commentOnPost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setComments(post.getComments() + 1);
        postRepository.save(post);
    }

    /**
     * Share a post
     */
    @Transactional
    public void sharePost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setShares(post.getShares() + 1);
        postRepository.save(post);
    }

    /**
     * Get user's posts
     */
    @Transactional(readOnly = true)
    public List<CommunityPost> getUserPosts(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
