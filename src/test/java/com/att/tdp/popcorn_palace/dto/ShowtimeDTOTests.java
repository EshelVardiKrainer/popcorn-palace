package com.att.tdp.popcorn_palace.dto;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ShowtimeDTOTests {

    @Test
    public void testDefaultConstructorAndGettersSetters() {
        ShowtimeDTO showtimeDTO = new ShowtimeDTO();

        // Test default values
        assertNull(showtimeDTO.getId());
        assertNull(showtimeDTO.getPrice());
        assertNull(showtimeDTO.getMovieId());
        assertNull(showtimeDTO.getTheater());
        assertNull(showtimeDTO.getStartTime());
        assertNull(showtimeDTO.getEndTime());

        // Test setters and getters
        showtimeDTO.setId(1L);
        assertEquals(1L, showtimeDTO.getId());

        showtimeDTO.setPrice(15.99);
        assertEquals(15.99, showtimeDTO.getPrice());

        showtimeDTO.setMovieId(101L);
        assertEquals(101L, showtimeDTO.getMovieId());

        showtimeDTO.setTheater("Theater A");
        assertEquals("Theater A", showtimeDTO.getTheater());

        Instant startTime = Instant.now();
        showtimeDTO.setStartTime(startTime);
        assertEquals(startTime, showtimeDTO.getStartTime());

        Instant endTime = startTime.plusSeconds(7200); // 2 hours later
        showtimeDTO.setEndTime(endTime);
        assertEquals(endTime, showtimeDTO.getEndTime());
    }

    @Test
    public void testAllArgsConstructor() {
        Long id = 2L;
        Double price = 12.50;
        Long movieId = 202L;
        String theater = "Theater B";
        Instant startTime = Instant.parse("2024-01-01T10:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T12:30:00Z");

        ShowtimeDTO showtimeDTO = new ShowtimeDTO(id, price, movieId, theater, startTime, endTime);

        assertEquals(id, showtimeDTO.getId());
        assertEquals(price, showtimeDTO.getPrice());
        assertEquals(movieId, showtimeDTO.getMovieId());
        assertEquals(theater, showtimeDTO.getTheater());
        assertEquals(startTime, showtimeDTO.getStartTime());
        assertEquals(endTime, showtimeDTO.getEndTime());
    }
}
