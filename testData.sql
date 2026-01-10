INSERT INTO media (id, title, description, media_type, year, age_restriction) VALUES
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01', 'Inception', 'A mind-bending thriller about dream invasion.', 'movie', 2010, 13),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb02', 'Breaking Bad', 'A chemistry teacher turns to drug dealing.', 'series', 2008, 18),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb03', 'The Witcher 3', 'A fantasy RPG with rich storytelling.', 'game', 2015, 18),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb04', 'The Office', 'A mockumentary-style sitcom.', 'series', 2005, 12),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb05', 'The Matrix', 'A hacker discovers reality is a simulation.', 'movie', 1999, 16),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb06', 'God of War', 'A Spartan warrior battles gods and monsters.', 'game', 2018, 18),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb07', 'Friends', 'Six friends navigate life in New York.', 'series', 1994, 12),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb08', 'Interstellar', 'A journey through space and time.', 'movie', 2014, 13),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb09', 'Cyberpunk 2077', 'A futuristic open-world RPG.', 'game', 2020, 18),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb0a', 'The Crown', 'A story about the reign of Queen Elizabeth II.', 'series', 2016, 15);

INSERT INTO media_genres (media_id, genre) VALUES
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01', 'Sci-Fi'), ('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01', 'Thriller'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb02', 'Crime'), ('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb02', 'Drama'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb03', 'RPG'), ('018f3c60-8cd5-7c92-a7e4-cf03a3a1bb03', 'Fantasy');

INSERT INTO users (id, username, password) VALUES
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa01', 'alice', 'password123'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa02', 'bob', 'securepass'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa03', 'carol', 'qwerty'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa04', 'dave', 'letmein'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa05', 'eve', 'passw0rd');

INSERT INTO ratings (id, value, comment, timestamp, confirmed, user_id, media_id) VALUES
('018f3d00-a001-7d12-a8f4-cf03a3a1cc01', 5, 'Amazing concept!', '2025-09-20 14:00:00', false, '018f3c60-8cd5-7c92-a7e4-cf03a3a1aa01', '018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01'),
('018f3d00-a002-7d12-a8f4-cf03a3a1cc02', 4, 'Really good drama.', '2025-09-21 11:00:00', false, '018f3c60-8cd5-7c92-a7e4-cf03a3a1aa01', '018f3c60-8cd5-7c92-a7e4-cf03a3a1bb02'),
('018f3d00-a003-7d12-a8f4-cf03a3a1cc03', 5, 'Fantastic game!', '2025-09-22 10:00:00', true, '018f3c60-8cd5-7c92-a7e4-cf03a3a1aa02', '018f3c60-8cd5-7c92-a7e4-cf03a3a1bb03');

INSERT INTO user_favorites (user_id, media_id) VALUES
-- Alice likes Breaking Bad and Witcher
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa01', '018f3c60-8cd5-7c92-a7e4-cf03a3a1bb02'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa01', '018f3c60-8cd5-7c92-a7e4-cf03a3a1bb03'),
-- Bob likes Inception
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa02', '018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01'),
-- Carol likes The Crown
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa03', '018f3c60-8cd5-7c92-a7e4-cf03a3a1bb0a');

INSERT INTO user_favorite_genres (user_id, genre) VALUES
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa01', 'Sci-Fi'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa02', 'Action'),
('018f3c60-8cd5-7c92-a7e4-cf03a3a1aa03', 'Thriller');

INSERT INTO likes (id, user_id, rating_id) VALUES
-- Bob likes Alice's rating on Inception
('018f3d01-b001-7d12-a8f4-cf03a3a1dd01', '018f3c60-8cd5-7c92-a7e4-cf03a3a1aa02', '018f3d00-a001-7d12-a8f4-cf03a3a1cc01'),
-- Carol likes Alice's rating on Inception
('018f3d01-b002-7d12-a8f4-cf03a3a1dd02', '018f3c60-8cd5-7c92-a7e4-cf03a3a1aa03', '018f3d00-a001-7d12-a8f4-cf03a3a1cc01');