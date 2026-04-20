-- 1. Create a test contest (Matches Contest.java)
INSERT INTO contests (id, name, mode, start_time, end_time, duration_minutes)
VALUES (1, 'Smoke Test Contest', 'LADDER', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day', 120)
ON CONFLICT DO NOTHING;

-- 2. Create a test user (Matches User.java)
INSERT INTO users (id, username, email, password_hash, role)
VALUES (1, 'test_nax', 'nax@gladiator.cz', 'dummy_hash', 'USER')
ON CONFLICT DO NOTHING;

-- 3. Create a test team (Matches Team.java, explicitly casting to UUID)
INSERT INTO teams (id, name)
VALUES ('123e4567-e89b-12d3-a456-426614174000'::uuid, 'Gladiator Radiator')
ON CONFLICT DO NOTHING;

-- 4. Link the Team to the Contest (Matches ContestParticipant.java)
INSERT INTO contest_participants (id, started_at, contest_id, team_id)
VALUES (1, CURRENT_TIMESTAMP, 1, '123e4567-e89b-12d3-a456-426614174000'::uuid)
ON CONFLICT DO NOTHING;

-- 5. Assign the User to the Team (Matches TeamMember.java)
INSERT INTO team_members (id, is_captain, team_id, user_id)
VALUES (1, true, '123e4567-e89b-12d3-a456-426614174000'::uuid, 1)
ON CONFLICT DO NOTHING;

-- 6. Create 3 test tasks (Matches Task.java)
INSERT INTO tasks (id, title, description, points_tier, time_limit_ms, memory_limit_kb) VALUES
(1, 'Hello World', 'Write a program that outputs exactly: Hello World!', 100, 2000, 128000),
(2, 'Simple Calculator', 'Read two space-separated numbers and output their sum.', 200, 2000, 128000),
(3, 'Factorial', 'Read one number and output its factorial.', 300, 2000, 128000),
(4, 'Anagram Grouping', 'Read an integer N representing the number of words. The next line contains N space-separated lowercase words. Output the size of the largest group of anagrams.', 400, 2000, 128000)
ON CONFLICT DO NOTHING;

-- 7. Hidden test cases (Matches TestCase.java)
INSERT INTO test_cases (id, task_id, input_data, expected_output, is_hidden) VALUES
(1, 1, '', 'Hello World!', true),
(2, 1, ' ', 'Hello World!', true),
(3, 2, '5 5', '10', true),
(4, 2, '-2 8', '6', true),
(5, 4, '6\neat tea tan ate nat bat', '3', true),
(6, 4, '1\nhello', '1', true),
(7, 4, '4\na b c d', '1', true)
ON CONFLICT DO NOTHING;