package com.att.tdp.popcorn_palace.service;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> fetchAllMovies() {
        return movieRepository.findAll();
    }

}
