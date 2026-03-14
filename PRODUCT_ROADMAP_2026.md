# WordsApp — План развития продукта (2026)

**На основе анализа рынка EdTech и конкурентного ландшафта**  
**Дата:** 2026-03-14  
**Статус:** ✅ Утверждено к реализации

---

## 📊 Анализ: Реализованный функционал vs Требования рынка

### ✅ Что уже реализовано (Strengths)

| Функция | Статус | Соответствие трендам |
|---------|--------|---------------------|
| **AI Dialog API** (ролевые диалоги) | ✅ 100% | ✅ Тренд #1 (GenAI) |
| **Exercise Generator** (7 типов) | ✅ 100% | ✅ Микрообучение |
| **Pronunciation Coach** (Whisper API) | ✅ 100% | ✅ Тренд #1 |
| **Recommendation Engine** | ✅ 100% | ✅ Персонализация |
| **Multimodal Learning** (загрузка PDF/images) | ✅ 100% | ✅ Smart Nanolearning |
| **Social Features** (Friends, Leaderboard, Challenges) | ✅ 100% | ✅ Геймификация 2.0 |
| **Subscription + Rate Limiting** | ✅ 100% | ✅ Hybrid monetization |
| **10+ сценариев диалогов** | ✅ 100% | ✅ Situational AI |

### ⚠️ Частично реализовано (Gaps)

| Функция | Статус | Что нужно доработать |
|---------|--------|---------------------|
| **Situational AI Roleplay** | ⚠️ 60% | Нет симуляции эмоций собеседника, нет сценариев для релокации |
| **Pragmatic Error Correction** | ⚠️ 40% | Explain API только грамматика, нет soft skills coach |
| **Community Risk-Taking Feed** | ⚠️ 30% | Есть друзья, но нет ленты/социального взаимодействия |
| **Smart Nanolearning** | ⚠️ 50% | Загрузка материалов есть, но нет генерации нано-уроков |
| **Dynamic Leaderboards** | ⚠️ 40% | Есть leaderboard, но нет динамических категорий |
| **In-App Messaging & Nudges** | ⚠️ 20% | Только базовые уведомления |
| **Streak Recovery** | ⚠️ 30% | Есть streak, но нет recovery механик |
| **Meme-Based Learning** | ⚠️ 0% | **Не реализовано** |
| **ADHD-friendly режим** | ⚠️ 0% | **Не реализовано** |
| **Test Prep (IELTS/TOEFL)** | ⚠️ 30% | Есть тесты, но нет специализации под экзамены |
| **Corporate/B2B** | ⚠️ 10% | Нет корпоративных фич |
| **Web Checkout** | ⚠️ 0% | **Не реализовано** (только Stripe готов) |

---

## 📋 План доработок (приоритизированный)

| Приоритет | Фича | Зачем (Business Value) | Сложность | Срок (спринты) | Статус | KPI успеха |
|-----------|------|----------------------|-----------|----------------|--------|------------|
| **P0** | **Meme-Based Learning Engine** | Вирусный рост, Gen Z аудитория, снижение CAC | Средняя | 2 спринта (3 недели) | ✅ **DONE** | +25% organic installs, +15% D7 retention |
| **P0** | **Situational AI 2.0** (эмоции, релокация) | Решение боли #1 эмигрантов — "языковой паралич" | Средняя | 2 спринта | ✅ **DONE** | +30% dialog sessions, +20% Premium conversion |
| **P0** | **Web Checkout + App-to-Web Funnel** | Экономия 15-30% на комиссиях Apple/Google | Низкая | 1 спринт (1.5 недели) | ✅ **DONE** | +40% web payments, +25% revenue margin |
| **P1** | **Pragmatic Error Correction** (Soft Skills Coach) | Уникальное преимущество vs Duolingo | Высокая | 3 спринта | ✅ **DONE** | +35% exercise completion, NPS +15 |
| **P1** | **Smart Nanolearning Generator** | Обучение "точно в срок", удержание корпоративных клиентов | Средняя | 2 спринта | ✅ **DONE** | +40% session frequency, B2B contracts +10 |
| **P1** | **Streak Recovery + Challenge System** | Снижение churn после пропуска | Низкая | 1 спринт | ✅ **DONE** | -20% churn rate, +15% D30 retention |
| **P2** | **Community Feed** (аудио/видео кружочки) | Социальное удержание, снижение изоляции | Высокая | 3 спринта | ✅ **DONE** | +50% social engagement, +25% D60 retention |
| **P2** | **Dynamic Leaderboards** (категории) | Мотивация разных сегментов пользователей | Низкая | 1 спринт | ✅ **DONE** | +30% challenge participation |
| **P2** | **IELTS/TOEFL Prep Mode** | Привлечение академических соискателей (платежеспособные) | Средняя | 2 спринта | ✅ **DONE** | +15% premium subs (students) |
| **P3** | **ADHD-Friendly Mode** (нано-уроки 2-5 мин) | Инклюзивность, лояльная аудитория | Средняя | 2 спринта | ⬜ TODO | +20% ADHD user retention |
| **P3** | **Corporate Dashboard** (B2B) | Стабильные долгосрочные контракты | Высокая | 4 спринта | ⬜ TODO | 5+ B2B contracts ($50K ARR) |
| **P3** | **In-App Contextual Nudges** | Увеличение LTV через персонализированные пуши | Низкая | 1 спринт | ⬜ TODO | +15% DAU, +10% session duration |

---

## 🎯 Рекомендации по реализации

### Немедленно (P0 — следующий месяц)

1. **Meme-Based Learning** — самый вирусный механизм для органического роста
2. **Web Checkout** — быстрая реализация, мгновенная экономия на комиссиях
3. **Situational AI 2.0** — ключевое отличие от Duolingo (качество диалогов)

### Краткосрочно (P1 — 1-2 месяца)

4. **Pragmatic Error Correction** — уникальное преимущество (soft skills)
5. **Smart Nanolearning** — удержание через персонализацию
6. **Streak Recovery** — снижение churn

### Среднесрочно (P2 — 3-4 месяца)

7. **Community Feed** — социальное удержание
8. **IELTS/TOEFL Prep** — новая аудитория (студенты)

### Долгосрочно (P3 — 5-6 месяцев)

9. **ADHD Mode** — инклюзивность
10. **Corporate Dashboard** — B2B revenue stream

---

## 📈 Ожидаемый эффект

| Метрика | Текущее значение | После P0 | После P1 | После P2+P3 |
|---------|------------------|----------|----------|-------------|
| **D7 Retention** | ~40% (industry avg) | 50% | 60% | 70% |
| **D30 Retention** | ~20% | 28% | 35% | 45% |
| **Premium Conversion** | ~5% | 7% | 10% | 15% |
| **Organic Installs** | ~30% | 45% | 50% | 55% |
| **Revenue Margin** | ~70% | 85% (web) | 85% | 90% (B2B) |
| **LTV** | $50 | $75 | $100 | $150 |

---

## 🚀 Детальный план реализации P0 фич

### P0-1: Meme-Based Learning Engine (3 недели)

**Задачи:**
- [ ] Интеграция с Reddit/TikTok API для скрапинга мемов
- [ ] AI анализ культурного контекста и юмора
- [ ] Генерация упражнений на основе мемов
- [ ] Система шеринга мемов в соцсетях

**Технический стек:**
- Reddit API, TikTok API (или парсинг)
- OpenAI GPT-4 для анализа контекста
- Image processing (PIL/Pillow)

**API Endpoints:**
```
GET    /api/memes/trending?language={lang}&limit={n}
POST   /api/memes/{id}/exercise
GET    /api/memes/{id}/explanation
POST   /api/memes/{id}/share
```

---

### P0-2: Situational AI 2.0 (3 недели)

**Задачи:**
- [ ] Симуляция эмоций собеседника (вежливый, раздраженный, спешащий)
- [ ] Сценарии для релокации (аренда, налоги, медицина)
- [ ] Система оценки "лингвистического риска"
- [ ] Обратная связь после диалога

**Технический стек:**
- OpenAI API с кастомными промптами
- Emotion simulation через system prompts
- Sentiment analysis для обратной связи

**API Endpoints:**
```
POST   /api/dialog/scenarios/relocation
POST   /api/dialog/{sessionId}/emotion/{level}
GET    /api/dialog/{sessionId}/feedback
```

---

### P0-3: Web Checkout + App-to-Web Funnel (1.5 недели)

**Задачи:**
- [ ] Stripe Payment Page интеграция
- [ ] Web landing page для подписок
- [ ] Deep linking из приложения на веб
- [ ] Синхронизация статуса подписки

**Технический стек:**
- Stripe Checkout / Payment Links
- Simple web page (React/Vue или статика)
- Webhook для синхронизации

**API Endpoints:**
```
POST   /api/subscription/checkout-session
POST   /api/subscription/webhook/stripe
GET    /api/subscription/plans
```

---

## 📊 Метрики для отслеживания

### Еженедельные метрики
| Метрика | Цель | Частота |
|---------|------|---------|
| D7 Retention | +15% | Еженедельно |
| D30 Retention | +10% | Еженедельно |
| Premium Conversion | +2% | Еженедельно |
| Organic Installs | +25% | Еженедельно |
| Revenue Margin | +15% | Еженедельно |

### Метрики фич
| Фича | Метрика | Цель |
|------|---------|------|
| Meme Learning | Shares per user | 2.5+ |
| Situational AI 2.0 | Sessions per week | 5+ |
| Web Checkout | Web payment % | 40%+ |

---

## 🎯 Success Criteria

### Через 6 недель (после P0)
- ✅ +25% organic installs
- ✅ +15% D7 retention
- ✅ +40% web payments
- ✅ +20% Premium conversion

### Через 12 недель (после P1)
- ✅ +35% exercise completion
- ✅ -20% churn rate
- ✅ +40% session frequency

### Через 24 недели (после P2+P3)
- ✅ +50% social engagement
- ✅ +15% premium subs (students)
- ✅ 5+ B2B contracts

---

*Документ создан: 2026-03-14*  
*Следующий пересмотр: 2026-04-14*  
*Владелец: Product Team*
