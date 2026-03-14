-- Meme-Based Learning Feature

CREATE TABLE IF NOT EXISTS learning_meme (
    id BIGSERIAL PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    title VARCHAR(500) NOT NULL,
    description VARCHAR(1000),
    meme_type VARCHAR(100),
    language VARCHAR(50),
    difficulty VARCHAR(10),
    cultural_context VARCHAR(500),
    vocabulary_words VARCHAR(1000),
    likes INTEGER DEFAULT 0,
    shares INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_meme_language ON learning_meme(language);
CREATE INDEX idx_meme_difficulty ON learning_meme(difficulty);
CREATE INDEX idx_meme_active ON learning_meme(is_active);
CREATE INDEX idx_meme_popularity ON learning_meme(likes DESC);

-- Sample memes for English learning
INSERT INTO learning_meme (image_url, title, description, meme_type, language, difficulty, cultural_context, vocabulary_words, likes, shares, is_active) VALUES
('https://i.imgflip.com/9ehk.jpg', 'Drake Hotline Bling', 'Drake rejecting formal English, approving slang', 'drake', 'English', 'B1', 'Popular meme format for comparing two things', 'slang, informal, reject, approve', 150, 45, TRUE),
('https://i.imgflip.com/1g8my4.jpg', 'Two Buttons', 'Choosing between studying grammar or vocabulary', 'two_buttons', 'English', 'A2', 'Decision paralysis meme', 'choice, decide, struggle', 120, 30, TRUE),
('https://i.imgflip.com/30b1gx.jpg', 'Distracted Boyfriend', 'Student looking at new slang instead of textbook', 'distracted_boyfriend', 'English', 'B2', 'Temptation and distraction meme', 'distract, tempt, ignore', 200, 67, TRUE),
('https://i.imgflip.com/261o3j.jpg', 'This is Fine', 'When you realize you''ve been using wrong word all along', 'this_is_fine', 'English', 'C1', 'Accepting disaster meme', 'realize, mistake, accept', 180, 52, TRUE),
('https://i.imgflip.com/1h7in3.jpg', 'Change My Mind', 'British English is better than American English', 'change_my_mind', 'English', 'C1', 'Controversial opinion meme', 'opinion, debate, convince', 95, 28, TRUE);
