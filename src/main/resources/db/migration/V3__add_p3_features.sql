-- P3 Features Database Schema

-- Pronunciation Attempts
CREATE TABLE IF NOT EXISTS pronunciation_attempt (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_phrase VARCHAR(500) NOT NULL,
    recognized_text TEXT,
    accuracy_score INTEGER,
    feedback TEXT,
    audio_path VARCHAR(500),
    attempted_at TIMESTAMP,
    status VARCHAR(50)
);

-- Friendships
CREATE TABLE IF NOT EXISTS friendship (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, friend_id)
);

-- Weekly Challenges
CREATE TABLE IF NOT EXISTS weekly_challenge (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    type VARCHAR(50) NOT NULL,
    target_value INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reward_points INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- User Challenge Progress
CREATE TABLE IF NOT EXISTS user_challenge_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id BIGINT NOT NULL REFERENCES weekly_challenge(id) ON DELETE CASCADE,
    current_value INTEGER NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP,
    reward_claimed BOOLEAN DEFAULT FALSE,
    UNIQUE(user_id, challenge_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_pronunciation_user ON pronunciation_attempt(user_id);
CREATE INDEX IF NOT EXISTS idx_friendship_user ON friendship(user_id);
CREATE INDEX IF NOT EXISTS idx_friendship_friend ON friendship(friend_id);
CREATE INDEX IF NOT EXISTS idx_challenge_active ON weekly_challenge(is_active, end_date);
CREATE INDEX IF NOT EXISTS idx_challenge_progress_user ON user_challenge_progress(user_id);

-- Sample challenges
INSERT INTO weekly_challenge (title, description, type, target_value, start_date, end_date, reward_points, is_active) VALUES
('Week 1: Word Master', 'Learn 100 new words this week', 'WORDS_LEARNED', 100, CURRENT_DATE - 7, CURRENT_DATE + 7, 500, TRUE),
('Week 1: Streak Champion', 'Maintain a 7-day streak', 'STREAK_DAYS', 7, CURRENT_DATE - 7, CURRENT_DATE + 7, 300, TRUE),
('Week 1: Exercise Warrior', 'Complete 50 exercises', 'EXERCISES_DONE', 50, CURRENT_DATE - 7, CURRENT_DATE + 7, 400, TRUE),
('Week 1: Conversation Pro', 'Complete 10 dialog sessions', 'DIALOGS_COMPLETED', 10, CURRENT_DATE - 7, CURRENT_DATE + 7, 350, TRUE),
('Week 1: Pronunciation Perfect', 'Practice pronunciation 20 times', 'PRONUNCIATION', 20, CURRENT_DATE - 7, CURRENT_DATE + 7, 250, TRUE);
