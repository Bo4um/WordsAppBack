package com.bo4um.wordsappback.security;

import java.lang.annotation.*;

/**
 * Аннотация для ограничения количества запросов к API
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * Лимит запросов в день
     */
    int dailyLimit() default 100;

    /**
     * Сообщение при превышении лимита
     */
    String message() default "Rate limit exceeded. Please upgrade to Premium for unlimited access.";
}
