package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.controler.MovieController;
import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.exceptions.DuplicateMovieTitleException;
import com.att.tdp.popcorn_palace.exceptions.MovieNotFoundException;
import com.att.tdp.popcorn_palace.mapper.MovieMapper;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private MovieMapper movieMapper;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON strings

    private Movie movie1;
    private MovieDTO movieDTO1;
    private Movie movie2;
    private MovieDTO movieDTO2;

    @BeforeEach
    void setUp() {
        movie1 = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        movie1.setId(1L);
        movieDTO1 = new MovieDTO(1L, "Inception", "Sci-Fi", 148, 8.8, 2010);

        movie2 = new Movie("Interstellar", "Sci-Fi", 169, 8.6, 2014);
        movie2.setId(2L);
        movieDTO2 = new MovieDTO(2L, "Interstellar", "Sci-Fi", 169, 8.6, 2014);
    }

    @Test
    void getAllMovies_shouldReturnListOfMovieDTOs() throws Exception {
        List<Movie> movies = Arrays.asList(movie1, movie2);
        List<MovieDTO> movieDTOs = Arrays.asList(movieDTO1, movieDTO2);

        given(movieService.fetchAllMovies()).willReturn(movies);
        given(movieMapper.toDTO(movie1)).willReturn(movieDTO1);
        given(movieMapper.toDTO(movie2)).willReturn(movieDTO2);

        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(movie1.getTitle())))
                .andExpect(jsonPath("$[1].title", is(movie2.getTitle())));
    }

    @Test
    void getAllMovies_shouldReturnEmptyListWhenNoMovies() throws Exception {
        given(movieService.fetchAllMovies()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addMovie_shouldReturnAddedMovie() throws Exception {
        Movie movieToAdd = new Movie("Tenet", "Action", 150, 7.4, 2020);
        MovieDTO movieDTOToAdd = new MovieDTO(null, "Tenet", "Action", 150, 7.4, 2020);
        Movie savedMovie = new Movie("Tenet", "Action", 150, 7.4, 2020);
        savedMovie.setId(3L);

        given(movieMapper.fromDTO(any(MovieDTO.class))).willReturn(movieToAdd);
        given(movieService.addMovie(any(Movie.class))).willReturn(savedMovie);

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDTOToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedMovie.getId().intValue())))
                .andExpect(jsonPath("$.title", is(savedMovie.getTitle())));
    }

    @Test
    void updateMovie_shouldReturnUpdatedMovie() throws Exception {
        String movieTitleToUpdate = "Inception"; // Original title used to find the movie
        // DTO for the update request - ID is present, title and other fields are updated
        MovieDTO movieUpdateDTO = new MovieDTO(1L, "Inception Updated", "Sci-Fi Thriller", 150, 9.0, 2010);
        
        // This is what the mapper will convert the DTO to, to pass to the service
        Movie movieFromDTO = new Movie("Inception Updated", "Sci-Fi Thriller", 150, 9.0, 2010);
        movieFromDTO.setId(1L); // ID should be set as it's an update

        // This is the movie object the service is expected to return after a successful update
        Movie updatedMovieFromService = new Movie("Inception Updated", "Sci-Fi Thriller", 150, 9.0, 2010);
        updatedMovieFromService.setId(1L); // ID remains the same

        given(movieMapper.fromDTO(any(MovieDTO.class))).willReturn(movieFromDTO);
        given(movieService.updateMovie(any(Movie.class), anyString())).willReturn(updatedMovieFromService);

        mockMvc.perform(post("/movies/update/{movieTitle}", movieTitleToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedMovieFromService.getId().intValue())))
                .andExpect(jsonPath("$.title", is(updatedMovieFromService.getTitle())))
                .andExpect(jsonPath("$.genre", is(updatedMovieFromService.getGenre())))
                .andExpect(jsonPath("$.duration", is(updatedMovieFromService.getDuration())))
                .andExpect(jsonPath("$.rating", is(updatedMovieFromService.getRating())))
                .andExpect(jsonPath("$.release_year", is(updatedMovieFromService.getRelease_year())));
    }

    @Test
    void deleteMovie_shouldReturnNoContent() throws Exception {
        String movieTitleToDelete = "Inception";
        given(movieService.deleteMovie(movieTitleToDelete)).willReturn(true);

        mockMvc.perform(delete("/movies/{movieTitle}", movieTitleToDelete))
                .andExpect(status().isNoContent());
    }

    @Test
    void addMovie_shouldReturnConflictWhenTitleExists() throws Exception {
        MovieDTO movieDTOToAdd = new MovieDTO(null, "Tenet", "Action", 150, 7.4, 2020);
        Movie movieToAdd = new Movie("Tenet", "Action", 150, 7.4, 2020);

        given(movieMapper.fromDTO(any(MovieDTO.class))).willReturn(movieToAdd);
        given(movieService.addMovie(any(Movie.class))).willThrow(new DuplicateMovieTitleException("Tenet"));

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDTOToAdd)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateMovie_shouldReturnNotFoundWhenMovieDoesNotExist() throws Exception {
        String movieTitleToUpdate = "NonExistent";
        MovieDTO movieUpdateDTO = new MovieDTO(1L, "NonExistent Updated", "Sci-Fi", 150, 9.0, 2010);
        Movie movieFromDTO = new Movie("NonExistent Updated", "Sci-Fi", 150, 9.0, 2010);

        given(movieMapper.fromDTO(any(MovieDTO.class))).willReturn(movieFromDTO);
        given(movieService.updateMovie(any(Movie.class), anyString())).willThrow(new MovieNotFoundException(movieTitleToUpdate));

        mockMvc.perform(post("/movies/update/{movieTitle}", movieTitleToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_shouldReturnConflictWhenNewTitleExists() throws Exception {
        String movieTitleToUpdate = "Inception";
        MovieDTO movieUpdateDTO = new MovieDTO(1L, "Interstellar", "Sci-Fi", 150, 9.0, 2010); // Attempting to update to existing title "Interstellar"
        Movie movieFromDTO = new Movie("Interstellar", "Sci-Fi", 150, 9.0, 2010);
        movieFromDTO.setId(1L);

        given(movieMapper.fromDTO(any(MovieDTO.class))).willReturn(movieFromDTO);
        given(movieService.updateMovie(any(Movie.class), anyString())).willThrow(new DuplicateMovieTitleException("Interstellar"));

        mockMvc.perform(post("/movies/update/{movieTitle}", movieTitleToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieUpdateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteMovie_shouldReturnNotFoundWhenMovieDoesNotExist() throws Exception {
        String movieTitleToDelete = "NonExistent";
        given(movieService.deleteMovie(movieTitleToDelete)).willThrow(new MovieNotFoundException(movieTitleToDelete));

        mockMvc.perform(delete("/movies/{movieTitle}", movieTitleToDelete))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMovie_shouldReturnBadRequestForInvalidInput() throws Exception {
        MovieDTO invalidMovieDTO = new MovieDTO(null, null, "Action", 150, 7.4, 2020); // Title is null

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMovieDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMovie_shouldReturnBadRequestForInvalidInput() throws Exception {
        String movieTitleToUpdate = "Inception";
        MovieDTO invalidMovieDTO = new MovieDTO(1L, "Inception Updated", null, 150, 9.0, 2010); // Genre is null

        mockMvc.perform(post("/movies/update/{movieTitle}", movieTitleToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMovieDTO)))
                .andExpect(status().isBadRequest());
    }
}
