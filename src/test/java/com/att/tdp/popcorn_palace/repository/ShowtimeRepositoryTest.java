package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShowtimeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired 
    private TicketRepository ticketRepository;

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        // Clear repositories before each test to ensure a clean state
        ticketRepository.deleteAll();
        showtimeRepository.deleteAll();
        movieRepository.deleteAll();
        entityManager.flush(); // Ensure deletions are processed

        // Persist some movies to be used in tests
        movie1 = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        movie2 = new Movie("Interstellar", "Sci-Fi", 169, 8.6, 2014);
        movie1 = entityManager.persistAndFlush(movie1);
        movie2 = entityManager.persistAndFlush(movie2);
    }

    private Showtime createTestShowtime(Movie movie, String theater, Instant startTime, Instant endTime, Double price) {
        return new Showtime(movie, theater, startTime, endTime, price);
    }

    @Test
    @DisplayName("save() should persist a showtime with all properties and assign an ID")
    void testSaveShowtime_Success() {
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, "Theater A", startTime, endTime, 12.50);

        Showtime savedShowtime = showtimeRepository.save(showtime);

        assertNotNull(savedShowtime.getId(), "ID should be generated upon save");
        assertEquals(movie1.getId(), savedShowtime.getMovie().getId());
        assertEquals("Theater A", savedShowtime.getTheater());
        assertEquals(startTime, savedShowtime.getStart_time());
        assertEquals(endTime, savedShowtime.getEnd_time());
        assertEquals(12.50, savedShowtime.getPrice());

        Showtime foundInDb = entityManager.find(Showtime.class, savedShowtime.getId());
        assertNotNull(foundInDb);
        assertEquals(savedShowtime.getMovie().getTitle(), foundInDb.getMovie().getTitle());
    }

    @Test
    @DisplayName("findById() should return the correct showtime when it exists")
    void testFindById_Exists() {
        Instant startTime = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(3, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, "Theater B", startTime, endTime, 15.00);
        Showtime savedShowtime = entityManager.persistFlushFind(showtime);

        Optional<Showtime> found = showtimeRepository.findById(savedShowtime.getId());

        assertTrue(found.isPresent(), "Showtime should be found");
        assertEquals(savedShowtime.getId(), found.get().getId());
        assertEquals("Theater B", found.get().getTheater());
    }

    @Test
    @DisplayName("findById() should return empty Optional when showtime does not exist")
    void testFindById_NotExists() {
        Optional<Showtime> found = showtimeRepository.findById(999L); // Non-existent ID
        assertFalse(found.isPresent(), "Showtime should not be found");
    }

    @Test
    @DisplayName("findAll() should return all persisted showtimes")
    void testFindAll_WithShowtimes() {
        Instant startTime1 = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endTime1 = startTime1.plus(2, ChronoUnit.HOURS);
        entityManager.persist(createTestShowtime(movie1, "Theater C", startTime1, endTime1, 10.00));

        Instant startTime2 = Instant.now().plus(1, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS);
        Instant endTime2 = startTime2.plus(2, ChronoUnit.HOURS);
        entityManager.persist(createTestShowtime(movie2, "Theater C", startTime2, endTime2, 11.00));
        entityManager.flush();

        List<Showtime> showtimes = showtimeRepository.findAll();
        assertEquals(2, showtimes.size(), "Should find 2 showtimes");
    }

    @Test
    @DisplayName("findAll() should return an empty list when no showtimes exist")
    void testFindAll_Empty() {
        List<Showtime> showtimes = showtimeRepository.findAll();
        assertTrue(showtimes.isEmpty(), "List should be empty when no showtimes are saved");
    }

    @Test
    @DisplayName("findByTheater() should return showtimes for the specified theater")
    void testFindByTheater_Exists() {
        Instant startTime1 = Instant.now().plus(3, ChronoUnit.DAYS);
        Instant endTime1 = startTime1.plus(2, ChronoUnit.HOURS);
        entityManager.persist(createTestShowtime(movie1, "AMC Central", startTime1, endTime1, 12.00));

        Instant startTime2 = Instant.now().plus(3, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS);
        Instant endTime2 = startTime2.plus(2, ChronoUnit.HOURS);
        entityManager.persist(createTestShowtime(movie2, "AMC Central", startTime2, endTime2, 12.00));

        Instant startTime3 = Instant.now().plus(3, ChronoUnit.DAYS);
        Instant endTime3 = startTime3.plus(2, ChronoUnit.HOURS);
        entityManager.persist(createTestShowtime(movie1, "Regal Downtown", startTime3, endTime3, 13.00));
        entityManager.flush();

        List<Showtime> amcShowtimes = showtimeRepository.findByTheater("AMC Central");
        assertEquals(2, amcShowtimes.size(), "Should find 2 showtimes for AMC Central");
        assertTrue(amcShowtimes.stream().allMatch(s -> s.getTheater().equals("AMC Central")));
    }

    @Test
    @DisplayName("findByTheater() should return an empty list when no showtimes exist for the theater")
    void testFindByTheater_NotExists() {
        List<Showtime> showtimes = showtimeRepository.findByTheater("NonExistent Theater");
        assertTrue(showtimes.isEmpty(), "Should not find showtimes for a non-existent theater");
    }

    @Test
    @DisplayName("update() should modify existing showtime properties")
    void testUpdateShowtime() {
        Instant startTime = Instant.now().plus(4, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, "Theater D", startTime, endTime, 14.00);
        Showtime savedShowtime = entityManager.persistFlushFind(showtime);

        Showtime showtimeToUpdate = showtimeRepository.findById(savedShowtime.getId()).orElseThrow();
        showtimeToUpdate.setTheater("Theater D Updated");
        showtimeToUpdate.setPrice(14.50);
        Showtime updatedShowtime = showtimeRepository.save(showtimeToUpdate);

        assertNotNull(updatedShowtime);
        assertEquals(savedShowtime.getId(), updatedShowtime.getId());
        assertEquals("Theater D Updated", updatedShowtime.getTheater());
        assertEquals(14.50, updatedShowtime.getPrice());
        assertEquals(movie1.getId(), updatedShowtime.getMovie().getId()); // Unchanged property
    }

    @Test
    @DisplayName("deleteById() should remove the showtime from the database")
    void testDeleteById_Exists() {
        Instant startTime = Instant.now().plus(5, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, "Theater E", startTime, endTime, 16.00);
        Showtime savedShowtime = entityManager.persistFlushFind(showtime);
        Long id = savedShowtime.getId();

        showtimeRepository.deleteById(id);
        entityManager.flush();

        Optional<Showtime> found = showtimeRepository.findById(id);
        assertFalse(found.isPresent(), "Showtime should be deleted");
        assertNull(entityManager.find(Showtime.class, id), "Showtime should not be found by EntityManager after delete");
    }

    @Test
    @DisplayName("deleteById() should not throw error for non-existent showtime")
    void testDeleteById_NotExists() {
        assertDoesNotThrow(() -> {
            showtimeRepository.deleteById(999L); // Non-existent ID
            entityManager.flush();
        }, "Deleting a non-existent showtime should not throw an error");
    }

    @Test
    @DisplayName("save() should throw ConstraintViolationException for null movie_id")
    void testSave_NullMovie_ThrowsException() {
        Instant startTime = Instant.now().plus(6, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(null, "Theater F", startTime, endTime, 10.00); // Movie is null

        assertThrows(ConstraintViolationException.class, () -> { 
            showtimeRepository.save(showtime);
            entityManager.flush(); // Attempt to flush to trigger DB constraint
        }, "Saving showtime with null movie should throw ConstraintViolationException");
    }
    
    @Test
    @DisplayName("save() should throw DataIntegrityViolationException for non-existent movie_id") // Updated DisplayName
    void testSave_NonExistentMovie_ThrowsException() {
        Instant startTime = Instant.now().plus(6, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        
        Movie nonExistentMovie = new Movie(); // Not persisted
        nonExistentMovie.setId(9999L); // Set a non-existent ID
        
        Showtime showtime = new Showtime();
        showtime.setMovie(nonExistentMovie); // Set the non-existent movie
        showtime.setTheater("Theater G");
        showtime.setStart_time(startTime);
        showtime.setEnd_time(endTime);
        showtime.setPrice(10.00);

        assertThrows(DataIntegrityViolationException.class, () -> { // Changed to DataIntegrityViolationException
            showtimeRepository.save(showtime);
            entityManager.flush(); 
        }, "Saving showtime with a non-existent movie ID should throw DataIntegrityViolationException due to foreign key constraint."); // Updated assertion message
    }


    @Test
    @DisplayName("save() should throw ConstraintViolationException for null theater")
    void testSave_NullTheater_ThrowsException() {
        Instant startTime = Instant.now().plus(7, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, null, startTime, endTime, 10.00);

        assertThrows(ConstraintViolationException.class, () -> {
            showtimeRepository.save(showtime);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("save() should throw ConstraintViolationException for null start_time")
    void testSave_NullStartTime_ThrowsException() {
        Instant endTime = Instant.now().plus(8, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, "Theater H", null, endTime, 10.00);

        assertThrows(ConstraintViolationException.class, () -> {
            showtimeRepository.save(showtime);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("save() should throw ConstraintViolationException for null end_time")
    void testSave_NullEndTime_ThrowsException() {
        Instant startTime = Instant.now().plus(9, ChronoUnit.DAYS);
        Showtime showtime = createTestShowtime(movie1, "Theater I", startTime, null, 10.00);

        assertThrows(ConstraintViolationException.class, () -> {
            showtimeRepository.save(showtime);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("save() should throw ConstraintViolationException for null price")
    void testSave_NullPrice_ThrowsException() {
        Instant startTime = Instant.now().plus(10, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, "Theater J", startTime, endTime, null);

        assertThrows(ConstraintViolationException.class, () -> {
            showtimeRepository.save(showtime);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("deleteMovie() should cascade delete to associated Showtimes")
    void testDeleteMovie_CascadesToShowtimes() {
        Instant startTime = Instant.now().plus(11, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtimeAssociated = createTestShowtime(movie1, "Theater K", startTime, endTime, 20.00);
        entityManager.persistAndFlush(showtimeAssociated);

        Long showtimeId = showtimeAssociated.getId();
        Long movieIdToDelete = movie1.getId();

        // Verify showtime exists before movie deletion
        assertTrue(showtimeRepository.findById(showtimeId).isPresent(), "Showtime should exist before movie deletion");

        // Delete the movie
        Movie movieToDelete = entityManager.find(Movie.class, movieIdToDelete);
        assertNotNull(movieToDelete, "Movie to delete should exist");
        
        entityManager.remove(movieToDelete); 
        entityManager.flush(); 
        entityManager.clear(); // Added to clear persistence context

        // Verify movie is deleted
        assertNull(entityManager.find(Movie.class, movieIdToDelete), "Movie should be deleted");

        // Verify associated showtime is also deleted due to ON DELETE CASCADE
        assertFalse(showtimeRepository.findById(showtimeId).isPresent(), "Showtime should be deleted due to cascade");
    }

    @Test
    @DisplayName("deleteShowtime() should cascade delete to associated Tickets")
    void testDeleteShowtime_CascadesToTickets() {
        // 1. Setup: Movie (from setUp), Showtime, Ticket
        Instant startTime = Instant.now().plus(12, ChronoUnit.DAYS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);
        Showtime showtime = createTestShowtime(movie1, "Theater Cascade", startTime, endTime, 25.00);
        Showtime savedShowtime = entityManager.persistFlushFind(showtime);
        Long showtimeIdToDelete = savedShowtime.getId();

        Ticket ticket = new Ticket(savedShowtime, 10, UUID.randomUUID());
        // Persist ticket using entityManager to ensure it's managed and gets an ID
        Ticket savedTicket = entityManager.persistFlushFind(ticket);
        Long ticketId = savedTicket.getId();

        // Verify ticket exists before showtime deletion
        assertTrue(ticketRepository.findById(ticketId).isPresent(), "Ticket should exist before showtime deletion");
        assertNotNull(entityManager.find(Ticket.class, ticketId), "Ticket should be findable by EntityManager before clear");

        // 2. Delete the Showtime
        Showtime showtimeToDelete = entityManager.find(Showtime.class, showtimeIdToDelete);
        assertNotNull(showtimeToDelete, "Showtime to delete should exist");

        entityManager.remove(showtimeToDelete);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to check DB state

        // 3. Verify Showtime is deleted
        assertNull(entityManager.find(Showtime.class, showtimeIdToDelete), "Showtime should be deleted (checked via new find)");
        assertFalse(showtimeRepository.findById(showtimeIdToDelete).isPresent(), "Showtime should be deleted (checked via repository)");

        // 4. Verify associated Ticket is also deleted due to ON DELETE CASCADE
        assertNull(entityManager.find(Ticket.class, ticketId), "Ticket should be deleted (checked via new find after clear)");
        assertFalse(ticketRepository.findById(ticketId).isPresent(), "Ticket should be deleted due to cascade (checked via repository)");
    }
}
