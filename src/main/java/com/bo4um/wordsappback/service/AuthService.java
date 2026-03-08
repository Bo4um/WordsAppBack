package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.dto.AuthRequest;
import com.bo4um.wordsappback.dto.AuthResponse;
import com.bo4um.wordsappback.dto.RegisterRequest;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.repository.UserRepository;
import com.bo4um.wordsappback.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserProgressService userProgressService;

    /**
     * Register new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("User with username '" + request.getUsername() + "' already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        User saved = userRepository.save(user);
        log.info("User registered with id: {}", saved.getId());

        // Создаём прогресс для нового пользователя
        userProgressService.createProgress(saved);

        String token = jwtTokenProvider.generateToken(saved.getUsername(), saved.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(saved.getUsername())
                .role(saved.getRole().name())
                .build();
    }

    /**
     * Login user
     */
    @Transactional
    public AuthResponse login(AuthRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUsername()));

        // Обновляем streak при входе
        userProgressService.updateStreak(user.getId());

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
