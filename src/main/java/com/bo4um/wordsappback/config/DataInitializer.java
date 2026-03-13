package com.bo4um.wordsappback.config;

import com.bo4um.wordsappback.entity.Character;
import com.bo4um.wordsappback.entity.LanguageTest;
import com.bo4um.wordsappback.entity.TestQuestion;
import com.bo4um.wordsappback.entity.User;
import com.bo4um.wordsappback.repository.CharacterRepository;
import com.bo4um.wordsappback.repository.LanguageTestRepository;
import com.bo4um.wordsappback.repository.TestQuestionRepository;
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
    private final LanguageTestRepository languageTestRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initCharacters() {
        return args -> {
            if (characterRepository.count() == 0) {
                log.info("Initializing default characters...");

                Character dimas = new Character();
                dimas.setName("Dimas");
                dimas.setSex("male");
                dimas.setDescription("Friendly and enthusiastic language learner");
                dimas.setIsSystem(true);
                dimas.setIsActive(true);
                dimas.setSortOrder(1);
                characterRepository.save(dimas);

                Character anna = new Character();
                anna.setName("Anna");
                anna.setSex("female");
                anna.setDescription("Cheerful and patient study companion");
                anna.setIsSystem(true);
                anna.setIsActive(true);
                anna.setSortOrder(2);
                characterRepository.save(anna);

                log.info("Default characters initialized");
            }
        };
    }

    @Bean
    CommandLineRunner initUsers() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Initializing default users...");

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);

                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user"));
                user.setRole(User.Role.USER);
                userRepository.save(user);

                log.info("Default users initialized");
            }
        };
    }

    @Bean
    CommandLineRunner initTests() {
        return args -> {
            if (languageTestRepository.count() == 0) {
                log.info("Initializing language tests...");

                LanguageTest englishTest = new LanguageTest();
                englishTest.setName("English Placement Test");
                englishTest.setDescription("Test to determine your English level (A1-B2)");
                englishTest.setLanguage("English");
                englishTest.setTotalQuestions(10);
                englishTest.setPassingScore(50);
                englishTest.setIsActive(true);
                languageTestRepository.save(englishTest);

                log.info("Language tests initialized");
            }
        };
    }
}
