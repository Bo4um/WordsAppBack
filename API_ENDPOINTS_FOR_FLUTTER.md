# WordsApp API Reference для Flutter разработчика

**Base URL:** `https://api.wordsapp.com` (production) или `http://localhost:8080` (dev)  
**Auth:** JWT Bearer token в заголовке `Authorization: Bearer <token>`  
**Content-Type:** `application/json`

---

## 🔐 1. Аутентификация

### POST /api/auth/register
**Регистрация нового пользователя**

**Request:**
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

### POST /api/auth/login
**Логин**

**Request:**
```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

---

## 🏠 2. Прогресс пользователя

### GET /api/progress
**Общий прогресс пользователя**

**Headers:** `Authorization: Bearer <token>`

**Response (200 OK):**
```json
{
  "id": 1,
  "currentStreak": 5,
  "longestStreak": 10,
  "lastVisitDate": "2026-03-13",
  "totalWordsLearned": 150,
  "joinDate": "2026-03-01"
}
```

---

### GET /api/progress/dictionaries
**Прогресс по языкам**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "dictionaryName": "English",
    "wordsLearned": 100,
    "progressPercentage": 0,
    "lastUpdated": "2026-03-13T21:00:00"
  }
]
```

---

### GET /api/progress/words
**Все изученные слова**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "word": "Hello",
    "language": "English",
    "learnedAt": "2026-03-13T21:00:00",
    "repetitions": 3,
    "nextReview": "2026-03-16"
  }
]
```

---

### POST /api/progress/words
**Отметить слово как изученное**

**Request:**
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
  "learnedAt": "2026-03-13T22:00:00",
  "repetitions": 1,
  "nextReview": "2026-03-14"
}
```

---

## 💬 3. AI Диалоги

### GET /api/dialog/scenarios
**Список сценариев для диалогов**

**Query Params:** `?language=English` (optional)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Airport Check-in",
    "description": "Practice checking in at an airport",
    "language": "English",
    "difficulty": "A2"
  }
]
```

---

### POST /api/dialog/start
**Начать новый диалог**

**Request:**
```json
{
  "scenarioId": 1,
  "characterId": 1,
  "topic": "Checking in for flight"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "scenarioId": 1,
  "scenarioTitle": "Airport Check-in",
  "characterId": 1,
  "characterName": "Dimas",
  "topic": "Checking in for flight",
  "startedAt": "2026-03-13T21:00:00",
  "isActive": true
}
```

---

### POST /api/dialog/message
**Отправить сообщение в диалог**

**Request:**
```json
{
  "sessionId": 1,
  "message": "Hello, I have a reservation"
}
```

**Response (200 OK):**
```json
{
  "id": 2,
  "role": "assistant",
  "content": "Hello! Welcome to British Airways. May I see your passport, please?",
  "timestamp": "2026-03-13T21:01:05",
  "isComplete": true
}
```

---

### POST /api/dialog/message/stream
**Отправить сообщение (SSE Streaming)**

**Content-Type:** `text/event-stream`

**Response:** Поток событий с частями ответа AI

**Пример SSE события:**
```
data: {"content": "Hello! Welcome...", "isComplete": false}
data: {"content": "Hello! Welcome to British Airways.", "isComplete": false}
data: {"content": "Hello! Welcome to British Airways. May I see...", "isComplete": true}
```

---

### GET /api/dialog/sessions
**История сессий диалогов**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "scenarioId": 1,
    "scenarioTitle": "Airport Check-in",
    "startedAt": "2026-03-13T21:00:00",
    "isActive": true
  }
]
```

---

### GET /api/dialog/sessions/{id}/history
**История сообщений сессии**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "role": "user",
    "content": "Hello, I have a reservation",
    "timestamp": "2026-03-13T21:01:00"
  },
  {
    "id": 2,
    "role": "assistant",
    "content": "Hello! Welcome...",
    "timestamp": "2026-03-13T21:01:05"
  }
]
```

---

## 🎭 4. Мемы (Meme Learning)

### GET /api/memes/trending
**Трендовые мемы**

**Query Params:** `?language=English&limit=10`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "imageUrl": "https://i.imgflip.com/9ehk.jpg",
    "title": "Drake Hotline Bling",
    "description": "Drake rejecting formal English",
    "memeType": "drake",
    "language": "English",
    "difficulty": "B1",
    "culturalContext": "Popular meme format",
    "vocabularyWords": ["slang", "informal", "reject"],
    "likes": 150,
    "shares": 45,
    "isActive": true,
    "createdAt": "2026-03-13T21:00:00"
  }
]
```

---

### GET /api/memes/difficulty
**Мемы по уровню сложности**

**Query Params:** `?language=English&difficulty=B1`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Drake Hotline Bling",
    "difficulty": "B1",
    ...
  }
]
```

---

### POST /api/memes/exercise
**Сгенерировать упражнение из мема**

**Request:**
```json
{
  "memeId": 1,
  "exerciseType": "explain" // или "complete", "translate"
}
```

**Response (200 OK):**
```json
{
  "memeId": 1,
  "question": "Explain why this meme is funny in English",
  "correctAnswer": "This meme uses cultural references...",
  "explanation": "This meme uses cultural references...",
  "vocabularyWords": ["slang", "informal"],
  "culturalContext": "Popular meme format",
  "isCorrect": true,
  "pointsEarned": 10
}
```

---

### POST /api/memes/{id}/like
**Лайкнуть мем**

**Response (200 OK):** No content

---

### POST /api/memes/{id}/share
**Поделиться мемом**

**Response (200 OK):**
```json
{
  "success": true,
  "shareUrl": "https://wordsapp.com/meme/1"
}
```

---

## 📚 5. Упражнения

### POST /api/exercise/generate
**Сгенерировать упражнения**

**Request:**
```json
{
  "language": "English",
  "difficulty": "B1",
  "exerciseType": "FILL_IN_BLANK",
  "topic": "Travel",
  "count": 5
}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "type": "FILL_IN_BLANK",
    "question": "I ___ (to be) at the airport yesterday.",
    "hint": "Past Simple tense",
    "explanation": "'Was' is the past simple form",
    "language": "English",
    "difficulty": "B1",
    "isCompleted": false,
    "createdAt": "2026-03-13T21:00:00"
  }
]
```

---

### GET /api/exercise
**Мои упражнения**

**Query Params:** `?completed=false` (optional)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "type": "FILL_IN_BLANK",
    "question": "I ___ at the airport...",
    "isCompleted": false
  }
]
```

---

### POST /api/exercise/submit
**Отправить ответ на упражнение**

**Request:**
```json
{
  "exerciseId": 1,
  "answer": "was"
}
```

**Response (200 OK):**
```json
{
  "exerciseId": 1,
  "isCorrect": true,
  "userAnswer": "was",
  "correctAnswer": "was",
  "explanation": "'Was' is the past simple form",
  "points": 10
}
```

---

## 🎤 6. Произношение

### POST /api/pronunciation
**Проверить произношение**

**Content-Type:** `multipart/form-data`

**Form Data:**
- `audio`: файл (MP3, WAV, WebM)
- `targetPhrase`: "She goes to school"

**Response (200 OK):**
```json
{
  "id": 1,
  "targetPhrase": "She goes to school",
  "recognizedText": "She goes to school",
  "accuracyScore": 95,
  "feedback": "Excellent pronunciation! 🎉",
  "isGood": true,
  "status": "COMPLETED",
  "attemptedAt": "2026-03-13T21:00:00"
}
```

---

### GET /api/pronunciation/stats
**Статистика произношения**

**Response (200 OK):**
```json
{
  "totalAttempts": 50,
  "averageScore": 87.5,
  "bestScore": 100,
  "improvementRate": 15
}
```

---

## 🏆 7. Лидерборды

### GET /api/leaderboard
**Глобальный лидерборд**

**Query Params:** `?limit=50`

**Response (200 OK):**
```json
[
  {
    "rank": 1,
    "userId": 123,
    "username": "ProLearner",
    "score": 5000,
    "streak": 100,
    "wordsLearned": 2000
  }
]
```

---

### GET /api/leaderboard/{category}
**Лидерборд по категории**

**Categories:** `streak`, `words`, `exercises`, `pronunciation`

**Response (200 OK):**
```json
[
  {
    "rank": 1,
    "userId": 123,
    "username": "StreakMaster",
    "streak": 100,
    "score": 1000
  }
]
```

---

### GET /api/leaderboard/my-rank/{category}
**Мой ранг**

**Response (200 OK):**
```json
{
  "rank": 42,
  "score": 500,
  "total": 1000
}
```

---

## 👥 8. Сообщество (Community Feed)

### GET /api/community/trending
**Трендовые посты**

**Query Params:** `?limit=10`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 123,
    "username": "LanguageLover",
    "content": "Practicing my pronunciation!",
    "mediaUrl": "https://cdn.wordsapp.com/media/audio123.mp3",
    "mediaType": "audio",
    "durationSeconds": 15,
    "language": "English",
    "topic": "Pronunciation",
    "likes": 50,
    "comments": 10,
    "shares": 5,
    "isActive": true,
    "createdAt": "2026-03-13T21:00:00"
  }
]
```

---

### POST /api/community
**Создать пост**

**Request:**
```json
{
  "content": "Just completed my first dialog!",
  "mediaUrl": "https://cdn.wordsapp.com/media/audio456.mp3",
  "mediaType": "audio",
  "language": "English",
  "topic": "Dialog practice"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 123,
  "username": "CurrentUser",
  "content": "Just completed my first dialog!",
  ...
}
```

---

### POST /api/community/{id}/like
**Лайкнуть пост**

**Response (200 OK):** No content

---

### POST /api/community/{id}/comment
**Добавить комментарий**

**Response (200 OK):** No content

---

### POST /api/community/{id}/share
**Поделиться постом**

**Response (200 OK):** No content

---

### GET /api/community/my
**Мои посты**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "content": "Just completed my first dialog!",
    "likes": 5,
    ...
  }
]
```

---

## 🎯 9. Рекомендации

### GET /api/recommendations
**Все рекомендации**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "type": "REVIEW_WORDS",
    "title": "Пора повторить слова! 📚",
    "description": "У тебя 15 слов для повторения сегодня",
    "priority": 1,
    "isRead": false,
    "createdAt": "2026-03-13T21:00:00"
  }
]
```

---

### GET /api/recommendations/unread
**Непрочитанные рекомендации**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "type": "REVIEW_WORDS",
    "title": "Пора повторить слова! 📚",
    ...
  }
]
```

---

### POST /api/recommendations/{id}/read
**Отметить как прочитанное**

**Response (200 OK):** No content

---

## 💳 10. Подписка (Stripe)

### GET /api/subscription
**Моя подписка**

**Response (200 OK):**
```json
{
  "id": 1,
  "tier": "PREMIUM",
  "isActive": true,
  "startDate": "2026-03-01T00:00:00",
  "endDate": "2026-04-01T00:00:00",
  "dailyLimit": -1
}
```

---

### GET /api/subscription/usage
**Статистика использования**

**Response (200 OK):**
```json
{
  "dialogMessages": 50,
  "dialogMessagesRemaining": -1,
  "wordExplanations": 30,
  "wordExplanationsRemaining": -1,
  "exercises": 20,
  "exercisesRemaining": -1
}
```

---

### POST /api/subscription/checkout
**Создать Stripe checkout сессию**

**Request:**
```json
{
  "tier": "PREMIUM"
}
```

**Response (200 OK):**
```json
{
  "sessionId": "cs_test_...",
  "url": "https://checkout.stripe.com/...",
  "expiresAt": 1710432000
}
```

---

### GET /api/subscription/plans
**Тарифные планы**

**Response (200 OK):**
```json
{
  "premium": {
    "id": "premium",
    "name": "Premium",
    "price": "$9.99",
    "interval": "month",
    "features": [
      "Unlimited AI dialogs",
      "Unlimited word explanations",
      "Unlimited exercises",
      "Pronunciation analysis",
      "No ads"
    ]
  },
  "lifetime": {
    "id": "lifetime",
    "name": "Lifetime",
    "price": "$399",
    "interval": "one-time",
    "features": [
      "All Premium features",
      "Lifetime access",
      "Priority support",
      "Early access to new features"
    ]
  }
}
```

---

### POST /api/subscription/cancel
**Отменить подписку**

**Response (200 OK):** No content

---

## 🧠 11. Прагматические ошибки (Soft Skills)

### POST /api/pragmatics/analyze
**Анализ прагматики текста**

**Request:**
```json
{
  "text": "Shut up and send the report!",
  "context": "business",
  "language": "English"
}
```

**Response (200 OK):**
```json
{
  "originalText": "Shut up and send the report!",
  "correctedText": "Could you please send the report?",
  "errorType": "tone",
  "explanation": "This phrase may sound too direct/rude in a formal context",
  "suggestedAlternatives": [
    "Could you please...",
    "I would appreciate if you...",
    "Would you mind..."
  ],
  "severityLevel": 4,
  "isFormalAppropriate": false,
  "isToneAppropriate": false,
  "culturalNote": "In English-speaking business contexts, indirect requests are often more polite",
  "timestamp": "2026-03-13T21:00:00"
}
```

---

### GET /api/pragmatics/recent
**Недавние ошибки**

**Query Params:** `?limit=10`

**Response (200 OK):**
```json
[
  {
    "originalText": "Shut up and send the report!",
    "correctedText": "Could you please...",
    "errorType": "tone",
    "explanation": "...",
    "severityLevel": 4
  }
]
```

---

### GET /api/pragmatics/by-type
**Ошибки по типу**

**Query Params:** `?errorType=tone`

**Response (200 OK):**
```json
[...]
```

---

### POST /api/pragmatics/{id}/helpful
**Отметить исправление как полезное**

**Response (200 OK):** No content

---

## 🎓 12. IELTS/TOEFL Prep

### POST /api/exam-prep/submit
**Сдать практический тест**

**Request:**
```json
{
  "examType": "IELTS",
  "section": "Reading",
  "score": 75,
  "maxScore": 100
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 123,
  "examType": "IELTS",
  "section": "Reading",
  "score": 75,
  "maxScore": 100,
  "feedback": "Good progress. Focus on weak areas.",
  "weakAreas": "Reading needs improvement",
  "completedAt": "2026-03-13T21:00:00"
}
```

---

### GET /api/exam-prep/history
**История тестов**

**Query Params:** `?examType=IELTS`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "examType": "IELTS",
    "section": "Reading",
    "score": 75,
    "maxScore": 100,
    "feedback": "Good progress...",
    "completedAt": "2026-03-13T21:00:00"
  }
]
```

---

### GET /api/exam-prep/average
**Средний балл по секции**

**Query Params:** `?examType=IELTS&section=Reading`

**Response (200 OK):**
```json
{
  "examType": "IELTS",
  "section": "Reading",
  "averageScore": 77.5,
  "maxScore": 100
}
```

---

## 🧠 13. ADHD-Friendly Mode

### GET /api/adhd/profile
**Получить профиль ADHD**

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 123,
  "adhdModeEnabled": true,
  "preferredSessionDuration": 5,
  "frequentBreaks": true,
  "breakFrequency": 5,
  "focusMode": "flexible",
  "visualReminders": true,
  "gamification": true,
  "gentleReminders": true,
  "createdAt": "2026-03-01T00:00:00",
  "updatedAt": "2026-03-13T21:00:00"
}
```

---

### POST /api/adhd/enable
**Включить ADHD режим**

**Response (200 OK):**
```json
{
  "id": 1,
  "adhdModeEnabled": true,
  ...
}
```

---

### POST /api/adhd/disable
**Выключить ADHD режим**

**Response (200 OK):**
```json
{
  "id": 1,
  "adhdModeEnabled": false,
  ...
}
```

---

### PUT /api/adhd/session-duration
**Установить длительность сессии**

**Request:**
```json
{
  "duration": 5
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "preferredSessionDuration": 5,
  ...
}
```

---

### PUT /api/adhd/focus-mode
**Установить режим фокуса**

**Request:**
```json
{
  "mode": "pomodoro" // pomodoro, flow, flexible
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "focusMode": "pomodoro",
  ...
}
```

---

### GET /api/adhd/recommended-duration
**Рекомендуемая длительность**

**Response (200 OK):**
```json
{
  "recommendedMinutes": 5
}
```

---

## 🏢 14. Corporate (B2B)

### POST /api/corporate/create
**Создать корпоративный аккаунт**

**Request:**
```json
{
  "companyName": "Tech Corp",
  "industry": "Technology",
  "maxEmployees": 100
}
```

**Response (200 OK):**
```json
{
  "accountId": 1,
  "accountCode": "CORP-ABC123",
  "message": "Share this code with employees: CORP-ABC123"
}
```

---

### POST /api/corporate/join
**Присоединиться к компании**

**Request:**
```json
{
  "accountCode": "CORP-ABC123",
  "department": "Engineering",
  "position": "Developer"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "employeeId": 1
}
```

---

### GET /api/corporate/dashboard
**Дашборд компании**

**Query Params:** `?accountId=1`

**Response (200 OK):**
```json
{
  "companyName": "Tech Corp",
  "totalEmployees": 50,
  "activeEmployees": 45,
  "subscriptionEndDate": "2027-03-13T00:00:00"
}
```

---

### GET /api/corporate/my-company
**Моя компания**

**Query Params:** `?userId=123`

**Response (200 OK):**
```json
{
  "companyName": "Tech Corp",
  "department": "Engineering",
  "position": "Developer",
  "joinedAt": "2026-03-01T00:00:00"
}
```

---

## 🔔 15. Nudges (Контекстные уведомления)

### GET /api/nudges/unread
**Непрочитанные nudges**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 123,
    "nudgeType": "encouragement",
    "message": "🎉 Great job! 7-day streak! Keep up the momentum!",
    "context": "behavior_based",
    "isRead": false,
    "isActioned": false,
    "createdAt": "2026-03-13T21:00:00"
  }
]
```

---

### GET /api/nudges/history
**История nudges**

**Query Params:** `?limit=20`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nudgeType": "encouragement",
    "message": "🎉 Great job!...",
    "isRead": true,
    "isActioned": true,
    "createdAt": "2026-03-13T21:00:00",
    "readAt": "2026-03-13T21:05:00",
    "actionedAt": "2026-03-13T21:05:00"
  }
]
```

---

### POST /api/nudges/{id}/read
**Отметить как прочитанное**

**Response (200 OK):** No content

---

### POST /api/nudges/{id}/action
**Отметить как выполненное**

**Response (200 OK):** No content

---

## 📊 Error Responses

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Invalid input data",
  "status": 400
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "status": 401
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Insufficient permissions",
  "status": 403
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Resource not found",
  "status": 404
}
```

### 429 Too Many Requests (Rate Limit)
```json
{
  "error": "Rate Limit Exceeded",
  "message": "You have reached your daily limit. Please upgrade to Premium.",
  "status": 429
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "Something went wrong",
  "status": 500
}
```

---

## 🔑 Flutter Implementation Tips

### 1. Dio Client Setup
```dart
class ApiClient {
  final Dio _dio = Dio();
  
  ApiClient() {
    _dio.options.baseUrl = 'https://api.wordsapp.com';
    _dio.interceptors.add(AuthInterceptor());
  }
}

class AuthInterceptor extends Interceptor {
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    final token = secureStorage.read('jwt_token');
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }
}
```

### 2. SSE Streaming (Dialogs)
```dart
import 'package:event/event.dart';

Stream<String> streamDialogMessage(String sessionId, String message) async* {
  final request = await Client().postUrl(
    Uri.parse('$baseUrl/api/dialog/message/stream'),
  );
  request.headers['Authorization'] = 'Bearer $token';
  request.headers['Content-Type'] = 'application/json';
  request.write(jsonEncode({'sessionId': sessionId, 'message': message}));
  
  final response = await request.close();
  await for (var chunk in response.transform(utf8.decoder)) {
    if (chunk.startsWith('data: ')) {
      yield chunk.substring(6);
    }
  }
}
```

### 3. Error Handling
```dart
try {
  final response = await apiClient.login(request);
} on DioException catch (e) {
  if (e.response?.statusCode == 401) {
    // Handle unauthorized
  } else if (e.response?.statusCode == 429) {
    // Handle rate limit
  }
}
```

---

**Всего endpoints: 70+**  
**Документация актуальна на: 2026-03-14**
