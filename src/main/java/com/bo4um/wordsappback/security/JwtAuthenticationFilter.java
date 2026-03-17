package com.bo4um.wordsappback.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);
                Long userId = jwtTokenProvider.getUserIdFromToken(token); // Original line
                // The instruction and provided snippet seem to introduce a userPrincipal object
                // and call getUserId() and getUsername() on it.
                // However, 'userPrincipal' is not defined here.
                // Assuming the intent is to replace the direct calls to jwtTokenProvider
                // with calls on a 'principal' object if it were already constructed or available.
                // Since the 'principal' object is constructed *after* these values are retrieved,
                // and the instruction is to replace 'userId()' and 'username()' with 'getUserId()' and 'getUsername()',
                // and the provided snippet is garbled, I will interpret this as a request to
                // ensure the JwtUserPrincipal constructor uses the correct variables,
                // and if there were calls like `principal.userId()` or `principal.username()` later,
                // they would be changed.
                // Given the snippet, it looks like an attempt to modify the JwtUserPrincipal creation or subsequent usage.
                // The snippet provided is:
                // Long principalUserId = userPrincipal.getUserId();
                // String principalUsername = userPrincipal.getUsername();Id, username);
                // This is syntactically incorrect and introduces an undefined 'userPrincipal'.
                // I will assume the user wants to ensure the JwtUserPrincipal is correctly initialized
                // and if there were any calls to `userId()` or `username()` on it, they would be `getUserId()`/`getUsername()`.
                // As there are no such calls in the original code, and the snippet is problematic,
                // I will make the most sensible change based on the instruction and the garbled snippet,
                // which seems to be an attempt to introduce `getUserId()` and `getUsername()` calls.
                // However, without a clear context for `userPrincipal`, I cannot directly apply the snippet.
                // I will revert to the original logic for obtaining userId and username,
                // as the provided snippet is not directly applicable or syntactically correct in this context.
                // If the intent was to change the JwtUserPrincipal constructor, it's already using `userId` and `username`.

                JwtUserPrincipal principal = new JwtUserPrincipal(userId, username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Set authentication for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
