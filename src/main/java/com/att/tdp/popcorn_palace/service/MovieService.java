package com.att.tdp.popcorn_palace.service;

import lombok.RequiredArgsConstructor;      // ✨ add
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.exceptions.DuplicateMovieTitleException;
import com.att.tdp.popcorn_palace.exceptions.MovieNotFoundException;
import com.att.tdp.popcorn_palace.model.Movie;

import java.util.List;

@Service
@RequiredArgsConstructor                 // ✨ generates a constructor for all final fields
public class MovieService {

    private final MovieRepository movieRepository;   // ✨ make it final and let Spring inject it

    public Movie addMovie(Movie movie) {
        System.out.println("Adding movie: " + movie);
        if (movieRepository.findByTitle(movie.getTitle()) != null) {
            throw new DuplicateMovieTitleException(movie.getTitle());
        }
        return movieRepository.save(movie);
    }

    public List<Movie> fetchAllMovies() {
        return movieRepository.findAll();
    }

    public boolean deleteMovie(String title) {
        Movie movie = movieRepository.findByTitle(title);
        if (movie == null) {
            throw new MovieNotFoundException(title);
        }
        movieRepository.deleteById(movie.getId());
        return true;
    }

    public Movie updateMovie(Movie updatedMovie, String title) {
        Movie movie = movieRepository.findByTitle(title);
        if (movie == null) {
            throw new MovieNotFoundException(title);
        }
        if (!updatedMovie.getTitle().equals(title) &&
            movieRepository.findByTitle(updatedMovie.getTitle()) != null) {
            throw new DuplicateMovieTitleException(title);
        }
        BeanUtils.copyProperties(updatedMovie, movie, "id");
        return movieRepository.save(movie);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }
}
