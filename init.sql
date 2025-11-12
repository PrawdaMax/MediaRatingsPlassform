-- ========================================
-- ENUM TYPES
-- ========================================
CREATE TYPE media_type AS ENUM ('movie', 'series', 'game');

-- ========================================
-- MEDIA TABLE
-- ========================================
CREATE TABLE media (
  id UUID PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  media_type media_type NOT NULL,
  year INT,
  age_restriction INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Media genres (many-to-many)
CREATE TABLE media_genres (
  media_id UUID REFERENCES media(id) ON DELETE CASCADE,
  genre VARCHAR(100) NOT NULL,
  PRIMARY KEY (media_id, genre)
);

-- ========================================
-- USERS TABLE
-- ========================================
CREATE TABLE users (
  id UUID PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User favorite media (many-to-many)
CREATE TABLE user_favorites (
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  media_id UUID REFERENCES media(id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, media_id)
);

-- User favorite genres
CREATE TABLE user_favorite_genres (
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  genre VARCHAR(100),
  PRIMARY KEY (user_id, genre)
);

-- ========================================
-- RATINGS TABLE
-- ========================================
CREATE TABLE ratings (
  id UUID PRIMARY KEY,
  value INT CHECK (value BETWEEN 1 AND 10),
  comment TEXT,
  timestamp TIMESTAMP,
  confirmed BOOLEAN DEFAULT FALSE,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  media_id UUID REFERENCES media(id) ON DELETE CASCADE
);

-- ========================================
-- LIKES TABLE
-- ========================================
CREATE TABLE likes (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  rating_id UUID REFERENCES ratings(id) ON DELETE CASCADE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- TOKENS TABLE
-- ========================================
CREATE TABLE tokens (
  token TEXT PRIMARY KEY
);
