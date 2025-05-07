-- Basic schema for Popcorn Palace entities for testing purposes

-- Users Table (kept for potential use by other tests, though Ticket model uses customer_id UUID)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)
);

-- Movies Table
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    duration INT, -- Duration in minutes
    rating DOUBLE PRECISION,
    release_year INT,
    director VARCHAR(255),
    description TEXT,
    poster_url VARCHAR(255),
    trailer_url VARCHAR(255)
    -- Adding UNIQUE constraint on title to match main schema and avoid issues in tests that might rely on it
    -- CONSTRAINT unique_movie_title_test UNIQUE (title)
);

-- Showtime Table (Named 'showtime' to match entity mapping if not explicitly overridden)
CREATE TABLE IF NOT EXISTS showtime (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    theater VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Tickets Table
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    showtime_id BIGINT NOT NULL,
    seat_number INTEGER NOT NULL,      -- Aligned with Ticket.seat_number (Integer)
    customer_id UUID NOT NULL,         -- Aligned with Ticket.customer_id (UUID)
    booking_id UUID NOT NULL UNIQUE,   -- Aligned with Ticket.booking_id (UUID)
    FOREIGN KEY (showtime_id) REFERENCES showtime(id) ON DELETE CASCADE, -- References 'showtime' table
    UNIQUE (showtime_id, seat_number)  -- Ensures a seat is not double-booked for the same showtime
);
