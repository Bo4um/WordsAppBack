-- Initial data for roleplay scenarios
-- Run this after database migration to PostgreSQL

INSERT INTO roleplay_scenario (title, description, language, difficulty, scenario_prompt, is_active, sort_order) VALUES
('Airport Check-in', 'Practice checking in at an airport. You need to get your boarding pass and check your luggage.', 'English', 'A2', 'You are at the airport check-in counter. The airline agent will ask for your passport, ticket, and check your luggage. Practice common airport vocabulary and polite responses.', TRUE, 1),

('Ordering Food', 'Order food at a restaurant. Practice menu vocabulary and polite requests.', 'English', 'A1', 'You are at a restaurant. The waiter will greet you and take your order. Practice ordering food, asking about ingredients, and making special requests.', TRUE, 2),

('Job Interview', 'Practice a job interview for your dream position. Answer common interview questions.', 'English', 'B1', 'You are interviewing for a job. The interviewer will ask about your experience, strengths, weaknesses, and career goals. Practice professional language and confident responses.', TRUE, 3),

('Hotel Check-in', 'Check in at a hotel. Handle reservation issues and special requests.', 'English', 'A2', 'You have arrived at a hotel. The receptionist will check your reservation and give you your room key. Practice hotel vocabulary and handling common issues.', TRUE, 4),

('Doctor Appointment', 'Visit a doctor and describe your symptoms. Get medical advice.', 'English', 'B1', 'You are at a doctor''s office. Describe your symptoms, answer questions about your health, and understand the doctor''s advice. Practice medical vocabulary.', TRUE, 5),

('Shopping for Clothes', 'Shop for clothes in a store. Ask about sizes, colors, and prices.', 'English', 'A1', 'You are in a clothing store. The sales assistant will help you find what you need. Practice asking about sizes, trying on clothes, and making purchases.', TRUE, 6),

('Business Meeting', 'Participate in a business meeting. Present your ideas and discuss projects.', 'English', 'B2', 'You are in a business meeting with colleagues. Practice professional language, presenting ideas, agreeing and disagreeing politely, and making decisions.', TRUE, 7),

('Making Friends', 'Meet new people at a social event. Start conversations and find common interests.', 'English', 'A2', 'You are at a party or social event. Practice small talk, introducing yourself, asking about hobbies and interests, and keeping conversations going.', TRUE, 8),

('Apartment Hunting', 'Look for an apartment to rent. Ask about amenities, prices, and lease terms.', 'English', 'B1', 'You are meeting with a landlord to view an apartment. Ask about rent, utilities, rules, and neighborhood. Practice housing vocabulary and negotiation.', TRUE, 9),

('Tech Support Call', 'Call tech support to solve a computer problem. Describe the issue clearly.', 'English', 'B2', 'You are calling tech support about a computer or internet problem. Describe the issue, follow troubleshooting steps, and ask clarifying questions.', TRUE, 10);
