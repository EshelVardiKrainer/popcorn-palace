package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TicketRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired 
    private MovieRepository movieRepository; 

    private Movie persistedMovie; 

    @BeforeEach 
    void setUp() { 
        // Ensure a movie exists for showtimes
        Movie movie = new Movie("Test Movie for Tickets", "Test Genre", 120, 7.5, 2023);
        persistedMovie = entityManager.persistAndFlush(movie);
    }


    private Showtime createShowtime() {
        Showtime showtime = new Showtime();
        showtime.setMovie(persistedMovie); // Use persisted movie
        showtime.setTheater("Theater 1");
        showtime.setStart_time(Instant.now());
        showtime.setEnd_time(Instant.now().plusSeconds(7200));
        showtime.setPrice(10.0); // Price is on Showtime, not Ticket
        return entityManager.persistAndFlush(showtime);
    }

    @Test
    void whenSaveTicket_thenTicketIsPersisted() {
        Showtime showtime = createShowtime();
        UUID customerId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeat_number(1); 
        ticket.setCustomer_id(customerId);
        ticket.setBooking_id(UUID.randomUUID()); // booking_id is set in constructor, but can be set for test consistency if needed

        Ticket savedTicket = ticketRepository.save(ticket);

        assertThat(savedTicket).isNotNull();
        assertThat(savedTicket.getId()).isNotNull();
        assertThat(savedTicket.getBooking_id()).isNotNull(); // Assert booking_id
        assertEquals(customerId, savedTicket.getCustomer_id()); // Assert customer_id
        assertEquals(showtime.getId(), savedTicket.getShowtime().getId());
        assertEquals(1, savedTicket.getSeat_number()); // Assert seat_number (Integer)
    }

    @Test
    void whenFindById_thenReturnTicket() {
        Showtime showtime = createShowtime();
        UUID customerId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeat_number(2);
        ticket.setCustomer_id(customerId);
        ticket.setBooking_id(UUID.randomUUID());
        entityManager.persistAndFlush(ticket);

        Optional<Ticket> foundTicket = ticketRepository.findById(ticket.getId());

        assertThat(foundTicket).isPresent();
        assertEquals(ticket.getId(), foundTicket.get().getId());
        assertEquals(Integer.valueOf(2), foundTicket.get().getSeat_number()); // Check integer seat number
    }

    @Test
    void whenFindById_withNonExistentId_thenReturnEmpty() {
        Optional<Ticket> foundTicket = ticketRepository.findById(999L);
        assertThat(foundTicket).isNotPresent();
    }

    @Test
    void whenFindAll_thenReturnAllTickets() {
        Showtime showtime1 = createShowtime();
        UUID customerId1 = UUID.randomUUID();
        Ticket ticket1 = new Ticket();
        // ticket1.setUser(user1); // Removed
        ticket1.setShowtime(showtime1);
        ticket1.setSeat_number(3); // Changed to Integer
        ticket1.setCustomer_id(customerId1); // Added
        // ticket1.setPrice(showtime1.getPrice()); // Removed
        // ticket1.setPurchaseDate(Instant.now()); // Removed
        ticket1.setBooking_id(UUID.randomUUID());
        entityManager.persistAndFlush(ticket1);

        Showtime showtime2 = createShowtime(); // Can use the same showtime or a different one
        UUID customerId2 = UUID.randomUUID();
        Ticket ticket2 = new Ticket();
        ticket2.setShowtime(showtime2);
        ticket2.setSeat_number(4); 
        ticket2.setCustomer_id(customerId2); 
        ticket2.setBooking_id(UUID.randomUUID());
        entityManager.persistAndFlush(ticket2);

        List<Ticket> tickets = ticketRepository.findAll();

        assertThat(tickets).hasSize(2).extracting(Ticket::getId).contains(ticket1.getId(), ticket2.getId());
    }

    @Test
    void whenDeleteById_thenTicketIsRemoved() {
        Showtime showtime = createShowtime();
        UUID customerId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeat_number(5); 
        ticket.setCustomer_id(customerId); 
        ticket.setBooking_id(UUID.randomUUID());
        entityManager.persistAndFlush(ticket);

        ticketRepository.deleteById(ticket.getId());
        entityManager.flush();

        Optional<Ticket> deletedTicket = ticketRepository.findById(ticket.getId());
        assertThat(deletedTicket).isNotPresent();
    }

    @Test
    void whenFindByShowtime_thenReturnTicketsForShowtime() { 
        Showtime showtime1 = createShowtime();
        Showtime showtime2 = createShowtime(); // A different showtime

        Ticket ticket1_s1 = new Ticket(showtime1, 10, UUID.randomUUID());
        entityManager.persistAndFlush(ticket1_s1);

        Ticket ticket2_s1 = new Ticket(showtime1, 11, UUID.randomUUID());
        entityManager.persistAndFlush(ticket2_s1);

        Ticket ticket1_s2 = new Ticket(showtime2, 10, UUID.randomUUID()); // Same seat number, different showtime
        entityManager.persistAndFlush(ticket1_s2);

        List<Ticket> ticketsForShowtime1 = ticketRepository.findByShowtime(showtime1);
        assertThat(ticketsForShowtime1).hasSize(2);
        assertThat(ticketsForShowtime1).extracting(Ticket::getId).containsExactlyInAnyOrder(ticket1_s1.getId(), ticket2_s1.getId());

        List<Ticket> ticketsForShowtime2 = ticketRepository.findByShowtime(showtime2);
        assertThat(ticketsForShowtime2).hasSize(1);
        assertThat(ticketsForShowtime2).extracting(Ticket::getId).containsExactly(ticket1_s2.getId());

        // Test for a showtime that exists but has no tickets
        Showtime showtimeWithNoTickets = createShowtime(); // This creates and persists a new showtime

        List<Ticket> ticketsForShowtimeWithNoTickets = ticketRepository.findByShowtime(showtimeWithNoTickets);
        assertThat(ticketsForShowtimeWithNoTickets).isEmpty();
    }

}
