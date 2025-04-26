package com.att.tdp.popcorn_palace.controler;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.model.Movie;

@RestController
public class MovieController {
    private MovieService movieService;

    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        List<Movie> movies = movieService.fetchAllMovies();
        List<MovieDTO> movies_dto = new LinkedList<>();
        for (Movie movie : movies) {
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setId(movie.getId());
            movieDTO.setTitle(movie.getTitle());
            movieDTO.setGenre(movie.getGenre());
            movieDTO.setDuration(movie.getDuration());
            movieDTO.setRating(movie.getRating());
            movieDTO.setRelease_year(movie.getRelease_year());
            movies_dto.add(movieDTO);
        }
        return ResponseEntity.ok(movies_dto);
    
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(MovieDTO movieDTO) {
        Movie addedMovie = movieService.addMovie(new Movie(movieDTO.getTitle(), movieDTO.getGenre(), movieDTO.getDuration(), movieDTO.getRating(), movieDTO.getRelease_year()));
        return ResponseEntity.ok(addedMovie);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(MovieDTO updatedMovie, String movieTitle) {
        Movie movie = movieService.updateMovie(new Movie(updatedMovie.getTitle(), updatedMovie.getGenre(), updatedMovie.getDuration(), updatedMovie.getRating(), updatedMovie.getRelease_year()), movieTitle);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.noContent().build();
    }

}
