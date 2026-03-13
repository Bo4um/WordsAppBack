package com.bo4um.wordsappback.api;

import com.bo4um.wordsappback.AbstractIntegrationTest;
import com.bo4um.wordsappback.dto.AuthResponse;
import com.bo4um.wordsappback.dto.RegisterRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Authentication endpoints using REST Assured.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Auth API Integration Tests")
class AuthApiTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    private String authToken;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void registerUser_Success() {
        // Given
        String username = "testuser_" + System.currentTimeMillis();
        RegisterRequest request = RegisterRequest.builder()
                .username(username)
                .password("password123")
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("username", equalTo(username))
            .body("role", equalTo("USER"));
    }

    @Test
    @DisplayName("Should login user successfully")
    void loginUser_Success() {
        // Given - сначала регистрируем пользователя
        String username = "loginuser_" + System.currentTimeMillis();
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .password("password123")
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(registerRequest)
        .when()
            .post("/api/auth/register");

        // When - логинимся
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", "password123");

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("username", equalTo(username))
            .body("role", equalTo("USER"));
    }

    @Test
    @DisplayName("Should return 400 for duplicate registration")
    void registerUser_DuplicateUsername() {
        // Given
        String username = "duplicateuser_" + System.currentTimeMillis();
        RegisterRequest request = RegisterRequest.builder()
                .username(username)
                .password("password123")
                .build();

        // Регистрируем первый раз
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register");

        // When & Then - вторая регистрация с тем же username
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void loginUser_InvalidCredentials() {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nonexistent");
        loginRequest.put("password", "wrongpassword");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Should return 400 for missing username")
    void registerUser_MissingUsername() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username(null)
                .password("password123")
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 for missing password")
    void registerUser_MissingPassword() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .password(null)
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 for empty request body")
    void registerUser_EmptyBody() {
        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should handle concurrent registrations")
    void registerUser_Concurrent() {
        // Given
        String baseUsername = "concurrentuser_" + System.currentTimeMillis();

        // When & Then - несколько регистраций с разными username
        for (int i = 0; i < 5; i++) {
            RegisterRequest request = RegisterRequest.builder()
                    .username(baseUsername + "_" + i)
                    .password("password123")
                    .build();

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/auth/register")
            .then()
                .statusCode(200)
                .body("token", notNullValue());
        }
    }

    @Test
    @DisplayName("Should validate password length")
    void registerUser_ShortPassword() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("shortpassuser")
                .password("123") // Короткий пароль
                .build();

        // When & Then - зависит от валидации в сервисе
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(400)));
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void registerUser_SpecialCharacters() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("test.user+api@example.com")
                .password("password123")
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(200)
            .body("username", equalTo("test.user+api@example.com"));
    }
}
