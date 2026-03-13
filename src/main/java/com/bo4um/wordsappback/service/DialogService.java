package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.*;
import com.bo4um.wordsappback.entity.*;
import com.bo4um.wordsappback.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {

    private final RoleplayScenarioRepository scenarioRepository;
    private final ConversationSessionRepository sessionRepository;
    private final ConversationMessageRepository messageRepository;
    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/responses";

    @Transactional(readOnly = true)
    public List<ScenarioResponse> getAllScenarios(String language) {
        List<RoleplayScenario> scenarios;
        if (language != null) {
            scenarios = scenarioRepository.findByLanguageAndIsActiveTrue(language);
        } else {
            scenarios = scenarioRepository.findByIsActiveTrueOrderBySortOrder();
        }

        return scenarios.stream()
                .map(this::mapToScenarioResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScenarioResponse getScenarioById(Long scenarioId) {
        RoleplayScenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + scenarioId));
        return mapToScenarioResponse(scenario);
    }

    @Transactional
    public DialogSessionResponse startDialog(Long userId, StartDialogRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        RoleplayScenario scenario = scenarioRepository.findById(request.getScenarioId())
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + request.getScenarioId()));

        com.bo4um.wordsappback.entity.Character character = null;
        if (request.getCharacterId() != null) {
            character = characterRepository.findById(request.getCharacterId())
                    .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + request.getCharacterId()));
        }

        ConversationSession session = ConversationSession.builder()
                .user(user)
                .scenario(scenario)
                .character(character)
                .topic(request.getTopic())
                .startedAt(LocalDateTime.now())
                .build();

        session = sessionRepository.save(session);

        return mapToSessionResponse(session);
    }

    @Transactional(readOnly = true)
    public List<DialogSessionResponse> getUserSessions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return sessionRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DialogMessageResponse> getSessionHistory(Long sessionId) {
        return messageRepository.findBySessionIdOrderByTimestampAsc(sessionId)
                .stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DialogMessageResponse sendMessage(Long userId, DialogMessageRequest request) {
        ConversationSession session = sessionRepository.findByIdAndUser(request.getSessionId(),
                        userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found")))
                .orElseThrow(() -> new IllegalArgumentException("Session not found or access denied"));

        if (!session.isActive()) {
            throw new IllegalStateException("Session is already ended");
        }

        // Сохраняем сообщение пользователя
        ConversationMessage userMessage = ConversationMessage.builder()
                .session(session)
                .role("user")
                .content(request.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(userMessage);

        // Получаем историю диалога для контекста
        List<ConversationMessage> history = messageRepository.findBySessionIdOrderByTimestampAsc(request.getSessionId());

        // Отправляем запрос к OpenAI
        String aiResponse = callOpenAI(session, history, request.getMessage());

        // Сохраняем ответ AI
        ConversationMessage aiMessage = ConversationMessage.builder()
                .session(session)
                .role("assistant")
                .content(aiResponse)
                .timestamp(LocalDateTime.now())
                .tokenUsage(estimateTokens(aiResponse))
                .build();
        messageRepository.save(aiMessage);

        return mapToMessageResponse(aiMessage);
    }

    /**
     * SSE streaming версия для отправки сообщения
     */
    public Flux<DialogMessageResponse> sendMessageStreaming(Long userId, DialogMessageRequest request) {
        ConversationSession session = sessionRepository.findByIdAndUser(request.getSessionId(),
                        userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found")))
                .orElseThrow(() -> new IllegalArgumentException("Session not found or access denied"));

        if (!session.isActive()) {
            return Flux.error(new IllegalStateException("Session is already ended"));
        }

        // Сохраняем сообщение пользователя
        ConversationMessage userMessage = ConversationMessage.builder()
                .session(session)
                .role("user")
                .content(request.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(userMessage);

        // Получаем историю для контекста
        List<ConversationMessage> history = messageRepository.findBySessionIdOrderByTimestampAsc(request.getSessionId());

        // Streaming запрос к OpenAI
        return streamFromOpenAI(session, history, request.getMessage())
                .flatMap(content -> {
                    ConversationMessage aiMessage = ConversationMessage.builder()
                            .session(session)
                            .role("assistant")
                            .content(content)
                            .timestamp(LocalDateTime.now())
                            .tokenUsage(estimateTokens(content))
                            .build();
                    messageRepository.save(aiMessage);
                    return Mono.just(mapToMessageResponse(aiMessage));
                });
    }

    @Transactional
    public DialogSessionResponse endSession(Long sessionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ConversationSession session = sessionRepository.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new IllegalArgumentException("Session not found or access denied"));

        session.setEndedAt(LocalDateTime.now());
        session = sessionRepository.save(session);

        return mapToSessionResponse(session);
    }

    private ScenarioResponse mapToScenarioResponse(RoleplayScenario scenario) {
        ScenarioResponse response = ScenarioResponse.builder()
                .id(scenario.getId())
                .title(scenario.getTitle())
                .description(scenario.getDescription())
                .language(scenario.getLanguage())
                .difficulty(scenario.getDifficulty())
                .build();

        // Если есть character в сценарии (можно добавить связь), заполняем данные
        // Пока заглушка - можно расширить
        return response;
    }

    private DialogSessionResponse mapToSessionResponse(ConversationSession session) {
        return DialogSessionResponse.builder()
                .id(session.getId())
                .scenarioId(session.getScenario().getId())
                .scenarioTitle(session.getScenario().getTitle())
                .characterId(session.getCharacter() != null ? session.getCharacter().getId() : null)
                .characterName(session.getCharacter() != null ? session.getCharacter().getName() : "AI Tutor")
                .topic(session.getTopic())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .active(session.isActive())
                .build();
    }

    private DialogMessageResponse mapToMessageResponse(ConversationMessage message) {
        return DialogMessageResponse.builder()
                .id(message.getId())
                .role(message.getRole())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .tokenUsage(message.getTokenUsage())
                .isComplete(true)
                .build();
    }

    private String callOpenAI(ConversationSession session, List<ConversationMessage> history, String userMessage) {
        try {
            Map<String, Object> requestBody = buildOpenAIRequestBody(session, history, userMessage);

            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            log.debug("OpenAI Dialog Request: {}", jsonRequest);

            Map<String, Object> response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .header("Authorization", "Bearer " + getOpenAIKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("output")) {
                throw new RuntimeException("Empty response from OpenAI API");
            }

            return extractContentFromOpenAIResponse(response);

        } catch (WebClientResponseException e) {
            log.error("OpenAI API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI API error: " + e.getStatusCode(), e);
        } catch (JsonProcessingException e) {
            log.error("Failed to process JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to process JSON", e);
        }
    }

    private Flux<String> streamFromOpenAI(ConversationSession session, List<ConversationMessage> history, String userMessage) {
        try {
            Map<String, Object> requestBody = buildOpenAIRequestBody(session, history, userMessage);
            requestBody.put("stream", true);

            return webClient.post()
                    .uri(OPENAI_API_URL)
                    .header("Authorization", "Bearer " + getOpenAIKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .map(this::parseSSEEvent);

        } catch (WebClientResponseException e) {
            log.error("OpenAI streaming error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return Flux.error(new RuntimeException("OpenAI API error: " + e.getStatusCode()));
        }
    }

    private Map<String, Object> buildOpenAIRequestBody(ConversationSession session,
                                                        List<ConversationMessage> history,
                                                        String userMessage) {
        String systemPrompt = buildSystemPrompt(session);

        List<Map<String, Object>> messages = new java.util.ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", systemPrompt
        ));

        // Добавляем историю диалога (последние 10 сообщений для контекста)
        int startIdx = Math.max(0, history.size() - 10);
        for (int i = startIdx; i < history.size(); i++) {
            ConversationMessage msg = history.get(i);
            messages.add(Map.of(
                    "role", msg.getRole(),
                    "content", msg.getContent()
            ));
        }

        return Map.of(
                "model", "gpt-4o-mini",
                "messages", messages,
                "max_tokens", 500,
                "temperature", 0.7,
                "stream", false
        );
    }

    private String buildSystemPrompt(ConversationSession session) {
        RoleplayScenario scenario = session.getScenario();
        com.bo4um.wordsappback.entity.Character character = session.getCharacter();

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI language tutor in a roleplay scenario.\n\n");
        prompt.append("Scenario: ").append(scenario.getTitle()).append("\n");
        prompt.append("Description: ").append(scenario.getDescription()).append("\n");
        prompt.append("Language to practice: ").append(scenario.getLanguage()).append("\n");
        prompt.append("Difficulty level: ").append(scenario.getDifficulty()).append("\n");

        if (character != null) {
            prompt.append("\nYour character:\n");
            prompt.append("Name: ").append(character.getName()).append("\n");
            prompt.append("Description: ").append(character.getDescription()).append("\n");
        }

        prompt.append("\nInstructions:\n");
        prompt.append("- Stay in character and maintain the scenario\n");
        prompt.append("- Use language appropriate for the difficulty level\n");
        prompt.append("- Keep responses concise (2-3 sentences)\n");
        prompt.append("- Encourage the user to continue the conversation\n");
        prompt.append("- Gently correct major grammar mistakes if they occur\n");
        prompt.append("- Be supportive and encouraging\n");

        if (session.getTopic() != null) {
            prompt.append("\nCurrent topic: ").append(session.getTopic());
        }

        return prompt.toString();
    }

    private String extractContentFromOpenAIResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> output = (List<Map<String, Object>>) response.get("output");
            if (output != null && !output.isEmpty()) {
                Map<String, Object> content = output.get(0);
                List<Map<String, Object>> contentList = (List<Map<String, Object>>) content.get("content");
                if (contentList != null && !contentList.isEmpty()) {
                    return (String) contentList.get(0).get("text");
                }
            }
            return "Sorry, I couldn't generate a response.";
        } catch (Exception e) {
            log.error("Failed to extract content from response: {}", e.getMessage());
            return "Sorry, there was an error processing the response.";
        }
    }

    private String parseSSEEvent(String event) {
        // Парсинг SSE событий от OpenAI
        if (event.startsWith("data: ")) {
            String data = event.substring(6);
            try {
                Map<String, Object> jsonData = objectMapper.readValue(data, Map.class);
                // Извлекаем дельту контента
                // Это упрощённая реализация, можно расширить
                return data;
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse SSE event: {}", e.getMessage());
                return event;
            }
        }
        return event;
    }

    private int estimateTokens(String text) {
        // Приблизительная оценка: 1 токен ≈ 4 символа
        return text.length() / 4;
    }

    private String getOpenAIKey() {
        // Получение ключа из environment variable или конфигурации
        return System.getenv("OPENAI_API_KEY");
    }
}
