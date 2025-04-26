-- Popcorn Palace Database Schema (Simplified - No Indexes)

-- Movies Table
CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    genre VARCHAR(255) NOT NULL,
    duration INTEGER NOT NULL, -- Duration in minutes
    rating DOUBLE PRECISION NOT NULL,
    release_year INTEGER NOT NULL
);

-- Showtime Table
CREATE TABLE IF NOT EXISTS showtime (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE, -- Foreign key to movies
    theater VARCHAR(255) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    price DOUBLE PRECISION NOT NULL
);

-- Ticket Table (Using plural name 'tickets' to match @Table annotation)
CREATE TABLE IF NOT EXISTS tickets ( -- <<< CHANGED table name back to plural
    id BIGSERIAL PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES showtime(id) ON DELETE CASCADE, -- Foreign key to showtime
    seat_number INTEGER NOT NULL, -- Seat number for this booking
    customer_id UUID NOT NULL,    -- Customer identifier
    booking_id UUID NOT NULL UNIQUE, -- Unique booking reference
    -- Ensure a seat number is unique for a given showtime
    CONSTRAINT unique_seat_per_showtime_tickets UNIQUE (showtime_id, seat_number) -- <<< Renamed constraint slightly
);

