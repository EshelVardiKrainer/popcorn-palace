package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Movie {
    private Long id;
    private String title;
    private String genre;
    private String duration;
    private String rating;
    private String release_year;


    public Movie(String title, String genre, String duration, String rating, String release_year) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.release_year = release_year;
    }
}   
