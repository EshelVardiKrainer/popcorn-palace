package com.att.tdp.popcorn_palace.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MovieDTOTests {

    @Test
    public void testDefaultConstructorAndGettersSetters() {
        MovieDTO movie = new MovieDTO();

        // Test default values (if any, typically null for objects, 0 for primitives)
        assertNull(movie.getId());
        assertNull(movie.getTitle());
        assertNull(movie.getGenre());
        // Assuming duration and release_year are int (default 0) and rating is double (default 0.0).
        // Primitive types cannot be null.
        assertEquals(null, movie.getId());
        assertEquals(null, movie.getDuration()); // Changed from assertNull(movie.getDuration());
        assertEquals(null, movie.getRating());  // Changed from assertNull(movie.getRating());
        assertEquals(null, movie.getRelease_year()); // Changed from assertNull(movie.getRelease_year());

        // Test setters and getters for defined fields
        movie.setId(1L);
        assertEquals(1L, movie.getId());

        movie.setTitle("Inception");
        assertEquals("Inception", movie.getTitle());

        movie.setGenre("Sci-Fi");
        assertEquals("Sci-Fi", movie.getGenre());

        movie.setDuration(148);
        assertEquals(148, movie.getDuration());

        movie.setRating(8.8);
        assertEquals(8.8, movie.getRating());

        movie.setRelease_year(2010);
        assertEquals(2010, movie.getRelease_year());
    }

    @Test
    public void testAllArgsConstructor() {
        MovieDTO movie = new MovieDTO(2L, "The Dark Knight", "Action", 152, 9.0, 2008);

        assertEquals("The Dark Knight", movie.getTitle());
        assertEquals("Action", movie.getGenre());
        assertEquals(152, movie.getDuration());
        assertEquals(9.0, movie.getRating());
        assertEquals(2008, movie.getRelease_year());
    }
}