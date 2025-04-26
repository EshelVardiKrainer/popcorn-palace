package com.att.tdp.popcorn_palace.controler;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.att.tdp.popcorn_palace.mapper.MovieMapper;
import com.att.tdp.popcorn_palace.model.Movie;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor 
public class MovieController {
    @Autowired
    private final MovieService movieService;

    @Autowired
    private MovieMapper movieMapper;

    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        List<Movie> movies = movieService.fetchAllMovies();
        List<MovieDTO> movies_dto = new LinkedList<>();
        for (Movie movie : movies) {
            MovieDTO movieDTO = movieMapper.toDTO(movie);
            movies_dto.add(movieDTO);
        }
        return ResponseEntity.ok(movies_dto);
    
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@Valid
                                        @RequestBody MovieDTO movieDTO) {
        Movie added = movieMapper.fromDTO(movieDTO);
        added = movieService.addMovie(added);
        System.out.println("Movie object after mapping: " + added);
        return ResponseEntity.ok(added);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(@RequestBody MovieDTO updatedMovie, @PathVariable String movieTitle) {
        Movie movie = movieMapper.fromDTO(updatedMovie);
        movie = movieService.updateMovie(movie, movieTitle);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.noContent().build();
    }

}
