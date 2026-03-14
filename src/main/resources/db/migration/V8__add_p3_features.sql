-- P3 Features Database Schema

-- ADHD Learning Profile
CREATE TABLE IF NOT EXISTS adhd_learning_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    adhd_mode_enabled BOOLEAN DEFAULT FALSE,
    preferred_session_duration INTEGER DEFAULT 3, -- minutes
    frequent_breaks BOOLEAN DEFAULT TRUE,
    break_frequency INTEGER DEFAULT 5, -- minutes
    focus_mode VARCHAR(50), -- pomodoro, flow, flexible
    visual_reminders BOOLEAN DEFAULT TRUE,
    gamification BOOLEAN DEFAULT TRUE,
    gentle_reminders BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Corporate Accounts (B2B)
CREATE TABLE IF NOT EXISTS corporate_account (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    industry VARCHAR(100),
    account_code VARCHAR(50) NOT NULL UNIQUE,
    max_employees INTEGER,
    current_employees INTEGER DEFAULT 0,
    subscription_end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Corporate Employees
CREATE TABLE IF NOT EXISTS corporate_employee (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    corporate_account_id BIGINT NOT NULL REFERENCES corporate_account(id) ON DELETE CASCADE,
    department VARCHAR(100),
    position VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_active_at TIMESTAMP
);

-- User Nudges (Contextual Notifications)
CREATE TABLE IF NOT EXISTS user_nudge (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    nudge_type VARCHAR(50) NOT NULL, -- reminder, encouragement, challenge, tip
    message VARCHAR(500) NOT NULL,
    context VARCHAR(100), -- time_based, behavior_based, milestone
    is_read BOOLEAN DEFAULT FALSE,
    is_actioned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    actioned_at TIMESTAMP
);

-- Indexes
CREATE INDEX idx_adhd_user ON adhd_learning_profile(user_id);
CREATE INDEX idx_corporate_account_code ON corporate_account(account_code);
CREATE INDEX idx_corporate_employee_account ON corporate_employee(corporate_account_id);
CREATE INDEX idx_corporate_employee_user ON corporate_employee(user_id);
CREATE INDEX idx_nudge_user ON user_nudge(user_id);
CREATE INDEX idx_nudge_unread ON user_nudge(user_id, is_read);
