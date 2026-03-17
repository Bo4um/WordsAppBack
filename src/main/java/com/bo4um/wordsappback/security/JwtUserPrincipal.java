package com.bo4um.wordsappback.security;

public class JwtUserPrincipal {
    private final Long userId;
    private final String username;

    public JwtUserPrincipal(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
