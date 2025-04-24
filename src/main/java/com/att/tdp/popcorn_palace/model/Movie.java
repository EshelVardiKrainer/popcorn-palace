package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Movie {
    private Long id;

    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "Genre is required")
    private String genre;

    @NotNull(message = "Duration is required")
    private String duration;
    
    @NotNull(message = "Rating is required")
    private String rating;

    @NotNull(message = "Release year is required")
    private Integer release_year;


    public Movie(String title, String genre, String duration, Double rating, Integer release_year) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.release_year = release_year;
    }
}   
