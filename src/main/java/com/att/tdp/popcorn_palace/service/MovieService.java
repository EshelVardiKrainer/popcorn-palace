package com.att.tdp.popcorn_palace.service;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import java.util.List;
import com.att.tdp.popcorn_palace.model.Movie;

@Service
public class MovieService {
    private MovieRepository movieRepository;


    public Movie addMovie(Movie movie) {
       if (movieRepository.findByTitle(movie.getTitle()) != null) {
            throw new IllegalArgumentException("Movie title already exists");
        }
        return movieRepository.save(movie);
    }

    public List<Movie> fetchAllMovies() {
        return movieRepository.findAll();
    }

    public boolean deleteMovie(String title) {
        Movie movie = movieRepository.findByTitle(title);
        if (movie == null) {
            throw new IllegalArgumentException("Movie title not found");
        }
        movieRepository.deleteById(movie.getId());
        return true;
    }

    public Movie updateMovie(Movie updatedMovie, String title) {
        Movie movie = movieRepository.findByTitle(title);
        // Check if the movie with the given title exists
        if(movie == null){
            throw new IllegalArgumentException("Movie title not found");
        }
        // Check if the updated movie title already exists
        if ((!updatedMovie.getTitle().equals(title) && movieRepository.findByTitle(updatedMovie.getTitle()) != null)) {
            throw new IllegalArgumentException("Movie title already exists");
        }
       
        BeanUtils.copyProperties(updatedMovie, movie, "id");
        return movieRepository.save(movie);

       
    }

}
