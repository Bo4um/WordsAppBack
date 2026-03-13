package com.bo4um.wordsappback.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider Unit Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "mySecretKeyForJWTTokenGenerationMustBeLongEnoughForHS256Algorithm";
    private final long testExpiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // Используем reflection для установки private полей
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Should generate valid token")
    void generateToken_Success() {
        // When
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract username from token")
    void getUsername_FromToken() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // When
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should extract role from token")
    void getRole_FromToken() {
        // Given
        String token = jwtTokenProvider.generateToken("admin", "ADMIN");

        // When
        String role = jwtTokenProvider.getRoleFromToken(token);

        // Then
        assertEquals("ADMIN", role);
    }

    @Test
    @DisplayName("Should validate valid token")
    void validateToken_ValidToken() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for expired token")
    void validateToken_ExpiredToken() {
        // Given - создаём просроченный токен
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000); // 1 second in the past

        String expiredToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(key)
                .compact();

        // When
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void validateToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for null token")
    void validateToken_NullToken() {
        // When
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for empty token")
    void validateToken_EmptyToken() {
        // When
        boolean isValid = jwtTokenProvider.validateToken("");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate token with correct claims")
    void generateToken_VerifyClaims() {
        // Given
        String username = "testuser";
        String role = "USER";

        // When
        String token = jwtTokenProvider.generateToken(username, role);
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        String extractedRole = jwtTokenProvider.getRoleFromToken(token);

        // Then
        assertEquals(username, extractedUsername);
        assertEquals(role, extractedRole);
    }

    @Test
    @DisplayName("Should handle different user roles")
    void generateToken_DifferentRoles() {
        // Given
        String adminToken = jwtTokenProvider.generateToken("admin", "ADMIN");
        String userToken = jwtTokenProvider.generateToken("user", "USER");

        // Then
        assertEquals("ADMIN", jwtTokenProvider.getRoleFromToken(adminToken));
        assertEquals("USER", jwtTokenProvider.getRoleFromToken(userToken));
        assertEquals("admin", jwtTokenProvider.getUsernameFromToken(adminToken));
        assertEquals("user", jwtTokenProvider.getUsernameFromToken(userToken));
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void generateToken_SpecialCharacters() {
        // Given
        String username = "test_user@example.com";
        String role = "USER";

        // When
        String token = jwtTokenProvider.generateToken(username, role);
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Token should have correct expiration")
    void generateToken_Expiration() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // When - парсим токен для проверки expiration
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        // Then
        assertNotNull(expiration);
        assertNotNull(issuedAt);
        assertTrue(expiration.after(issuedAt));
    }
}
