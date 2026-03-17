# Промпт для разработки Flutter мобильного приложения WordsApp

## 📱 Общее описание

Разработай мобильное приложение для изучения иностранных слов с AI-компаньоном на **Flutter**. Приложение использует готовый backend API (Spring Boot).

**Название:** WordsApp  
**Платформы:** iOS + Android  
**Стиль:** Современный, не стандартный Material/Cupertino, уникальная дизайн-система

---

## 🎨 Дизайн-система (ОСОБОЕ ВНИМАНИЕ)

### Цветовая палитра

**Основная палитра (Modern Gradient Vibes):**
```dart
// Градиентный primary цвет (не стандартный синий!)
primaryGradient: LinearGradient(
  colors: [
    Color(0xFF667EEA), // Electric Indigo
    Color(0xFF764BA2), // Purple Dream
    Color(0xFFF093FB), // Soft Pink
  ],
  begin: Alignment.topLeft,
  end: Alignment.bottomRight,
)

// Secondary акценты
accentOrange: Color(0xFFFF6B6B),  // Энергия, достижения
accentGreen: Color(0xFF4ECDC4),   // Успех, прогресс
accentYellow: Color(0xFFFFE66D),  // Внимание, подсказки

// Background (не белый!)
backgroundDark: Color(0xFF0F0F1A),      // Глубокий тёмный
backgroundCard: Color(0xFF1A1A2E),      // Карточки
backgroundElevated: Color(0xFF252540),  // Приподнятые элементы

// Text
textPrimary: Color(0xFFFFFFFFF),
textSecondary: Color(0xFFB8B8D0),
textMuted: Color(0xFF6B6B85),
```

### Типографика

**Шрифт:** `Poppins` или `Inter` (modern, geometric)

```dart
// Заголовки с градиентом
headlineLarge: 32px, weight: 800, gradient: primaryGradient
headlineMedium: 24px, weight: 700
headlineSmall: 20px, weight: 600

// Текст
bodyLarge: 16px, weight: 400, color: textPrimary
bodyMedium: 14px, weight: 400, color: textSecondary
caption: 12px, weight: 500, color: textMuted
```

### Компоненты (Custom UI)

**1. Градиентные кнопки (не стандартные!):**
```dart
- GradientButton: градиентный фон, белая обводка 1px, тень
- GhostButton: прозрачный фон, градиентная обводка
- IconButton: с micro-interactions (scale при нажатии)
```

**2. Карточки с эффектом стекла (Glassmorphism):**
```dart
- GlassCard: полупрозрачный фон, blur: 20, border: white 1px opacity 0.2
- Тени: цветные тени (primary color с opacity 0.3)
- Border radius: 24px (не стандартный 12px!)
```

**3. Поля ввода:**
```dart
- Градиентная обводка при фокусе
- Floating label с анимацией
- Иконки внутри поля (не снаружи)
```

**4. Навигация:**
```dart
- BottomNavigationBar: плавающий, не прилеплен к низу
- Градиентный индикатор активной вкладки
- Анимация иконок (bounce при переключении)
```

---

## ✨ Анимации (ОБЯЗАТЕЛЬНО)

### Micro-interactions

```dart
// При загрузке
- Shimmer effect на скелетонах
- Pulse animation для кнопок CTA

// При взаимодействии
- Scale animation на кнопках (0.95 при нажатии)
- Ripple effect с градиентом
- Swipe to complete (упражнения)

// Переходы между экранами
- Shared axis transition (не стандартный fade!)
- Custom page route с parallax эффектом
```

### Macro-animations

```dart
// Onboarding
- Анимированные иллюстрации (Lottie)
- Parallax scroll эффекты

// Прогресс
- Анимированные progress bars (градиентные)
- Confetti при достижении целей
- Streak counter с fire animation

// AI Диалоги
- Typing indicator с градиентными dots
- Message bubbles с slide-in анимацией
- Voice wave animation для произношения
```

---

## 📐 Структура приложения

### Архитектура

```
lib/
├── core/
│   ├── theme/
│   │   ├── app_theme.dart       # Тема с градиентами
│   │   ├── colors.dart          # Цветовая палитра
│   │   ├── typography.dart      # Шрифты
│   │   └── animations.dart      # Анимации
│   ├── api/
│   │   ├── api_client.dart      # HTTP клиент (Dio)
│   │   ├── endpoints.dart       # API endpoints
│   │   └── interceptors.dart    # Auth interceptor
│   └── utils/
│       ├── validators.dart
│       └── extensions.dart
├── features/
│   ├── auth/
│   │   ├── login_screen.dart
│   │   ├── register_screen.dart
│   │   └── auth_bloc.dart
│   ├── home/
│   │   ├── home_screen.dart
│   │   └── widgets/
│   ├── dialogs/
│   │   ├── dialog_screen.dart
│   │   └── widgets/
│   ├── exercises/
│   ├── memes/
│   ├── profile/
│   └── ...
├── shared/
│   ├── widgets/
│   │   ├── gradient_button.dart
│   │   ├── glass_card.dart
│   │   ├── animated_progress.dart
│   │   └── ...
│   └── models/
└── main.dart
```

### State Management

**BLoC Pattern** (или Riverpod)
- Чистая архитектура
- Testable business logic
- Stream-based state management

---

## 🖥️ Экраны (Detailed)

### 1. Onboarding (3 экрана)

**Дизайн:**
- Полноэкранные иллюстрации (Lottie анимации)
- Градиентный background с moving shapes
- Skip button top-right (ghost style)
- Page indicator с градиентными dots
- CTA кнопка "Начать" с gradient + shadow

**Анимации:**
- Parallax при скролле
- Fade-in текст с slide-up
- Bounce на page indicator

---

### 2. Login / Register

**Дизайн:**
- Logo с gradient (анимированный glow)
- Поля ввода с gradient border при фокусе
- Кнопка "Войти" — gradient full-width
- Social login кнопки (Apple, Google) — glassmorphism
- "Забыли пароль?" — gradient text link

**Анимации:**
- Logo pulse animation
- Form fields slide-in staggered
- Button scale on tap
- Success checkmark animation

---

### 3. Home Screen (Главная)

**Дизайн:**
- App bar: gradient background, floating
- Streak counter: fire animation, top-left
- Today's goals: glass cards с gradient progress
- Quick actions: grid с gradient icons
- Bottom nav: floating, gradient indicator

**Виджеты:**
```dart
- StreakBadge: animated fire emoji + counter
- GoalCard: circular gradient progress
- QuickActionTile: gradient icon + label
- DailyQuote: gradient text, italic
```

**Анимации:**
- Pull-to-refresh с custom gradient indicator
- Cards fade-in staggered
- Streak fire particle effect

---

### 4. AI Dialog Screen

**Дизайн:**
- Character avatar: circular с gradient border
- Message bubbles: gradient (user) vs glass (AI)
- Input field: floating, gradient border
- Voice button: gradient с wave animation
- Emotion indicators: emoji с glow

**Анимации:**
- Typing indicator: 3 gradient dots bouncing
- Message slide-in + fade
- Voice wave: animated bars
- Emotion pulse on character avatar

---

### 5. Meme Learning Screen

**Дизайн:**
- Meme card: full-width, rounded 24px
- Caption overlay: gradient text с shadow
- Interaction buttons: gradient icons (like, share)
- Exercise section: glass card below meme
- Progress: gradient linear progress bar

**Анимации:**
- Meme swipe left/right (Tinder-style)
- Like button: heart explosion
- Share: share icon с bounce
- Exercise reveal: slide-down

---

### 6. Exercises Screen

**Дизайн:**
- Exercise type selector: gradient segmented control
- Question card: glassmorphism с gradient border
- Options: gradient buttons (hover effect)
- Timer: circular gradient progress
- Feedback: gradient toast с icon

**Анимации:**
- Option select: scale + gradient fill
- Correct: green gradient flash + confetti
- Wrong: red gradient shake
- Timer: circular progress с pulse

---

### 7. Pronunciation Screen

**Дизайн:**
- Wave visualization: gradient bars (animated)
- Record button: large gradient circular
- Playback controls: gradient icons
- Score: circular gradient progress
- Feedback: gradient text + suggestions

**Анимации:**
- Voice wave: real-time animated bars
- Record button: pulse while recording
- Score counter: counting up animation
- Feedback slide-up

---

### 8. Community Feed

**Дизайн:**
- Post card: glassmorphism с gradient border
- User avatar: gradient ring
- Media: rounded 24px
- Like/comment/share: gradient icons с count
- Trending badge: gradient pill

**Анимации:**
- Like: heart explosion + counter animate
- Comment: slide-up panel
- Share: share sheet с bounce
- Infinite scroll: fade-in new posts

---

### 9. Leaderboard Screen

**Дизайн:**
- Category selector: gradient segmented control
- Top 3: gradient medals (🥇🥈🥉)
- List items: glass cards с gradient rank badge
- User rank: sticky header с gradient
- Category tabs: gradient indicator

**Анимации:**
- Medal shine effect
- Rank change: slide + fade
- User rank highlight: pulse
- Tab switch: gradient slide

---

### 10. Profile Screen

**Дизайн:**
- Avatar: large circular с gradient border
- Stats grid: glass cards с gradient icons
- Settings: list с gradient icons
- Subscription: gradient card с benefits
- Logout: gradient text button

**Анимации:**
- Avatar tap: scale + rotate
- Stats: counting up animation
- Settings: slide-right transition
- Subscription: gradient shimmer

---

## 🔌 API Integration

### Client Setup

```dart
// Dio client с interceptors
class ApiClient {
  final Dio _dio = Dio();
  
  ApiClient() {
    _dio.options.baseUrl = 'https://api.wordsapp.com';
    _dio.interceptors.add(AuthInterceptor()); // JWT
    _dio.interceptors.add(LogInterceptor());
  }
  
  // Методы для каждого endpoint
  Future<AuthResponse> login(LoginRequest req);
  Future<List<Meme>> getTrendingMemes();
  // ...
}
```

### Endpoints (ключевые)

```dart
// Auth
POST /api/auth/login
POST /api/auth/register

// Home
GET /api/progress
GET /api/recommendations

// Dialogs
GET /api/dialog/scenarios
POST /api/dialog/message
POST /api/dialog/message/stream (SSE)

// Memes
GET /api/memes/trending
POST /api/memes/exercise

// Exercises
POST /api/exercise/generate
POST /api/exercise/submit

// Pronunciation
POST /api/pronunciation

// Community
GET /api/community/trending
POST /api/community

// Leaderboard
GET /api/leaderboard
GET /api/leaderboard/{category}

// Profile
GET /api/subscription
POST /api/subscription/checkout (Stripe)
```

---

## 🎯 Ключевые требования

### Производительность

- Lazy loading для списков
- Image caching (cached_network_image)
- Optimistic UI updates
- Skeleton loaders с shimmer

### Доступность

- Semantic labels для screen readers
- Dynamic text size support
- High contrast mode support
- Keyboard navigation (iPad)

### Offline

- Local cache (Hive или Isar)
- Queue для offline действий
- Sync при восстановлении соединения

### Безопасность

- Secure storage для JWT (flutter_secure_storage)
- Biometric auth support
- Certificate pinning
- Obfuscation в production

---

## 📦 Зависимости (pubspec.yaml)

```yaml
dependencies:
  flutter:
    sdk: flutter
  
  # State Management
  flutter_bloc: ^8.1.3
  equatable: ^2.0.5
  
  # API
  dio: ^5.4.0
  retrofit: ^4.0.3
  
  # Local Storage
  hive: ^2.2.3
  hive_flutter: ^1.1.0
  
  # UI
  lottie: ^3.0.0
  shimmer: ^3.0.0
  flutter_animate: ^4.3.0
  glassmorphism: ^3.0.0
  
  # Images
  cached_network_image: ^3.3.1
  
  # Auth
  flutter_secure_storage: ^9.0.0
  local_auth: ^2.1.8
  
  # Utilities
  get_it: ^7.6.4
  intl: ^0.18.1
```

---

## 🎨 Референсы (Визуальный стиль)

**Приложения для вдохновения:**
- Calm (градиенты, анимации)
- Headspace (иллюстрации, цвета)
- Duolingo (геймификация, прогресс)
- BeReal (glassmorphism)
- Linear (современный dark mode)

**Dribbble/Pinterest search:**
- "Gradient mobile app design"
- "Glassmorphism UI"
- "Dark mode app design"
- "Micro interactions mobile"

---

## ✅ Deliverables

1. **Full app** с всеми экранами
2. **Design system** (theme, colors, typography)
3. **Custom widgets** (gradient buttons, glass cards)
4. **Animations** (micro + macro)
5. **API integration** (все endpoints)
6. **State management** (BLoC)
7. **Tests** (unit + widget)
8. **Build** для iOS + Android

---

**Начни с дизайна системы (цвета, шрифты, компоненты), затем onboarding flow, затем основные фичи по приоритету.**

**Удачи! 🚀**
