package com.att.tdp.popcorn_palace.service;
import com.att.tdp.popcorn_palace.repository.MovieRepository;

public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
}
