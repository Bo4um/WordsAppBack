# WordsApp Back — AI Language Learning Platform

[![Java CI](https://github.com/bo4um/WordsAppBack/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/bo4um/WordsAppBack/actions/workflows/maven-ci.yml)
[![Java](https://img.shields.io/badge/java-17-blue)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-4.0.3-brightgreen)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-proprietary-red)](LICENSE)

Backend для платформы изучения иностранных слов с AI-компаньоном.

---

## 📋 Оглавление

- [О проекте](#-о-проекте)
- [Возможности](#-возможности)
- [Технологии](#-технологии)
- [Быстрый старт](#-быстрый-старт)
- [API Документация](#-api-документация)
- [Тестирование](#-тестирование)
- [Production](#-production)
- [Структура проекта](#-структура-проекта)

---

## 🎯 О проекте

WordsApp — это AI-powered платформа для изучения иностранных слов через:
- **Персонализированные объяснения** слов через OpenAI
- **Ролевые диалоги** с AI-компаньонами
- **Интервальное повторение** для запоминания
- **Генерацию упражнений** на основе прогресса
- **Анализ произношения** через Whisper API
- **Систему уровней** через тестирование

---

## ✨ Возможности

### 🔥 AI Функции
- **AI Объяснения слов** — детальные объяснения с примерами (OpenAI GPT-4)
- **AI Диалоги** — ролевые сценарии с персонажами (10+ сценариев)
- **AI Упражнения** — генерация упражнений на основе уровня
- **AI Произношение** — анализ через Whisper API

### 📚 Обучение
- **7 типов упражнений** — Fill-in-blank, Translation, Vocabulary и др.
- **Тесты уровня** — определение CEFR уровня (A1-C1)
- **Интервальное повторение** — автоматический расчёт next review
- **Прогресс по языкам** — трекинг по словарям

### 🎮 Геймификация
- **Streak система** — текущая и самая длинная серия
- **Персонажи-компаньоны** — мужские/женские персонажи
- **Достижения** — система наград за прогресс

### 👥 Социальные функции
- **Друзья** — добавление и управление
- **Leaderboard** — таблица лидеров
- **Челленджи** — еженедельные соревнования

### 💰 Монетизация
- **3 тарифа** — FREE / PREMIUM ($9.99/мес) / LIFETIME ($399)
- **Rate Limiting** — лимиты для FREE tier
- **Stripe integration** — готово к подключению

---

## 🛠 Технологии

### Backend
| Технология | Версия | Описание |
|------------|--------|----------|
| Java | 17 | Основной язык |
| Spring Boot | 4.0.3 | Фреймворк |
| Spring Security | - | JWT аутентификация |
| Spring Data JPA | - | ORM |
| Spring WebFlux | - | WebClient для OpenAI |

### Базы данных
| БД | Назначение |
|----|------------|
| PostgreSQL 16 | Production БД |
| H2 | Development БД |
| Redis 7 | Кэширование |

### Внешние интеграции
| Сервис | Назначение |
|--------|------------|
| OpenAI API | AI объяснения, диалоги, упражнения |
| Whisper API | Анализ произношения |
| Stripe | Платежи (готово) |

### Инструменты
- **Maven** — сборка
- **Docker & Docker Compose** — контейнеризация
- **Flyway** — миграции БД
- **Lombok** — редукция кода
- **Swagger/OpenAPI** — документация

---

## 🚀 Быстрый старт

### Требования
- Java 17+
- Maven 3.6+
- Docker (опционально)

### 1. Локальная разработка (H2)

```bash
# Клонировать репозиторий
git clone https://github.com/bo4um/WordsAppBack.git
cd WordsAppBack

# Запустить приложение
./mvnw spring-boot:run

# Приложение доступно на http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console
```

**Параметры H2:**
- JDBC URL: `jdbc:h2:file:./data/wordsapp`
- Username: `sa`
- Password: (пустой)

### 2. PostgreSQL + Redis (Docker)

```bash
# Запустить БД и Redis
docker-compose up -d

# Запустить приложение
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgresql
```

### 3. Полное развёртывание (Docker)

```bash
# Запустить всё (приложение + БД + Redis)
docker-compose -f docker-compose.full.yml up -d

# Просмотр логов
docker-compose -f docker-compose.full.yml logs -f app
```

---

## 📚 API Документация

### Аутентификация

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/auth/register` | Регистрация |
| POST | `/api/auth/login` | Логин (JWT токен) |

### Персонажи

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/characters` | Все персонажи |
| GET | `/api/characters/active` | Активные персонажи |
| POST | `/api/characters` | Создать (ADMIN) |
| PUT | `/api/characters/{id}` | Обновить (ADMIN) |

### Прогресс

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/progress` | Общий прогресс |
| GET | `/api/progress/dictionaries` | По словарям |
| GET | `/api/progress/words` | Изученные слова |
| POST | `/api/progress/words` | Отметить слово |

### AI Диалоги

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/dialog/scenarios` | Сценарии |
| POST | `/api/dialog/start` | Начать диалог |
| POST | `/api/dialog/message` | Отправить сообщение |
| POST | `/api/dialog/message/stream` | Streaming (SSE) |

### Подписка

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/subscription` | Моя подписка |
| GET | `/api/subscription/usage` | Статистика |
| POST | `/api/subscription/upgrade` | Улучшить |

### Упражнения

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/exercise/generate` | Сгенерировать |
| GET | `/api/exercise` | Мои упражнения |
| POST | `/api/exercise/submit` | Отправить ответ |

### Произношение

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/pronunciation` | Проверить |
| GET | `/api/pronunciation/stats` | Статистика |

### Социальные функции

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/social/friends` | Друзья |
| GET | `/api/social/leaderboard` | Лидерборд |
| GET | `/api/social/challenges` | Челленджи |

**Полная документация:** [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

## 🧪 Тестирование

### Запуск тестов

```bash
# Все Unit тесты
./mvnw test -Dtest="*ServiceTest,*ProviderTest,*LogicTest"

# Integration тесты (требуют Docker)
docker-compose up -d
./mvnw test -Dtest="*IntegrationTest"

# Все тесты
./mvnw test
```

### Покрытие тестами

| Тип | Количество | Статус |
|-----|------------|--------|
| Unit тесты | 59 | ✅ 100% pass |
| Integration тесты | 8 | ⚠️ Требуют Docker |
| API тесты | 20 | ⚠️ Требуют Docker |

---

## 📦 Production

### Переменные окружения

| Переменная | Описание | Пример |
|------------|----------|--------|
| `JWT_SECRET` | Секрет для JWT | `your-secret-key` |
| `OPENAI_API_KEY` | API ключ OpenAI | `sk-...` |
| `DATABASE_URL` | PostgreSQL URL | `jdbc:postgresql://...` |
| `DATABASE_USERNAME` | Пользователь БД | `wordsapp` |
| `DATABASE_PASSWORD` | Пароль БД | `...` |
| `REDIS_HOST` | Redis хост | `localhost` |
| `STRIPE_API_KEY` | Stripe ключ | `sk_...` |

### Сборка

```bash
# Собрать WAR
./mvnw clean package -DskipTests

# Запустить production
java -jar target/WordsAppBack-0.0.1-SNAPSHOT.war \
  --spring.profiles.active=production
```

### Monitoring

```bash
# Prometheus metrics
http://localhost:8080/actuator/prometheus

# Health check
http://localhost:8080/actuator/health
```

---

## 📁 Структура проекта

```
WordsAppBack/
├── src/
│   ├── main/
│   │   ├── java/com/bo4um/wordsappback/
│   │   │   ├── config/          # Конфигурация
│   │   │   ├── controller/      # REST API
│   │   │   ├── dto/             # DTO
│   │   │   ├── entity/          # JPA сущности
│   │   │   ├── repository/      # Репозитории
│   │   │   ├── service/         # Сервисы
│   │   │   └── security/        # JWT Security
│   │   └── resources/
│   │       ├── db/migration/    # Flyway миграции
│   │       └── application*.properties
│   └── test/
│       └── java/com/bo4um/wordsappback/
│           ├── service/         # Service тесты
│           └── security/        # Security тесты
├── docker-compose.yml           # Docker для разработки
├── docker-compose.full.yml      # Полное развёртывание
├── Dockerfile                   # Production образ
├── prometheus.yml               # Monitoring config
└── pom.xml                      # Maven зависимости
```

---

## 📊 Метрики проекта

| Метрика | Значение |
|---------|----------|
| Java классов | 120+ |
| API endpoints | 70+ |
| Таблиц БД | 20 |
| Unit тестов | 59 |
| Время запуска | ~4.6 сек |

---

## 🤝 Вклад

1. Fork репозиторий
2. Создай feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Открой Pull Request

---

## 📝 Лицензия

Proprietary — все права защищены.

---

## 📞 Контакты

- **Email:** support@bo4um.com
- **GitHub:** https://github.com/bo4um/WordsAppBack

---

*Последнее обновление: 2026-03-14*
