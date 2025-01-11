-- Packages Table
CREATE TABLE IF NOT EXISTS packages (
    id SERIAL PRIMARY KEY
);

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    coins INTEGER DEFAULT 20
);

-- Cards Table
CREATE TABLE IF NOT EXISTS cards (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    damage FLOAT NOT NULL,
    package_id INTEGER REFERENCES packages(id),
    owner_id INTEGER REFERENCES users(id)
);

-- Trades Table
CREATE TABLE IF NOT EXISTS trades (
    id UUID PRIMARY KEY,
    cardToTrade UUID REFERENCES cards(id),  -- Corrected column name
    type VARCHAR(50),  -- Corrected column name
    minimumDamage FLOAT,  -- Corrected column name
    owner_id INTEGER REFERENCES users(id)
);

-- Battles Table
CREATE TABLE IF NOT EXISTS battles (
    id SERIAL PRIMARY KEY,
    userA_id INTEGER REFERENCES users(id),
    userB_id INTEGER REFERENCES users(id),
    winner_id INTEGER REFERENCES users(id),
    log TEXT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Deck Table
CREATE TABLE IF NOT EXISTS deck (
    user_id INT,
    card_id UUID,
    PRIMARY KEY (user_id, card_id)
);

