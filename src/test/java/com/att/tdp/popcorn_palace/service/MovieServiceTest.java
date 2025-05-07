package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.exceptions.DuplicateMovieTitleException;
import com.att.tdp.popcorn_palace.exceptions.MovieNotFoundException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        movie1 = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        movie1.setId(1L);

        movie2 = new Movie("Interstellar", "Sci-Fi", 169, 8.6, 2014);
        movie2.setId(2L);
    }

    @Test
    void addMovie_shouldSaveAndReturnMovie_whenTitleIsUnique() {
        when(movieRepository.findByTitle(movie1.getTitle())).thenReturn(null);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie1);

        Movie savedMovie = movieService.addMovie(movie1);

        assertNotNull(savedMovie);
        assertEquals(movie1.getTitle(), savedMovie.getTitle());
        verify(movieRepository, times(1)).findByTitle(movie1.getTitle());
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void addMovie_shouldThrowDuplicateMovieTitleException_whenTitleExists() {
        when(movieRepository.findByTitle(movie1.getTitle())).thenReturn(movie1);

        assertThrows(DuplicateMovieTitleException.class, () -> movieService.addMovie(movie1));
        verify(movieRepository, times(1)).findByTitle(movie1.getTitle());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void fetchAllMovies_shouldReturnListOfMovies() {
        List<Movie> movies = Arrays.asList(movie1, movie2);
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.fetchAllMovies();

        assertEquals(2, result.size());
        assertEquals(movie1.getTitle(), result.get(0).getTitle());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void fetchAllMovies_shouldReturnEmptyList_whenNoMoviesExist() {
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        List<Movie> result = movieService.fetchAllMovies();

        assertTrue(result.isEmpty());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void deleteMovie_shouldReturnTrue_whenMovieExists() {
        when(movieRepository.findByTitle(movie1.getTitle())).thenReturn(movie1);
        doNothing().when(movieRepository).deleteById(movie1.getId());

        boolean result = movieService.deleteMovie(movie1.getTitle());

        assertTrue(result);
        verify(movieRepository, times(1)).findByTitle(movie1.getTitle());
        verify(movieRepository, times(1)).deleteById(movie1.getId());
    }

    @Test
    void deleteMovie_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        when(movieRepository.findByTitle(anyString())).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie("NonExistentTitle"));
        verify(movieRepository, times(1)).findByTitle("NonExistentTitle");
        verify(movieRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateMovie_shouldUpdateAndReturnMovie_whenMovieExistsAndNewTitleIsUnique() {
        Movie updatedInfo = new Movie("Inception Updated", "Sci-Fi Thriller", 150, 9.0, 2010);
        updatedInfo.setId(1L); // ID must match for BeanUtils.copyProperties to work as expected in service

        // Mock finding the original movie
        when(movieRepository.findByTitle(movie1.getTitle())).thenReturn(movie1);
        // Mock finding no movie with the new title (if title is changed)
        when(movieRepository.findByTitle(updatedInfo.getTitle())).thenReturn(null);
        // Mock saving the updated movie
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movieToSave = invocation.getArgument(0);
            // Simulate what BeanUtils.copyProperties does
            BeanUtils.copyProperties(updatedInfo, movieToSave, "id");
            return movieToSave;
        });


        Movie result = movieService.updateMovie(updatedInfo, movie1.getTitle());

        assertNotNull(result);
        assertEquals(updatedInfo.getTitle(), result.getTitle());
        assertEquals(updatedInfo.getGenre(), result.getGenre());
        verify(movieRepository, times(1)).findByTitle(movie1.getTitle());
        verify(movieRepository, times(1)).findByTitle(updatedInfo.getTitle());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }
    
    @Test
    void updateMovie_shouldUpdateAndReturnMovie_whenMovieExistsAndTitleIsUnchanged() {
        Movie updatedInfo = new Movie(movie1.getTitle(), "Sci-Fi Thriller", 150, 9.0, 2010); // Title is the same
        updatedInfo.setId(movie1.getId());

        when(movieRepository.findByTitle(movie1.getTitle())).thenReturn(movie1);
        // No need to mock findByTitle for the updated title if it's the same
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movieToSave = invocation.getArgument(0);
            BeanUtils.copyProperties(updatedInfo, movieToSave, "id"); // Simulate save behavior
            return movieToSave;
        });

        Movie result = movieService.updateMovie(updatedInfo, movie1.getTitle());

        assertNotNull(result);
        assertEquals(updatedInfo.getTitle(), result.getTitle());
        assertEquals(updatedInfo.getGenre(), result.getGenre());
        verify(movieRepository, times(1)).findByTitle(movie1.getTitle());
        // findByTitle for the updated title should not be called if the title hasn't changed
        verify(movieRepository, never()).findByTitle(updatedInfo.getTitle());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }


    @Test
    void updateMovie_shouldThrowMovieNotFoundException_whenMovieToUpdateDoesNotExist() {
        Movie updatedInfo = new Movie("NonExistent Updated", "Sci-Fi", 150, 9.0, 2010);
        when(movieRepository.findByTitle("NonExistent")).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(updatedInfo, "NonExistent"));
        verify(movieRepository, times(1)).findByTitle("NonExistent");
        verify(movieRepository, never()).findByTitle(updatedInfo.getTitle());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void updateMovie_shouldThrowDuplicateMovieTitleException_whenNewTitleAlreadyExists() {
        Movie updatedInfo = new Movie(movie2.getTitle(), "Action", 120, 7.0, 2020); // Attempting to update movie1's title to movie2's title
        updatedInfo.setId(movie1.getId());

        when(movieRepository.findByTitle(movie1.getTitle())).thenReturn(movie1); // Original movie found
        when(movieRepository.findByTitle(movie2.getTitle())).thenReturn(movie2); // New title already exists

        assertThrows(DuplicateMovieTitleException.class, () -> movieService.updateMovie(updatedInfo, movie1.getTitle()));
        verify(movieRepository, times(1)).findByTitle(movie1.getTitle());
        verify(movieRepository, times(1)).findByTitle(movie2.getTitle());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void getMovieById_shouldReturnMovie_whenFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        Movie found = movieService.getMovieById(1L);
        assertNotNull(found);
        assertEquals("Inception", found.getTitle());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieById_shouldReturnNull_whenNotFound() {
        when(movieRepository.findById(3L)).thenReturn(Optional.empty());
        Movie found = movieService.getMovieById(3L);
        assertNull(found);
        verify(movieRepository, times(1)).findById(3L);
    }
}
