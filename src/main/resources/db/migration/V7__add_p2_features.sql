-- P2 Features Database Schema

-- Community Feed (audio/video posts)
CREATE TABLE IF NOT EXISTS community_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    content VARCHAR(1000),
    media_url VARCHAR(500),
    media_type VARCHAR(50), -- audio, video
    duration_seconds INTEGER,
    language VARCHAR(50),
    topic VARCHAR(200),
    likes INTEGER DEFAULT 0,
    comments INTEGER DEFAULT 0,
    shares INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP
);

-- Exam Prep (IELTS/TOEFL)
CREATE TABLE IF NOT EXISTS exam_prep_test (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    exam_type VARCHAR(50) NOT NULL, -- IELTS, TOEFL, PTE
    section VARCHAR(50) NOT NULL, -- Reading, Writing, Listening, Speaking
    score INTEGER,
    max_score INTEGER,
    feedback VARCHAR(1000),
    weak_areas VARCHAR(500),
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP
);

-- Indexes
CREATE INDEX idx_community_post_active ON community_post(is_active);
CREATE INDEX idx_community_post_language ON community_post(language);
CREATE INDEX idx_community_post_likes ON community_post(likes DESC);
CREATE INDEX idx_exam_prep_user ON exam_prep_test(user_id);
CREATE INDEX idx_exam_prep_type ON exam_prep_test(exam_type);
