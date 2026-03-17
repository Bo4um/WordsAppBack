package com.bo4um.wordsappback.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
        // Prevent instantiation
    }

    /**
     * Extracts the userId from the current security context.
     *
     * @return the userId
     * @throws IllegalStateException if the context contains no authentication
     *                               or the principal does not hold a userId
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found in security context");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserPrincipal) {
            return ((JwtUserPrincipal) principal).getUserId();
        }

        throw new IllegalStateException("Authentication principal is not of type JwtUserPrincipal");
    }
}
