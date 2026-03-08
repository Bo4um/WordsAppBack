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

    /**
     * Инициализация базовых персонажей при старте приложения
     */
    @Bean
    CommandLineRunner initCharacters() {
        return args -> {
            if (characterRepository.count() == 0) {
                log.info("Initializing default characters...");

                // Мужские персонажи
                Character dimas = Character.builder()
                        .name("Dimas")
                        .sex("male")
                        .description("Friendly and enthusiastic language learner")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(1)
                        .build();

                Character alex = Character.builder()
                        .name("Alex")
                        .sex("male")
                        .description("Professional teacher with years of experience")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(2)
                        .build();

                Character max = Character.builder()
                        .name("Max")
                        .sex("male")
                        .description("Casual buddy for everyday conversations")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(3)
                        .build();

                Character james = Character.builder()
                        .name("James")
                        .sex("male")
                        .description("British gentleman with proper pronunciation")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(4)
                        .build();

                // Женские персонажи
                Character anna = Character.builder()
                        .name("Anna")
                        .sex("female")
                        .description("Cheerful and patient study companion")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(5)
                        .build();

                Character emma = Character.builder()
                        .name("Emma")
                        .sex("female")
                        .description("Creative storyteller who makes learning fun")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(6)
                        .build();

                Character sophia = Character.builder()
                        .name("Sophia")
                        .sex("female")
                        .description("Expert in grammar and vocabulary building")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(7)
                        .build();

                Character olivia = Character.builder()
                        .name("Olivia")
                        .sex("female")
                        .description("Travel enthusiast who loves sharing cultural insights")
                        .isSystem(true)
                        .isActive(true)
                        .sortOrder(8)
                        .build();

                characterRepository.save(dimas);
                characterRepository.save(alex);
                characterRepository.save(max);
                characterRepository.save(james);
                characterRepository.save(anna);
                characterRepository.save(emma);
                characterRepository.save(sophia);
                characterRepository.save(olivia);

                log.info("Default characters created: 4 male, 4 female");
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

    /**
     * Инициализация теста на определение уровня английского
     */
    @Bean
    CommandLineRunner initLanguageTest() {
        return args -> {
            if (languageTestRepository.count() == 0) {
                log.info("Initializing English placement test...");

                // Создаём тест
                LanguageTest englishTest = LanguageTest.builder()
                        .name("English Placement Test")
                        .description("Test to determine your English level (A1-B2)")
                        .language("English")
                        .totalQuestions(10)
                        .passingScore(50)
                        .isActive(true)
                        .build();

                LanguageTest savedTest = languageTestRepository.save(englishTest);

                // Вопросы A1 (3 вопроса)
                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("Choose the correct form: 'She ___ to school every day.'")
                        .optionA("go")
                        .optionB("goes")
                        .optionC("going")
                        .optionD("gone")
                        .correctAnswer("B")
                        .level("A1")
                        .points(1)
                        .orderNumber(1)
                        .build());

                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("What is the past tense of 'eat'?")
                        .optionA("eated")
                        .optionB("ate")
                        .optionC("eaten")
                        .optionD("eating")
                        .correctAnswer("B")
                        .level("A1")
                        .points(1)
                        .orderNumber(2)
                        .build());

                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("Choose the correct article: 'I have ___ apple.'")
                        .optionA("a")
                        .optionB("an")
                        .optionC("the")
                        .optionD("-")
                        .correctAnswer("B")
                        .level("A1")
                        .points(1)
                        .orderNumber(3)
                        .build());

                // Вопросы A2 (3 вопроса)
                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("Choose the correct form: 'If I ___ rich, I would buy a house.'")
                        .optionA("am")
                        .optionB("was")
                        .optionC("were")
                        .optionD("been")
                        .correctAnswer("C")
                        .level("A2")
                        .points(1)
                        .orderNumber(4)
                        .build());

                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("What is the comparative form of 'good'?")
                        .optionA("gooder")
                        .optionB("better")
                        .optionC("best")
                        .optionD("more good")
                        .correctAnswer("B")
                        .level("A2")
                        .points(1)
                        .orderNumber(5)
                        .build());

                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("Choose the correct preposition: 'I'm interested ___ learning English.'")
                        .optionA("on")
                        .optionB("at")
                        .optionC("in")
                        .optionD("for")
                        .correctAnswer("C")
                        .level("A2")
                        .points(1)
                        .orderNumber(6)
                        .build());

                // Вопросы B1 (2 вопроса)
                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("Choose the correct form: 'By this time next year, I ___ my studies.'")
                        .optionA("will finish")
                        .optionB("will have finished")
                        .optionC("am finishing")
                        .optionD("have finished")
                        .correctAnswer("B")
                        .level("B1")
                        .points(1)
                        .orderNumber(7)
                        .build());

                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("What does the phrasal verb 'give up' mean?")
                        .optionA("to start something")
                        .optionB("to stop trying")
                        .optionC("to continue")
                        .optionD("to succeed")
                        .correctAnswer("B")
                        .level("B1")
                        .points(1)
                        .orderNumber(8)
                        .build());

                // Вопросы B2 (2 вопроса)
                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("Choose the correct inversion: 'Never ___ such a beautiful sunset.'")
                        .optionA("I have seen")
                        .optionB("have I seen")
                        .optionC("I saw")
                        .optionD("did I see")
                        .correctAnswer("B")
                        .level("B2")
                        .points(1)
                        .orderNumber(9)
                        .build());

                testQuestionRepository.save(TestQuestion.builder()
                        .test(savedTest)
                        .questionText("Which sentence is correct?")
                        .optionA("I wish I would know the answer.")
                        .optionB("I wish I knew the answer.")
                        .optionC("I wish I know the answer.")
                        .optionD("I wish I had knew the answer.")
                        .correctAnswer("B")
                        .level("B2")
                        .points(1)
                        .orderNumber(10)
                        .build());

                log.info("English placement test created with 10 questions (A1-B2)");
            } else {
                log.info("Language tests already exist, skipping initialization");
            }
        };
    }
}
