INSERT INTO users (id, full_name, email, password_hash, phone, bio, location, headline, profile_photo_url, role, points_balance, average_rating, total_reviews)
VALUES
  (1, 'Aarav Mehta', 'aarav@skillx.local', '$2a$10$NxQqODQ1RhPCqKqricY1KeJasmkWgfOH/tcJKZIcEfs4JKHFRqAAC', '+91-9990001111', 'Frontend engineer who mentors new developers and wants to improve public speaking.', 'Bengaluru', 'Frontend Engineer and UI mentor', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80', 'USER', 1300, 4.80, 6),
  (2, 'Maya Kapoor', 'maya@skillx.local', '$2a$10$NxQqODQ1RhPCqKqricY1KeJasmkWgfOH/tcJKZIcEfs4JKHFRqAAC', '+91-9990002222', 'Product storyteller with a strong background in communication coaching and content strategy.', 'Mumbai', 'Communication Coach and Content Strategist', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80', 'USER', 980, 4.90, 8),
  (3, 'Admin User', 'admin@skillx.local', '$2a$10$NxQqODQ1RhPCqKqricY1KeJasmkWgfOH/tcJKZIcEfs4JKHFRqAAC', '+91-9990003333', 'Platform administrator', 'Delhi', 'Trust and Safety Admin', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=400&q=80', 'ADMIN', 2000, 5.00, 2);

INSERT INTO skills (id, name, category, description, icon)
VALUES
  (1, 'JavaScript', 'Development', 'Modern JavaScript for web apps and product interfaces.', 'code-slash'),
  (2, 'Public Speaking', 'Communication', 'Presentation skills, storytelling, and stage confidence.', 'mic-fill'),
  (3, 'UI Design', 'Design', 'Designing intuitive, accessible, and high-converting interfaces.', 'palette-fill'),
  (4, 'Content Strategy', 'Marketing', 'Planning content systems, editorial calendars, and brand voice.', 'chat-square-text-fill'),
  (5, 'SQL', 'Data', 'Query writing, schema thinking, and database fundamentals.', 'database-fill'),
  (6, 'Product Discovery', 'Product', 'Interviews, research synthesis, and rapid validation.', 'compass-fill');

INSERT INTO user_skills (id, user_id, skill_id, skill_type, proficiency_level, years_of_experience, highlights, is_featured)
VALUES
  (1, 1, 1, 'TEACH', 'ADVANCED', 5, 'Mentored 20+ junior developers through live coding sessions.', TRUE),
  (2, 1, 2, 'LEARN', 'BEGINNER', 0, 'Preparing for meetups and demos.', FALSE),
  (3, 1, 3, 'TEACH', 'INTERMEDIATE', 3, 'Builds polished dashboard UIs.', TRUE),
  (4, 2, 2, 'TEACH', 'ADVANCED', 7, 'Runs workshops for founders and professionals.', TRUE),
  (5, 2, 4, 'TEACH', 'ADVANCED', 6, 'Helps teams build clear, useful content systems.', TRUE),
  (6, 2, 1, 'LEARN', 'INTERMEDIATE', 1, 'Improving technical fluency for product collaboration.', FALSE),
  (7, 2, 5, 'LEARN', 'BEGINNER', 0, 'Learning SQL for analytics.', FALSE),
  (8, 1, 5, 'TEACH', 'INTERMEDIATE', 2, 'Comfortable with SQL joins and schema reviews.', FALSE);

INSERT INTO availability_slots (user_id, weekday, start_time, end_time, timezone)
VALUES
  (1, 'MONDAY', '18:00', '20:00', 'Asia/Kolkata'),
  (1, 'THURSDAY', '19:00', '21:00', 'Asia/Kolkata'),
  (2, 'TUESDAY', '18:30', '20:30', 'Asia/Kolkata'),
  (2, 'SATURDAY', '11:00', '13:00', 'Asia/Kolkata');

INSERT INTO exchange_requests (id, sender_id, receiver_id, offered_user_skill_id, requested_user_skill_id, status, message, preferred_date_time, points_cost)
VALUES
  (1, 1, 2, 1, 4, 'ACCEPTED', 'I can help with JavaScript if you coach me on public speaking.', DATE_ADD(NOW(), INTERVAL 2 DAY), 350);

INSERT INTO exchange_sessions (id, request_id, scheduled_at, duration_minutes, meeting_link, agenda, status)
VALUES
  (1, 1, DATE_ADD(NOW(), INTERVAL 3 DAY), 60, 'https://meet.jit.si/skill-exchange-demo-room', 'Intro, goals, and practice round.', 'SCHEDULED');

INSERT INTO feedback (session_id, reviewer_id, reviewee_id, rating, comment)
VALUES
  (1, 1, 2, 5, 'Maya made the session practical and confidence-building.');

INSERT INTO point_transactions (user_id, session_id, transaction_type, points_delta, balance_after, description)
VALUES
  (1, 1, 'DEBIT', -350, 950, 'Reserved points for scheduled session.'),
  (2, 1, 'CREDIT', 300, 1280, 'Teaching reward for scheduled session.');

INSERT INTO notifications (user_id, title, message, type)
VALUES
  (1, 'Session confirmed', 'Your session with Maya has been scheduled.', 'SESSION'),
  (2, 'New request accepted', 'Aarav accepted the exchange and booked a session.', 'REQUEST');

INSERT INTO moderation_reports (reporter_id, reported_user_id, request_id, reason, details, status)
VALUES
  (1, 2, 1, 'MISCOMMUNICATION', 'Test report for the admin dashboard.', 'OPEN');

INSERT INTO admin_audit_logs (admin_id, action, entity_type, entity_id, details)
VALUES
  (3, 'SEED_REVIEWED', 'MODERATION_REPORT', 1, 'Initial moderation sample for the dashboard.');

INSERT INTO chat_rooms (id, request_id)
VALUES
  (1, 1);

INSERT INTO chat_messages (room_id, sender_id, content, is_seen)
VALUES
  (1, 1, 'Hi Maya, excited for our skill exchange session.', TRUE),
  (1, 2, 'Same here. I will bring a speaking framework and some exercises.', FALSE);
