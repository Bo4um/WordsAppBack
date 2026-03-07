package com.bo4um.wordsappback.config;

import com.bo4um.wordsappback.entity.Character;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.repository.CharacterRepository;
import com.bo4um.wordsappback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Инициализация базовых персонажей при старте приложения
     */
    @Bean
    CommandLineRunner initCharacters() {
        return args -> {
            if (characterRepository.count() == 0) {
                log.info("Initializing default characters...");

                Character male = Character.builder()
                        .name("Dimas")
                        .sex("male")
                        .build();

                Character female = Character.builder()
                        .name("Anna")
                        .sex("female")
                        .build();

                characterRepository.save(male);
                characterRepository.save(female);

                log.info("Default characters created: Dimas (male), Anna (female)");
            } else {
                log.info("Characters already exist, skipping initialization");
            }
        };
    }

    /**
     * Инициализация тестового пользователя при старте приложения
     */
    @Bean
    CommandLineRunner initUser() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Initializing default user...");

                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .role(User.Role.USER)
                        .build();

                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(User.Role.ADMIN)
                        .build();

                userRepository.save(user);
                userRepository.save(admin);

                log.info("Default users created: user/password, admin/admin");
            } else {
                log.info("Users already exist, skipping initialization");
            }
        };
    }
}
