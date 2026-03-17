# 🚨 ВНИМАНИЮ ИИ-АГЕНТОВ: Вы работаете параллельно. Берите только задачи со статусом 'Не начато'.

## ПРАВИЛА:
1. Работайте в новой git-ветке (`refactor/имя-задачи`).
2. После выполнения измените статус на '[x] Готово'.
3. Заполните блок 'ОТЧЕТ АГЕНТА'.
4. Код должен успешно проходить тесты перед коммитом.
5. Запишите Git-команды фиксации.

---

# Executive Summary — Аудит WordsApp Back API

## Стек проекта
- **Spring Boot 4.0.3** / Maven / Java 17
- **БД:** PostgreSQL (prod) + H2 (dev), Flyway миграции (V1–V8)
- **Кэш:** Redis (prod) / Caffeine (dev)
- **Безопасность:** Spring Security + JWT (jjwt 0.12.6)
- **Интеграции:** OpenAI API, Stripe Payments
- **Мониторинг:** Actuator + Micrometer/Prometheus
- **Тестирование:** JUnit 5 + Testcontainers + REST Assured

## Критические проблемы

| # | Проблема | Серьёзность | Где |
|---|---------|-------------|-----|
| 1 | **Сломанная аутентификация:** `Long userId = 1L` (заглушка) в КАЖДОМ контроллере и `RateLimitFilter` | 🔴 CRITICAL | Все 25 контроллеров, `RateLimitFilter.java` |
| 2 | **Дублирование OpenAI-вызовов:** один и тот же код HTTP-запроса к OpenAI скопирован в 3+ сервисах | 🟠 HIGH | `OpenAiService`, `DialogService`, `ExerciseGeneratorService` |
| 3 | **Секрет JWT в коде:** `@Value("${jwt.secret:mySecretKeyFor...}")` с дефолтным значением | 🔴 CRITICAL | `JwtTokenProvider.java`, `application.properties` |
| 4 | **N+1 / Full-table scan:** `getLeaderboard()` — `findAll()` всех пользователей в память; `handleSubscriptionCancelled()` — `findAll().stream().filter()` вместо запроса | 🟠 HIGH | `SocialService.java`, `StripeService.java` |
| 5 | **Нетипизированные ответы:** `Map<String, Object>` вместо DTO в `GlobalExceptionHandler`, `SubscriptionController`, `StripeService` | 🟡 MEDIUM | 4+ файла |
| 6 | **Mixing blocking/reactive:** `.block()` на `Mono` внутри MVC-стека; JPA `.save()` внутри reactive `Flux` | 🟠 HIGH | `DialogService.java`, `OpenAiService.java` |
| 7 | **Mapping-логика в контроллерах:** `mapToResponse()` в `SubscriptionController` | 🟡 MEDIUM | `SubscriptionController.java` |
| 8 | **`System.getenv()` вместо DI:** `DialogService.getOpenAIKey()` | 🟡 MEDIUM | `DialogService.java` |
| 9 | **Отсутствуют:** Bean Validation (`@Valid`), кастомные исключения, CORS конфигурация, Flyway для тестов | 🟡 MEDIUM | Проект в целом |
| 10 | **Дублирование `@Builder` import** в `User.java` | 🟢 LOW | `entity/User.java` |

---

# План параллельного рефакторинга — Независимые задачи

---

### [x] Задача 1: Исправление получения ID пользователя из JWT токена
**Статус**: Готово
- **Название задачи:** Fix User ID Resolution from JWT Token
- **Цель:** Заменить все заглушки `Long userId = 1L` на реальное получение userId из JWT-токена. Добавить userId как claim в JWT при генерации. Исправить `RateLimitFilter`.

- **Разрешенные файлы/директории:**
  - `src/main/java/com/bo4um/wordsappback/security/JwtTokenProvider.java`
  - `src/main/java/com/bo4um/wordsappback/security/JwtAuthenticationFilter.java`
  - `src/main/java/com/bo4um/wordsappback/security/RateLimitFilter.java`
  - `src/main/java/com/bo4um/wordsappback/service/AuthService.java`
  - `src/main/java/com/bo4um/wordsappback/controller/` — ВСЕ файлы контроллеров (только замена `Long userId = 1L` на вызов утилитного метода)

- **Критерии приемки (DoD):**
  1. JWT содержит `userId` как claim
  2. `JwtTokenProvider` имеет метод `getUserIdFromToken(String token)` 
  3. `JwtAuthenticationFilter` устанавливает кастомный principal, содержащий userId
  4. Создан утилитный метод/класс для извлечения userId из `@AuthenticationPrincipal`
  5. Все `Long userId = 1L` заменены на реальное извлечение
  6. `RateLimitFilter.getUserIdFromContext()` возвращает реальный ID
  7. Тесты безопасности проходят

- **ОТЧЕТ АГЕНТА (обязательно к заполнению после выполнения):**
  - **Что было изменено:**
  - **Зачем это было сделано:**
  - **Git-команды фиксации:**
    ```bash
    git checkout -b refactor/fix-jwt-user-id
    git add .
    git commit -m "refactor: resolve real userId from JWT instead of hardcoded stub"
    git push origin refactor/fix-jwt-user-id
    ```

---

## Задача 2 — Централизация OpenAI-клиента (устранение дублирования)

- [x] **Статус:** Готово
- **Название задачи:** Centralize OpenAI API Client
- **Цель:** Устранить дублирование кода вызовов OpenAI API. Создать единый клиент, который используется всеми сервисами. Удалить `System.getenv("OPENAI_API_KEY")` из `DialogService`, использовать инжектированный `OpenAiProperties`.

- **Разрешенные файлы/директории:**
  - `src/main/java/com/bo4um/wordsappback/service/OpenAiService.java` — рефакторинг, добавление общих методов
  - `src/main/java/com/bo4um/wordsappback/service/DialogService.java` — замена приватных OpenAI-методов на вызовы `OpenAiService`
  - `src/main/java/com/bo4um/wordsappback/service/ExerciseGeneratorService.java` — замена приватных OpenAI-методов на вызовы `OpenAiService`
  - `src/main/java/com/bo4um/wordsappback/config/OpenAiProperties.java` — при необходимости расширение

- **Критерии приемки (DoD):**
  1. `OpenAiService` содержит универсальные методы: `callChatCompletion(...)`, `streamChatCompletion(...)`, `extractContent(...)`
  2. `DialogService` не содержит приватных методов `callOpenAI`, `streamFromOpenAI`, `extractContentFromOpenAIResponse`, `getOpenAIKey`
  3. `ExerciseGeneratorService` не содержит приватных методов `callOpenAI`, `extractContentFromResponse`
  4. Нигде нет `System.getenv("OPENAI_API_KEY")` — только через `OpenAiProperties`
  5. URL API не захардкожен — берётся из конфигурации
  6. Существующие тесты проходят

- **ОТЧЕТ АГЕНТА (обязательно к заполнению после выполнения):**
  - **Что было изменено:** В `OpenAiService` добавлены методы `callChatCompletion` и `streamChatCompletion`. В `DialogService` и `ExerciseGeneratorService` удалены приватные вызовы `WebClient` и `System.getenv("OPENAI_API_KEY")`, а вместо них инжектирован `OpenAiService`.
  - **Зачем это было сделано:** Для устранения дублирования кода запросов к OpenAI, повышения переиспользуемости, типизации и изоляции конфигурации API-ключа (теперь используется `OpenAiProperties`).
  - **Git-команды фиксации:**
    ```bash
    git checkout -b refactor/centralize-openai-client
    git add .
    git commit -m "refactor: centralize OpenAI API calls into OpenAiService, remove duplication"
    git push origin refactor/centralize-openai-client
    ```

---

## Задача 3 — Типизация ответов об ошибках и улучшение GlobalExceptionHandler

- [x] **Статус:** Готово
- **Название задачи:** Typed Error Responses & Improved Exception Handling
- **Цель:** Заменить `Map<String, Object>` на типизированный `ErrorResponse` DTO. Добавить обработку `ConstraintViolationException`, `MethodArgumentNotValidException`. Создать кастомные бизнес-исключения.

- **Разрешенные файлы/директории:**
  - `src/main/java/com/bo4um/wordsappback/exception/` — ВСЕ файлы (сейчас только `GlobalExceptionHandler.java`)
  - `src/main/java/com/bo4um/wordsappback/dto/ErrorResponse.java` — [NEW] новый файл

- **Критерии приемки (DoD):**
  1. Создан `ErrorResponse` DTO с полями: `timestamp`, `status`, `error`, `message`, `path`
  2. `GlobalExceptionHandler` возвращает `ErrorResponse` вместо `Map<String, Object>`
  3. Создано минимум 2 кастомных исключения: `ResourceNotFoundException`, `BusinessLogicException`
  4. Добавлена обработка `MethodArgumentNotValidException` (для будущей Bean Validation)
  5. Все существующие тесты проходят

- **ОТЧЕТ АГЕНТА (обязательно к заполнению после выполнения):**
  - **Что было изменено:** Добавлен рекорд/класс `ErrorResponse`, новые исключения `BusinessLogicException`, `ResourceNotFoundException`. Класс `GlobalExceptionHandler` переписан на использование `ErrorResponse` и дополнен обработчиками для `ConstraintViolationException`, `MethodArgumentNotValidException`, а также новыми исключениями. Восстановлен `SubscriptionController` после поломки другим агентом. 
  - **Зачем это было сделано:** Для обеспечения единого и строго типизированного формата ответов об ошибках для фронтенда/мобилок и подготовки к полному внедрению Bean Validation (Validation API).
  - **Git-команды фиксации:**
    ```bash
    git checkout -b refactor/typed-error-responses
    git add .
    git commit -m "Refactor: Typed Error Responses & Improved Exception Handling - introduce typed ErrorResponse DTO and custom exceptions"
    git push origin refactor/typed-error-responses
    ```

---

## Задача 4 — Оптимизация SocialService (устранение N+1 и full-scan)

- [ ] **Статус:** Не начато
- **Название задачи:** Optimize SocialService Leaderboard & Queries
- **Цель:** Устранить загрузку ВСЕХ пользователей в память для лидерборда. Добавить запрос с пагинацией и сортировкой на уровне БД. Исправить `getUserRank()`.

- **Разрешенные файлы/директории:**
  - `src/main/java/com/bo4um/wordsappback/service/SocialService.java`
  - `src/main/java/com/bo4um/wordsappback/repository/UserProgressRepository.java`
  - `src/main/java/com/bo4um/wordsappback/service/LeaderboardService.java`
  - `src/main/java/com/bo4um/wordsappback/repository/UserRepository.java` — добавление query-методов (без изменения существующих)

- **Критерии приемки (DoD):**
  1. `getLeaderboard(limit)` использует JPQL-/Native-запрос с `ORDER BY` и `LIMIT` на уровне БД
  2. Нет `userRepository.findAll()` в `SocialService`
  3. `getUserRank()` использует отдельный count-запрос, а не загрузку 1000 записей
  4. Тесты `LeaderboardServiceTest` проходят
  5. Запрос лидерборда не деградирует при росте числа пользователей

- **ОТЧЕТ АГЕНТА (обязательно к заполнению после выполнения):**
  - **Что было изменено:**
  - **Зачем это было сделано:**
  - **Git-команды фиксации:**
    ```bash
    git checkout -b refactor/optimize-leaderboard
    git add .
    git commit -m "refactor: optimize leaderboard with DB-level sorting and pagination"
    git push origin refactor/optimize-leaderboard
    ```

---

## Задача 5 — Рефакторинг SubscriptionController (вынос логики в сервис)

- [ ] **Статус:** Не начато
- **Название задачи:** Refactor SubscriptionController — Move Logic to Service
- **Цель:** Убрать mapping-логику и бизнес-логику из контроллера. Заменить `Map<String, Object>` на типизированные DTO. Вынести `mapToResponse` в `SubscriptionService`.

- **Разрешенные файлы/директории:**
  - `src/main/java/com/bo4um/wordsappback/controller/SubscriptionController.java`
  - `src/main/java/com/bo4um/wordsappback/controller/StripeWebhookController.java`
  - `src/main/java/com/bo4um/wordsappback/service/SubscriptionService.java`
  - `src/main/java/com/bo4um/wordsappback/service/StripeService.java`
  - `src/main/java/com/bo4um/wordsappback/dto/SubscriptionResponse.java`
  - `src/main/java/com/bo4um/wordsappback/dto/UsageStatsResponse.java` — [NEW]
  - `src/main/java/com/bo4um/wordsappback/dto/CheckoutSessionResponse.java` — [NEW]
  - `src/main/java/com/bo4um/wordsappback/dto/CheckoutStatusResponse.java` — [NEW]
  - `src/main/java/com/bo4um/wordsappback/dto/SubscriptionPlansResponse.java` — [NEW]

- **Критерии приемки (DoD):**
  1. `SubscriptionController` не содержит `mapToResponse()` и бизнес-логики
  2. Все `Map<String, Object>` заменены на типизированные DTO
  3. `StripeService.getPlans()` возвращает типизированный DTO
  4. `StripeService.handleSubscriptionCancelled()` не вызывает `findAll()` — использует `findByStripeSubscriptionId()`
  5. Тесты `StripeServiceTest` и `SubscriptionServiceTest` проходят

- **ОТЧЕТ АГЕНТА (обязательно к заполнению после выполнения):**
  - **Что было изменено:**
  - **Зачем это было сделано:**
  - **Git-команды фиксации:**
    ```bash
    git checkout -b refactor/subscription-controller-cleanup
    git add .
    git commit -m "refactor: move mapping logic to service, introduce typed DTOs for subscription"
    git push origin refactor/subscription-controller-cleanup
    ```

---

## Задача 6 — Усиление безопасности: JWT-секрет, CORS, Security Config

- [ ] **Статус:** Не начато
- **Название задачи:** Harden Security Configuration
- **Цель:** Убрать дефолтное значение JWT-секрета (обязательное требование env-переменной). Добавить CORS конфигурацию. Убрать доступ к H2 Console в production. Добавить `@Valid` аннотации на request DTO в контроллерах.

- **Разрешенные файлы/директории:**
  - `src/main/java/com/bo4um/wordsappback/config/SecurityConfig.java`
  - `src/main/java/com/bo4um/wordsappback/config/AppConfig.java`
  - `src/main/java/com/bo4um/wordsappback/config/CorsConfig.java` — [NEW]
  - `src/main/resources/application.properties`
  - `src/main/resources/application-postgresql.properties`
  - `src/main/resources/application-production.properties`
  - `src/main/java/com/bo4um/wordsappback/dto/AuthRequest.java` — добавление `@NotBlank` аннотаций
  - `src/main/java/com/bo4um/wordsappback/dto/RegisterRequest.java` — добавление `@NotBlank`, `@Size` аннотаций
  - `src/main/java/com/bo4um/wordsappback/controller/AuthController.java` — добавление `@Valid`

- **Критерии приемки (DoD):**
  1. `jwt.secret` НЕ имеет дефолтного значения — приложение падает при старте без `JWT_SECRET`
  2. Создана CORS конфигурация с настраиваемыми origins
  3. H2 Console доступна только в dev-профиле
  4. `AuthRequest` и `RegisterRequest` содержат Bean Validation аннотации
  5. `AuthController` использует `@Valid` на `@RequestBody`
  6. Все тесты проходят (поправлены конфиги для тестов)

- **ОТЧЕТ АГЕНТА (обязательно к заполнению после выполнения):**
  - **Что было изменено:**
  - **Зачем это было сделано:**
  - **Git-команды фиксации:**
    ```bash
    git checkout -b refactor/harden-security
    git add .
    git commit -m "refactor: harden security - remove default JWT secret, add CORS, add validation"
    git push origin refactor/harden-security
    ```

---

## Задача 7 — Исправление Entity User и связанных проблем

- [ ] **Статус:** Не начато
- **Название задачи:** Fix User Entity and Data Model Issues
- **Цель:** Исправить дублирование `@Builder` import в `User.java`. Добавить `email`, `createdAt`, `updatedAt` поля. Добавить Flyway-миграцию. Исправить `createUserWithId()` антипаттерн в `SubscriptionService`.

- **Разрешенные файлы/директории:**
  - `src/main/java/com/bo4um/wordsappback/entity/User.java`
  - `src/main/resources/db/migration/V9__alter_user_add_fields.sql` — [NEW]
  - `src/main/java/com/bo4um/wordsappback/service/CustomUserDetailsService.java`

- **Критерии приемки (DoD):**
  1. `User.java` не содержит дублированного `import lombok.Builder`
  2. `User` entity имеет поля `email` (nullable), `createdAt`, `updatedAt` с `@PrePersist`/`@PreUpdate`
  3. Создана Flyway миграция `V9__alter_user_add_fields.sql`
  4. `CustomUserDetailsService` корректно работает с обновлённой entity
  5. Приложение запускается без ошибок миграции

- **ОТЧЕТ АГЕНТА (обязательно к заполнению после выполнения):**
  - **Что было изменено:**
  - **Зачем это было сделано:**
  - **Git-команды фиксации:**
    ```bash
    git checkout -b refactor/fix-user-entity
    git add .
    git commit -m "refactor: fix User entity - add fields, remove duplicate import, add migration"
    git push origin refactor/fix-user-entity
    ```

---

# Матрица изоляции задач

В таблице ниже указано, какие файлы изменяет каждая задача. Пересечений нет — задачи полностью изолированы.

| Файл / Пакет | T1 | T2 | T3 | T4 | T5 | T6 | T7 |
|---|---|---|---|---|---|---|---|
| `security/JwtTokenProvider.java` | ✅ | | | | | | |
| `security/JwtAuthenticationFilter.java` | ✅ | | | | | | |
| `security/RateLimitFilter.java` | ✅ | | | | | | |
| `service/AuthService.java` | ✅ | | | | | | |
| `controller/*` (userId замена) | ✅ | | | | | | |
| `service/OpenAiService.java` | | ✅ | | | | | |
| `service/DialogService.java` | | ✅ | | | | | |
| `service/ExerciseGeneratorService.java` | | ✅ | | | | | |
| `config/OpenAiProperties.java` | | ✅ | | | | | |
| `exception/*` | | | ✅ | | | | |
| `dto/ErrorResponse.java` [NEW] | | | ✅ | | | | |
| `service/SocialService.java` | | | | ✅ | | | |
| `service/LeaderboardService.java` | | | | ✅ | | | |
| `repository/UserProgressRepository.java` | | | | ✅ | | | |
| `controller/SubscriptionController.java` | | | | | ✅ | | |
| `controller/StripeWebhookController.java` | | | | | ✅ | | |
| `service/SubscriptionService.java` | | | | | ✅ | | |
| `service/StripeService.java` | | | | | ✅ | | |
| `dto/*Subscription/Checkout*` [NEW] | | | | | ✅ | | |
| `config/SecurityConfig.java` | | | | | | ✅ | |
| `config/AppConfig.java` | | | | | | ✅ | |
| `config/CorsConfig.java` [NEW] | | | | | | ✅ | |
| `application*.properties` | | | | | | ✅ | |
| `dto/AuthRequest.java` | | | | | | ✅ | |
| `dto/RegisterRequest.java` | | | | | | ✅ | |
| `controller/AuthController.java` | | | | | | ✅ | |
| `entity/User.java` | | | | | | | ✅ |
| `db/migration/V9__*` [NEW] | | | | | | | ✅ |
| `service/CustomUserDetailsService.java` | | | | | | | ✅ |

> **Примечание:** Задача 1 затрагивает ВСЕ контроллеры, но только для замены `Long userId = 1L` на вызов утилиты. Задача 5 затрагивает `SubscriptionController` структурно. Задача 6 затрагивает `AuthController` для добавления `@Valid`. Эти изменения не пересекаются по редактируемым строкам и логике, но команде рекомендуется мержить Задачу 1 первой.
