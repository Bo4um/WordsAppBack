-- Enhanced Scenarios with Emotion Simulation (Situational AI 2.0)

CREATE TABLE IF NOT EXISTS enhanced_scenario (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    category VARCHAR(50) NOT NULL,
    language VARCHAR(50) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    estimated_duration INTEGER,
    learning_objectives VARCHAR(1000),
    system_prompt VARCHAR(3000),
    is_active BOOLEAN DEFAULT TRUE,
    completion_count INTEGER DEFAULT 0,
    average_rating DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS scenario_emotions (
    scenario_id BIGINT REFERENCES enhanced_scenario(id) ON DELETE CASCADE,
    emotion VARCHAR(50)
);

-- Indexes
CREATE INDEX idx_scenario_category ON enhanced_scenario(category);
CREATE INDEX idx_scenario_language ON enhanced_scenario(language);
CREATE INDEX idx_scenario_difficulty ON enhanced_scenario(difficulty);
CREATE INDEX idx_scenario_active ON enhanced_scenario(is_active);

-- Sample Relocation Scenarios
INSERT INTO enhanced_scenario (title, description, category, language, difficulty, estimated_duration, learning_objectives, system_prompt, is_active, completion_count, average_rating) VALUES
('Apartment Hunting', 'Find and rent an apartment in a new country. Deal with landlords, understand lease terms, and ask about utilities.', 'relocation', 'English', 'B1', 15, 'Learn housing vocabulary, practice negotiation, understand rental agreements', 'You are a landlord who is sometimes impatient but generally helpful. You''ve been renting apartments for 20 years. You prefer tenants who ask clear questions and are serious about renting. Occasionally sigh when the tenant asks basic questions, but provide helpful information.', TRUE, 0, NULL),

('Bank Account Setup', 'Open a bank account in your new country. Provide required documents and understand banking terms.', 'relocation', 'English', 'A2', 10, 'Learn banking vocabulary, practice formal conversations, understand documentation requirements', 'You are a bank teller who is professional and efficient. You''ve explained this process hundreds of times. You''re polite but can become slightly impatient if the customer doesn''t have all required documents.', TRUE, 0, NULL),

('Doctor Visit', 'Visit a doctor for a checkup or health concern. Describe symptoms and understand medical advice.', 'relocation', 'English', 'B2', 20, 'Learn medical vocabulary, practice describing symptoms, understand healthcare system', 'You are a busy doctor with many patients waiting. You''re skilled and caring but often rushed. You use medical terminology but try to explain in simple terms when needed.', TRUE, 0, NULL),

('Job Interview', 'Interview for a position in an international company. Answer behavioral and technical questions.', 'relocation', 'English', 'C1', 30, 'Practice professional communication, learn interview techniques, build confidence', 'You are an HR manager conducting interviews. You''re friendly but evaluating critically. You ask follow-up questions and occasionally challenge the candidate''s answers to see how they handle pressure.', TRUE, 0, NULL),

('Government Office', 'Navigate bureaucracy at a government office for visa, ID, or permit applications.', 'relocation', 'English', 'B1', 25, 'Learn bureaucratic language, practice formal requests, understand official procedures', 'You are a government clerk who has seen it all. You''re neither particularly helpful nor unhelpful. You follow procedures strictly and get annoyed when people don''t have proper documentation.', TRUE, 0, NULL);

-- Add emotions to scenarios
INSERT INTO scenario_emotions (scenario_id, emotion) VALUES
(1, 'impatient'), (1, 'helpful'), (1, 'skeptical'),
(2, 'professional'), (2, 'efficient'), (2, 'slightly_impatient'),
(3, 'busy'), (3, 'caring'), (3, 'rushed'),
(4, 'friendly'), (4, 'critical'), (4, 'challenging'),
(5, 'neutral'), (5, 'strict'), (5, 'annoyed');

-- Business Scenarios
INSERT INTO enhanced_scenario (title, description, category, language, difficulty, estimated_duration, learning_objectives, is_active) VALUES
('Salary Negotiation', 'Negotiate your salary with HR or manager. Present your value and handle counter-arguments.', 'business', 'English', 'C1', 20, 'Learn negotiation phrases, practice persuasion, build confidence', TRUE),
('Team Meeting', 'Participate in a team meeting. Present updates and respond to feedback.', 'business', 'English', 'B2', 15, 'Practice meeting vocabulary, learn to interrupt politely, give updates', TRUE);

INSERT INTO scenario_emotions (scenario_id, emotion) VALUES
(6, 'skeptical'), (6, 'interested'), (6, 'challenging'),
(7, 'supportive'), (7, 'critical'), (7, 'curious');
