package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    @DisplayName("save() should persist a movie with all properties and assign an ID")
    void testSaveMovie_Success() {
        Movie movie = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        Movie savedMovie = movieRepository.save(movie);

        assertNotNull(savedMovie.getId(), "ID should be generated upon save");
        assertEquals("Inception", savedMovie.getTitle());
        assertEquals("Sci-Fi", savedMovie.getGenre());
        assertEquals(148, savedMovie.getDuration());
        assertEquals(8.8, savedMovie.getRating());
        assertEquals(2010, savedMovie.getRelease_year());

        Movie foundInDb = entityManager.find(Movie.class, savedMovie.getId());
        assertNotNull(foundInDb);
        assertEquals(savedMovie.getTitle(), foundInDb.getTitle());
    }

    @Test
    @DisplayName("findById() should return the correct movie when it exists")
    void testFindById_Exists() {
        Movie movie = new Movie("Interstellar", "Sci-Fi", 169, 8.6, 2014);
        Movie savedMovie = entityManager.persistFlushFind(movie); // Persist and get managed instance

        Optional<Movie> found = movieRepository.findById(savedMovie.getId());

        assertTrue(found.isPresent(), "Movie should be found");
        assertEquals(savedMovie.getId(), found.get().getId());
        assertEquals("Interstellar", found.get().getTitle());
    }

    @Test
    @DisplayName("findById() should return empty Optional when movie does not exist")
    void testFindById_NotExists() {
        Optional<Movie> found = movieRepository.findById(999L); // Non-existent ID
        assertFalse(found.isPresent(), "Movie should not be found");
    }

    @Test
    @DisplayName("findAll() should return all persisted movies")
    void testFindAll_WithMovies() {
        entityManager.persist(new Movie("Movie1", "Action", 120, 7.5, 2020));
        entityManager.persist(new Movie("Movie2", "Comedy", 90, 6.5, 2021));
        entityManager.flush();

        List<Movie> movies = movieRepository.findAll();
        assertEquals(2, movies.size(), "Should find 2 movies");
    }

    @Test
    @DisplayName("findAll() should return an empty list when no movies exist")
    void testFindAll_Empty() {
        List<Movie> movies = movieRepository.findAll();
        assertTrue(movies.isEmpty(), "List should be empty when no movies are saved");
    }
    
    @Test
    @DisplayName("findByTitle() should return the correct movie when title exists")
    void testFindByTitle_Exists() {
        Movie movie = new Movie("Tenet", "Action", 150, 7.8, 2020);
        entityManager.persistFlushFind(movie);

        Movie found = movieRepository.findByTitle("Tenet");

        assertNotNull(found, "Movie should be found by title");
        assertEquals("Tenet", found.getTitle());
    }

    @Test
    @DisplayName("findByTitle() should return null when title does not exist")
    void testFindByTitle_NotExists() {
        Movie found = movieRepository.findByTitle("NonExistentTitle");
        assertNull(found, "Movie should not be found for a non-existent title");
    }

    @Test
    @DisplayName("update() should modify existing movie properties")
    void testUpdateMovie() {
        Movie movie = new Movie("Dunkirk", "War", 106, 7.9, 2017);
        Movie savedMovie = entityManager.persistFlushFind(movie);

        // Detach and modify, or find and modify
        Movie movieToUpdate = movieRepository.findById(savedMovie.getId()).orElseThrow();
        movieToUpdate.setTitle("Dunkirk - Director's Cut");
        movieToUpdate.setRating(8.1);
        Movie updatedMovie = movieRepository.save(movieToUpdate); // save acts as update for managed entities

        assertNotNull(updatedMovie);
        assertEquals(savedMovie.getId(), updatedMovie.getId());
        assertEquals("Dunkirk - Director's Cut", updatedMovie.getTitle());
        assertEquals(8.1, updatedMovie.getRating());
        assertEquals("War", updatedMovie.getGenre()); // Unchanged property
    }


    @Test
    @DisplayName("deleteById() should remove the movie from the database")
    void testDeleteById_Exists() {
        Movie movie = new Movie("Oppenheimer", "Biography", 180, 8.5, 2023);
        Movie savedMovie = entityManager.persistFlushFind(movie);
        Long id = savedMovie.getId();

        movieRepository.deleteById(id);
        entityManager.flush(); // Ensure delete is processed

        Optional<Movie> found = movieRepository.findById(id);
        assertFalse(found.isPresent(), "Movie should be deleted");
        assertNull(entityManager.find(Movie.class, id), "Movie should not be found by EntityManager after delete");
    }

    @Test
    @DisplayName("deleteById() should not throw error for non-existent movie")
    void testDeleteById_NotExists() {
        assertDoesNotThrow(() -> {
            movieRepository.deleteById(999L); // Non-existent ID
            entityManager.flush();
        }, "Deleting a non-existent movie should not throw an error");
    }

    @Test
    @DisplayName("save() should throw DataIntegrityViolationException for duplicate titles")
    void testSave_DuplicateTitle_ThrowsException() {
        Movie movie1 = new Movie("UniqueTitle", "Drama", 100, 7.0, 2022);
        movieRepository.save(movie1);
        entityManager.flush(); // Ensure the first movie is persisted and constraint is active

        Movie movie2_duplicateTitle = new Movie("UniqueTitle", "Sci-Fi", 110, 7.1, 2023);
        
        // Using assertThrows to catch the expected exception
        assertThrows(DataIntegrityViolationException.class, () -> {
            movieRepository.save(movie2_duplicateTitle);
            entityManager.flush(); // Attempt to flush to trigger DB constraint
        });
        
        // Optionally, assert something about the exception message if it's consistent
        // assertTrue(exception.getMessage().contains("ConstraintViolationException"));
        // Or check for root cause if available and specific
    }
}
