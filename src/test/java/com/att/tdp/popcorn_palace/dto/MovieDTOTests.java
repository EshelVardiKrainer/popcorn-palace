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
        assertNull(movie.getDuration());
        assertNull(movie.getRating());
        assertNull(movie.getRelease_year());

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

        assertEquals(2L, movie.getId());
        assertEquals("The Dark Knight", movie.getTitle());
        assertEquals("Action", movie.getGenre());
        assertEquals(152, movie.getDuration());
        assertEquals(9.0, movie.getRating());
        assertEquals(2008, movie.getRelease_year());
    }
}