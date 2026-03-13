-- WordsApp Database Schema for PostgreSQL
-- Run this script to initialize the database

-- Enable UUID extension if needed
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER'
);

-- Characters table
CREATE TABLE IF NOT EXISTS characters (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sex VARCHAR(50),
    image BYTEA,
    description VARCHAR(1000),
    is_system BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER
);

-- User Progress table
CREATE TABLE IF NOT EXISTS user_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    last_visit_date DATE,
    total_words_learned INTEGER DEFAULT 0,
    join_date DATE,
    UNIQUE(user_id)
);

-- Dictionary Progress table
CREATE TABLE IF NOT EXISTS dictionary_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    dictionary_name VARCHAR(255) NOT NULL,
    words_learned INTEGER DEFAULT 0,
    total_words INTEGER,
    last_updated TIMESTAMP
);

-- Word Learning table
CREATE TABLE IF NOT EXISTS word_learning (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    word VARCHAR(255) NOT NULL,
    language VARCHAR(100) NOT NULL,
    learned_at TIMESTAMP,
    repetitions INTEGER DEFAULT 0,
    next_review DATE
);

-- Language Tests table
CREATE TABLE IF NOT EXISTS language_test (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    language VARCHAR(100) NOT NULL,
    total_questions INTEGER,
    passing_score INTEGER,
    is_active BOOLEAN DEFAULT TRUE
);

-- Test Questions table
CREATE TABLE IF NOT EXISTS test_question (
    id BIGSERIAL PRIMARY KEY,
    test_id BIGINT NOT NULL REFERENCES language_test(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500),
    option_b VARCHAR(500),
    option_c VARCHAR(500),
    option_d VARCHAR(500),
    level VARCHAR(10),
    points INTEGER DEFAULT 1,
    order_number INTEGER
);

-- User Test Results table
CREATE TABLE IF NOT EXISTS user_test_result (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    test_id BIGINT NOT NULL REFERENCES language_test(id) ON DELETE CASCADE,
    score INTEGER,
    max_score INTEGER,
    percentage DOUBLE PRECISION,
    determined_level VARCHAR(10),
    completed_at TIMESTAMP
);

-- Roleplay Scenarios table (NEW for AI Dialog)
CREATE TABLE IF NOT EXISTS roleplay_scenario (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    language VARCHAR(100) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    scenario_prompt TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER
);

-- Conversation Sessions table (NEW for AI Dialog)
CREATE TABLE IF NOT EXISTS conversation_session (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    scenario_id BIGINT NOT NULL REFERENCES roleplay_scenario(id) ON DELETE CASCADE,
    character_id BIGINT REFERENCES characters(id) ON DELETE SET NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    topic VARCHAR(1000)
);

-- Conversation Messages table (NEW for AI Dialog)
CREATE TABLE IF NOT EXISTS conversation_message (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES conversation_session(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    token_usage INTEGER
);

-- User Subscriptions table (NEW for Subscription)
CREATE TABLE IF NOT EXISTS user_subscription (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tier VARCHAR(50) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    stripe_customer_id VARCHAR(255),
    stripe_subscription_id VARCHAR(255),
    UNIQUE(user_id)
);

-- API Usage Stats table (NEW for Rate Limiting)
CREATE TABLE IF NOT EXISTS api_usage_stats (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    endpoint VARCHAR(255) NOT NULL,
    usage_date DATE NOT NULL,
    request_count INTEGER DEFAULT 0,
    UNIQUE(user_id, endpoint, usage_date)
);

-- Exercises table (NEW for Exercise Generator)
CREATE TABLE IF NOT EXISTS exercise (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    type VARCHAR(50) NOT NULL,
    question TEXT NOT NULL,
    correct_answer TEXT,
    hint TEXT,
    explanation TEXT,
    language VARCHAR(100) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    is_completed BOOLEAN DEFAULT FALSE
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_user_progress_user ON user_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_dictionary_progress_user ON dictionary_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_word_learning_user ON word_learning(user_id);
CREATE INDEX IF NOT EXISTS idx_word_learning_next_review ON word_learning(next_review);
CREATE INDEX IF NOT EXISTS idx_test_questions_test ON test_question(test_id);
CREATE INDEX IF NOT EXISTS idx_user_test_results_user ON user_test_result(user_id);
CREATE INDEX IF NOT EXISTS idx_conversation_session_user ON conversation_session(user_id);
CREATE INDEX IF NOT EXISTS idx_conversation_message_session ON conversation_message(session_id);
CREATE INDEX IF NOT EXISTS idx_exercise_user ON exercise(user_id);
CREATE INDEX IF NOT EXISTS idx_api_usage_user_date ON api_usage_stats(user_id, usage_date);

-- Comments
COMMENT ON TABLE users IS 'Пользователи приложения';
COMMENT ON TABLE characters IS 'Персонажи-компаньоны для диалогов';
COMMENT ON TABLE user_progress IS 'Общий прогресс пользователя (streak, слова)';
COMMENT ON TABLE dictionary_progress IS 'Прогресс по языковым словарям';
COMMENT ON TABLE word_learning IS 'Изученные слова с интервальным повторением';
COMMENT ON TABLE language_test IS 'Тесты для определения уровня языка';
COMMENT ON TABLE roleplay_scenario IS 'Сценарии для ролевых диалогов с AI';
COMMENT ON TABLE conversation_session IS 'Сессии AI диалогов';
COMMENT ON TABLE user_subscription IS 'Подписки пользователей (FREE/PREMIUM/LIFETIME)';
COMMENT ON TABLE api_usage_stats IS 'Статистика использования API для rate limiting';
COMMENT ON TABLE exercise IS 'Упражнения для практики языка';
