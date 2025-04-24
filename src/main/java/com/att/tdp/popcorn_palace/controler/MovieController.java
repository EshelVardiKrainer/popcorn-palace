package com.att.tdp.popcorn_palace.controler;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.att.tdp.popcorn_palace.model.Movie;

@RestController
public class MovieController {
    private MovieService movieService;

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.fetchAllMovies();
        return ResponseEntity.ok(movies);
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(Movie movie) {
        Movie addedMovie = movieService.addMovie(movie);
        return ResponseEntity.ok(addedMovie);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(Movie updatedMovie, String movieTitle) {
        Movie movie = movieService.updateMovie(updatedMovie, movieTitle);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.noContent().build();
    }

}
