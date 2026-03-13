package com.bo4um.wordsappback.security;

import com.bo4um.wordsappback.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Пропускаем эндпоинты без rate limiting
        if (shouldSkipRateLimit(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Получаем userId из SecurityContext (JWT токен)
        Long userId = getUserIdFromContext();

        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Проверяем лимит
        String endpoint = method + ":" + uri;
        if (subscriptionService.isRateLimitExceeded(userId, endpoint)) {
            log.warn("Rate limit exceeded for user={}, endpoint={}", userId, endpoint);

            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Rate Limit Exceeded");
            errorResponse.put("message", "You have reached your daily limit. Please upgrade to Premium for unlimited access.");
            errorResponse.put("status", 429);

            objectMapper.writeValue(response.getWriter(), errorResponse);
            return;
        }

        // Записываем использование
        subscriptionService.recordUsage(userId, endpoint);

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipRateLimit(String uri) {
        // Пропускаем публичные эндпоинты и эндпоинты без лимитов
        return uri.startsWith("/api/auth/") ||
               uri.startsWith("/h2-console/") ||
               uri.startsWith("/swagger-ui/") ||
               uri.startsWith("/v3/api-docs/") ||
               uri.equals("/api/word"); // Word API имеет свой кэш
    }

    private Long getUserIdFromContext() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                // Здесь можно получить userId из кастомного UserDetails
                // Пока заглушка - нужно реализовать получение из JWT токена
                return 1L;
            }
        } catch (Exception e) {
            log.debug("Could not extract userId from security context: {}", e.getMessage());
        }
        return null;
    }
}
