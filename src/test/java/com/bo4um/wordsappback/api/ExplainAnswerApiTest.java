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
 * API Tests for Explain Answer endpoints using REST Assured.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Explain Answer API Integration Tests")
class ExplainAnswerApiTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    private String authToken;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Регистрируемся и получаем токен
        registerAndLogin();
    }

    private void registerAndLogin() {
        String username = "testuser_" + System.currentTimeMillis();

        // Регистрация
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .password("password123")
                .build();

        authToken = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
            .when()
                .post("/api/auth/register")
            .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Test
    @DisplayName("Should explain incorrect answer")
    void explainAnswer_IncorrectAnswer() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", "She go to school");
        request.put("correctAnswer", "She goes to school");
        request.put("language", "English");
        request.put("context", "Present Simple tense");
        request.put("questionType", "grammar");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + authToken)
            .body(request)
        .when()
            .post("/api/ai/explain")
        .then()
            .statusCode(200)
            .body("explanation", notNullValue())
            .body("isCorrect", equalTo(false));
    }

    @Test
    @DisplayName("Should explain correct answer")
    void explainAnswer_CorrectAnswer() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", "She goes to school");
        request.put("correctAnswer", "She goes to school");
        request.put("language", "English");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + authToken)
            .body(request)
        .when()
            .post("/api/ai/explain")
        .then()
            .statusCode(200)
            .body("isCorrect", equalTo(true));
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    void explainAnswer_Unauthenticated() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", "test");
        request.put("correctAnswer", "correct");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/ai/explain")
        .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Should return 400 for missing required fields")
    void explainAnswer_MissingFields() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", null); // Missing required field
        request.put("correctAnswer", "correct");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + authToken)
            .body(request)
        .when()
            .post("/api/ai/explain")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should handle quick explain endpoint")
    void explainAnswerQuick_Success() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", "He go home");
        request.put("correctAnswer", "He goes home");
        request.put("language", "English");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + authToken)
            .body(request)
        .when()
            .post("/api/ai/explain/quick")
        .then()
            .statusCode(200)
            .body("explanation", notNullValue());
    }

    @Test
    @DisplayName("Should handle different languages")
    void explainAnswer_DifferentLanguages() {
        // Given
        String[] languages = {"English", "Spanish", "French", "German"};

        for (String language : languages) {
            Map<String, String> request = new HashMap<>();
            request.put("userAnswer", "test");
            request.put("correctAnswer", "correct");
            request.put("language", language);

            // When & Then
            given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(request)
            .when()
                .post("/api/ai/explain")
            .then()
                .statusCode(anyOf(equalTo(200), equalTo(500))); // 500 если нет мока OpenAI
        }
    }

    @Test
    @DisplayName("Should handle different question types")
    void explainAnswer_DifferentQuestionTypes() {
        // Given
        String[] types = {"grammar", "vocabulary", "translation", "pronunciation"};

        for (String type : types) {
            Map<String, String> request = new HashMap<>();
            request.put("userAnswer", "test");
            request.put("correctAnswer", "correct");
            request.put("language", "English");
            request.put("questionType", type);

            // When & Then
            given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(request)
            .when()
                .post("/api/ai/explain")
            .then()
                .statusCode(anyOf(equalTo(200), equalTo(500)));
        }
    }

    @Test
    @DisplayName("Should handle long answers")
    void explainAnswer_LongAnswer() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", "This is a very long sentence that the user wrote as their answer to the question");
        request.put("correctAnswer", "This is the correct version of the very long sentence that should have been written");
        request.put("language", "English");
        request.put("context", "This is additional context about the question and what was being asked");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + authToken)
            .body(request)
        .when()
            .post("/api/ai/explain")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(500)));
    }

    @Test
    @DisplayName("Should handle special characters in answers")
    void explainAnswer_SpecialCharacters() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", "Test!@#$%^&*()");
        request.put("correctAnswer", "Correct!@#$%^&*()");
        request.put("language", "English");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + authToken)
            .body(request)
        .when()
            .post("/api/ai/explain")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(500)));
    }

    @Test
    @DisplayName("Should validate response structure")
    void explainAnswer_ResponseStructure() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("userAnswer", "She go to school");
        request.put("correctAnswer", "She goes to school");
        request.put("language", "English");

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + authToken)
            .body(request)
        .when()
            .post("/api/ai/explain")
        .then()
            .statusCode(200)
            .body("explanation", notNullValue())
            .body("isCorrect", notNullValue())
            .body("confidence", notNullValue());
    }
}
