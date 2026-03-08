# WordsApp Back - API Documentation

Документация по REST API для приложения WordsApp.

## 📋 Оглавление

- [Аутентификация](#-аутентификация)
- [Персонажи](#-персонажи)
- [Прогресс пользователя](#-прогресс-пользователя)
- [Тесты уровня языка](#-тесты-уровня-языка)
- [Слова](#-слова)

---

## 🔐 Аутентификация

Все запросы к API (кроме регистрации и логина) требуют JWT токен в заголовке.

### Регистрация пользователя

**POST** `/api/auth/register`

Регистрирует нового пользователя.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "newuser",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "newuser",
  "role": "USER"
}
```

---

### Логин

**POST** `/api/auth/login`

Аутентификация пользователя и получение JWT токена.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc3Mjk5MzI2NCwiZXhwIjoxNzczMDc5NjY0fQ...",
  "username": "admin",
  "role": "ADMIN"
}
```

---

## 🎭 Персонажи

### Получить всех персонажей

**GET** `/api/characters`

Возвращает список всех персонажей (включая неактивных).

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Dimas",
    "sex": "male",
    "description": "Friendly and enthusiastic language learner",
    "isSystem": true,
    "isActive": true,
    "sortOrder": 1,
    "hasImage": false
  },
  {
    "id": 5,
    "name": "Anna",
    "sex": "female",
    "description": "Cheerful and patient study companion",
    "isSystem": true,
    "isActive": true,
    "sortOrder": 5,
    "hasImage": false
  }
]
```

---

### Получить только активных персонажей

**GET** `/api/characters/active`

Возвращает только активных персонажей.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Dimas",
    "sex": "male",
    "description": "Friendly and enthusiastic language learner",
    "isSystem": true,
    "isActive": true,
    "sortOrder": 1,
    "hasImage": false
  }
]
```

---

### Получить персонажа по ID

**GET** `/api/characters/{id}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Dimas",
  "sex": "male",
  "description": "Friendly and enthusiastic language learner",
  "isSystem": true,
  "isActive": true,
  "sortOrder": 1,
  "hasImage": false
}
```

---

### Получить изображение персонажа

**GET** `/api/characters/{id}/image`

Возвращает бинарные данные изображения.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```
Content-Type: image/jpeg
<binary image data>
```

**Response (404 Not Found):**
```json
{
  "error": "Not Found",
  "message": "Image not found",
  "status": 404
}
```

---

### Создать персонажа (ADMIN)

**POST** `/api/characters`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "NewCharacter",
  "sex": "male",
  "description": "Character description",
  "isSystem": false,
  "isActive": true,
  "sortOrder": 10
}
```

**Response (200 OK):**
```json
{
  "id": 9,
  "name": "NewCharacter",
  "sex": "male",
  "description": "Character description",
  "isSystem": false,
  "isActive": true,
  "sortOrder": 10,
  "hasImage": false
}
```

---

### Обновить персонажа (ADMIN)

**PUT** `/api/characters/{id}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "UpdatedCharacter",
  "sex": "male",
  "description": "Updated description",
  "isSystem": false,
  "isActive": true,
  "sortOrder": 10
}
```

**Response (200 OK):**
```json
{
  "id": 9,
  "name": "UpdatedCharacter",
  "sex": "male",
  "description": "Updated description",
  "isSystem": false,
  "isActive": true,
  "sortOrder": 10,
  "hasImage": false
}
```

---

### Загрузить изображение персонажа (ADMIN)

**POST** `/api/characters/{id}/image`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

**Request Body (form-data):**
```
Key: image
Type: File
Value: <select image file>
```

**Response (200 OK):**
```json
{
  "id": 9,
  "name": "Character",
  "sex": "male",
  "description": "Description",
  "isSystem": false,
  "isActive": true,
  "sortOrder": 10,
  "hasImage": true
}
```

---

### Удалить изображение персонажа (ADMIN)

**DELETE** `/api/characters/{id}/image`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content):**
```
No content
```

---

### Удалить персонажа (ADMIN)

**DELETE** `/api/characters/{id}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content):**
```
No content
```

**Response (400 Bad Request) - попытка удалить системного персонажа:**
```json
{
  "message": "Cannot delete system character: Dimas"
}
```

---

## 📊 Прогресс пользователя

### Получить общий прогресс

**GET** `/api/progress`

Возвращает общую статистику пользователя (streak, всего слов и т.д.).

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "currentStreak": 5,
  "longestStreak": 10,
  "lastVisitDate": "2026-03-08",
  "totalWordsLearned": 150,
  "joinDate": "2026-03-01"
}
```

---

### Получить статистику

**GET** `/api/progress/stats`

Альтернативный эндпоинт для получения статистики.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "currentStreak": 5,
  "longestStreak": 10,
  "lastVisitDate": "2026-03-08",
  "totalWordsLearned": 150,
  "joinDate": "2026-03-01"
}
```

---

### Получить прогресс по словарям

**GET** `/api/progress/dictionaries`

Возвращает прогресс по всем языковым словарям пользователя.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "dictionaryName": "English",
    "wordsLearned": 100,
    "totalWords": null,
    "progressPercentage": 0,
    "lastUpdated": "2026-03-08T21:00:00"
  },
  {
    "id": 2,
    "dictionaryName": "Spanish",
    "wordsLearned": 50,
    "totalWords": null,
    "progressPercentage": 0,
    "lastUpdated": "2026-03-08T20:00:00"
  }
]
```

---

### Получить прогресс по конкретному языку

**GET** `/api/progress/dictionaries/{language}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "dictionaryName": "English",
  "wordsLearned": 100,
  "totalWords": null,
  "progressPercentage": 0,
  "lastUpdated": "2026-03-08T21:00:00"
}
```

---

### Получить все изученные слова

**GET** `/api/progress/words`

Возвращает все слова, которые пользователь отметил как изученные.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "word": "Hello",
    "language": "English",
    "learnedAt": "2026-03-08T21:00:00",
    "repetitions": 3,
    "nextReview": "2026-03-11"
  },
  {
    "id": 2,
    "word": "Goodbye",
    "language": "English",
    "learnedAt": "2026-03-08T21:05:00",
    "repetitions": 2,
    "nextReview": "2026-03-10"
  }
]
```

---

### Получить слова по языку

**GET** `/api/progress/words/by-language?language={language}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request Parameters:**
```
language: English
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "word": "Hello",
    "language": "English",
    "learnedAt": "2026-03-08T21:00:00",
    "repetitions": 3,
    "nextReview": "2026-03-11"
  }
]
```

---

### Получить слова для повторения

**GET** `/api/progress/words/review`

Возвращает слова, у которых наступила дата повторения.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "word": "Hello",
    "language": "English",
    "learnedAt": "2026-03-01T21:00:00",
    "repetitions": 3,
    "nextReview": "2026-03-08"
  }
]
```

---

### Отметить слово как изученное

**POST** `/api/progress/words`

Добавляет слово в список изученных. Если слово уже существует, увеличивается счётчик повторений.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "word": "Thank you",
  "language": "English"
}
```

**Response (200 OK):**
```json
{
  "id": 3,
  "word": "Thank you",
  "language": "English",
  "learnedAt": "2026-03-08T22:00:00",
  "repetitions": 1,
  "nextReview": "2026-03-09"
}
```

---

## 📝 Тесты уровня языка

### Получить список тестов

**GET** `/api/tests`

Возвращает список всех доступных тестов.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "English Placement Test",
    "description": "Test to determine your English level (A1-B2)",
    "language": "English",
    "totalQuestions": 10,
    "passingScore": 50,
    "isActive": true
  }
]
```

---

### Получить тест с вопросами

**GET** `/api/tests/{id}`

Возвращает полную информацию о тесте со всеми вопросами.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "English Placement Test",
  "description": "Test to determine your English level (A1-B2)",
  "language": "English",
  "totalQuestions": 10,
  "passingScore": 50,
  "questions": [
    {
      "id": 1,
      "questionText": "Choose the correct form: 'She ___ to school every day.'",
      "optionA": "go",
      "optionB": "goes",
      "optionC": "going",
      "optionD": "gone",
      "level": "A1",
      "points": 1,
      "orderNumber": 1
    },
    {
      "id": 2,
      "questionText": "What is the past tense of 'eat'?",
      "optionA": "eated",
      "optionB": "ate",
      "optionC": "eaten",
      "optionD": "eating",
      "level": "A1",
      "points": 1,
      "orderNumber": 2
    }
  ]
}
```

---

### Отправить ответы на тест

**POST** `/api/tests/{id}/submit`

Принимает ответы пользователя, подсчитывает результат и определяет уровень языка.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "answers": {
    "1": "B",
    "2": "B",
    "3": "B",
    "4": "C",
    "5": "B",
    "6": "C",
    "7": "B",
    "8": "B",
    "9": "B",
    "10": "B"
  }
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "testId": 1,
  "testName": "English Placement Test",
  "score": 10,
  "maxScore": 10,
  "percentage": 100,
  "determinedLevel": "C1",
  "completedAt": "2026-03-08T22:00:00"
}
```

**Уровни определяются по проценту правильных ответов:**
- ≥90% → C1
- ≥75% → B2
- ≥50% → B1
- ≥35% → A2
- <35% → A1

---

### Получить историю тестов пользователя

**GET** `/api/tests/history`

Возвращает все результаты тестов пользователя.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "testId": 1,
    "testName": "English Placement Test",
    "score": 10,
    "maxScore": 10,
    "percentage": 100,
    "determinedLevel": "C1",
    "completedAt": "2026-03-08T22:00:00"
  },
  {
    "id": 2,
    "testId": 1,
    "testName": "English Placement Test",
    "score": 7,
    "maxScore": 10,
    "percentage": 70,
    "determinedLevel": "B1",
    "completedAt": "2026-03-07T18:00:00"
  }
]
```

---

### Получить лучший результат по тесту

**GET** `/api/tests/{id}/best-result`

Возвращает лучший результат пользователя по указанному тесту.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "testId": 1,
  "testName": "English Placement Test",
  "score": 10,
  "maxScore": 10,
  "percentage": 100,
  "determinedLevel": "C1",
  "completedAt": "2026-03-08T22:00:00"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Not Found",
  "message": "No results found",
  "status": 404
}
```

---

## 🔤 Слова

### Получить значение слова (через OpenAI)

**POST** `/api/word`

Получает детальное объяснение слова с примерами через OpenAI API.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "input": "Suck",
  "def_language": "Russian",
  "character_id": 1,
  "style": "Normal"
}
```

**Альтернативный запрос (без character_id):**
```json
{
  "input": "Suck",
  "def_language": "Russian",
  "character_sex": "male",
  "character_name": "Dimas",
  "style": "Normal"
}
```

**Response (200 OK):**
```json
{
  "input": "Suck",
  "output_language": "Russian",
  "type": "word",
  "meanings": [
    {
      "definition": "To draw something into the mouth by creating a vacuum with the lips and mouth.",
      "level": "A2",
      "example_input": "Dimas likes to suck on a lollipop.",
      "example_output": "Димас любит сосать леденец."
    },
    {
      "definition": "To be very bad or unpleasant (informal).",
      "level": "B1",
      "example_input": "Dimas said the movie sucks because it was boring.",
      "example_output": "Димас сказал, что фильм отстой, потому что он был скучным."
    }
  ]
}
```

---

## 🔧 Коды ответов

| Код | Описание |
|-----|----------|
| 200 | Успешный запрос |
| 204 | Успешное удаление (без тела ответа) |
| 400 | Ошибка валидации / Bad Request |
| 401 | Неавторизованный запрос |
| 403 | Недостаточно прав (ADMIN required) |
| 404 | Ресурс не найден |
| 500 | Внутренняя ошибка сервера |

---

## 📌 Примечания

1. **JWT Токен** действителен 24 часа (86400000 мс)
2. **Формат токена:** `Authorization: Bearer <token>`
3. **Системные персонажи** не могут быть удалены
4. **Streak дней** обновляется автоматически при логине
5. **Интервальное повторение:** nextReview = learnedAt + repetitions дней

---

## 🚀 Быстрый старт для разработчиков

### 1. Логин и получение токена
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

### 2. Получить всех персонажей
```bash
curl -X GET http://localhost:8080/api/characters \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Отметить слово как изученное
```bash
curl -X POST http://localhost:8080/api/progress/words \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"word":"Hello","language":"English"}'
```

### 4. Пройти тест
```bash
curl -X POST http://localhost:8080/api/tests/1/submit \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"answers":{"1":"B","2":"B","3":"B","4":"C","5":"B","6":"C","7":"B","8":"B","9":"B","10":"B"}}'
```

---

## 📞 Контакты

По вопросам интеграции обращайтесь к backend-разработчику.
