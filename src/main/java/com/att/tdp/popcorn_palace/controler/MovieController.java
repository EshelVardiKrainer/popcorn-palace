package com.att.tdp.popcorn_palace.controler;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import com.att.tdp.popcorn_palace.service.MovieService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.model.Movie;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor 
public class MovieController {
    private final MovieService movieService;

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
            movieDTO.setReleaseYear(movie.getRelease_year());
            movies_dto.add(movieDTO);
        }
        return ResponseEntity.ok(movies_dto);
    
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@Valid
                                        @RequestBody MovieDTO movieDTO) {
        Movie added = movieService.addMovie(
            new Movie(movieDTO.getTitle(),
                    movieDTO.getGenre(),
                    movieDTO.getDuration(),
                    movieDTO.getRating(),
                    movieDTO.getReleaseYear()));
        return ResponseEntity.ok(added);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(@RequestBody MovieDTO updatedMovie, @PathVariable String movieTitle) {
        Movie movie = movieService.updateMovie(new Movie(updatedMovie.getTitle(), updatedMovie.getGenre(), updatedMovie.getDuration(), updatedMovie.getRating(), updatedMovie.getReleaseYear()), movieTitle);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.noContent().build();
    }

}
