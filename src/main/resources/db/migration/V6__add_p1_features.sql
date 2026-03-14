-- P1 Features Database Schema

-- Pragmatic Error Correction
CREATE TABLE IF NOT EXISTS pragmatic_error (
    id BIGSERIAL PRIMARY KEY,
    user_utterance VARCHAR(1000) NOT NULL,
    corrected_version VARCHAR(1000) NOT NULL,
    error_type VARCHAR(100),
    explanation VARCHAR(1000),
    context VARCHAR(200),
    suggested_alternatives VARCHAR(500),
    severity_level INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    is_helpful BOOLEAN
);

-- Nano-learning Sessions
CREATE TABLE IF NOT EXISTS nanolearning_session (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(2000),
    content_type VARCHAR(50),
    duration_minutes INTEGER,
    language VARCHAR(50),
    difficulty VARCHAR(10),
    vocabulary_list VARCHAR(1000),
    quiz_score INTEGER,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- Streak Recovery Tokens
CREATE TABLE IF NOT EXISTS streak_recovery (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tokens_count INTEGER DEFAULT 1,
    max_tokens INTEGER DEFAULT 3,
    last_used_date DATE,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    expires_at DATE,
    UNIQUE(user_id)
);

-- Indexes
CREATE INDEX idx_pragmatic_error_type ON pragmatic_error(error_type);
CREATE INDEX idx_pragmatic_severity ON pragmatic_error(severity_level);
CREATE INDEX idx_nano_completed ON nanolearning_session(is_completed);
CREATE INDEX idx_nano_language ON nanolearning_session(language);
CREATE INDEX idx_streak_user ON streak_recovery(user_id);
