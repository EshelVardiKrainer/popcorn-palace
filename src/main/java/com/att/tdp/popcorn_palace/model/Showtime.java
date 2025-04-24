package com.att.tdp.popcorn_palace.model;
import java.time.Instant;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;



@Entity
@Data
@NoArgsConstructor
public class Showtime {
    
    private Long id;

    @NotNull(message = "Movie is required")
    private Movie movie;

    @NotNull(message = "Theater is required")
    private String theater;

    @NotNull(message = "Start time is required")
    private Instant start_time;

    @NotNull(message = "End time is required")
    private Instant end_time;

    @NotNull(message = "Price is required")
    private Double price;

    public Showtime(Movie movie, String theater, Instant start_time, Instant end_time, Double price) {
        this.movie = movie;
        this.theater = theater;
        this.start_time = start_time;
        this.end_time = end_time;
        this.price = price;
    }
}


