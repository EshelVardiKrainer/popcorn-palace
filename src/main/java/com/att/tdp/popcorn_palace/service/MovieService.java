package com.att.tdp.popcorn_palace.service;
import org.springframework.stereotype.Service;

import com.att.tdp.popcorn_palace.repository.MovieRepository;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
}
