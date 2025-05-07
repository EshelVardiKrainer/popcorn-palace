package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private TicketService ticketService;

    private Showtime showtime;
    private Ticket ticket;
    private UUID customerId;
    private Long showtimeId;
    private Integer seatNumber;

    @BeforeEach
    void setUp() {
        showtimeId = 1L;
        seatNumber = 5;
        customerId = UUID.randomUUID();

        Movie movie = new Movie("Test Movie", "Genre", 120, 9.0, 2024);
        movie.setId(1L);

        showtime = new Showtime();
        showtime.setId(showtimeId);
        showtime.setMovie(movie);
        showtime.setTheater("Theater A");
        showtime.setStart_time(Instant.now());
        showtime.setEnd_time(Instant.now().plusSeconds(7200));
        showtime.setPrice(12.0);

        ticket = new Ticket(showtime, seatNumber, customerId);
        // The booking_id is set in the Ticket constructor, so no need to set it here explicitly for the 'ticket' instance
        // If we needed to mock a ticket coming from repository with a specific booking_id, we would set it.
    }

    @Test
    void bookTicket_success() {
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(ticketRepository.findByShowtime(showtime)).thenReturn(Collections.emptyList()); // No existing tickets for this showtime
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket savedTicket = invocation.getArgument(0);
            // Simulate saving by assigning an ID if it's not set, though constructor handles booking_id
            if (savedTicket.getId() == null) {
                savedTicket.setId(1L); // Simulate DB generated ID
            }
            return savedTicket;
        });

        Ticket bookedTicket = ticketService.bookTicket(showtimeId, seatNumber, customerId);

        assertNotNull(bookedTicket);
        assertNotNull(bookedTicket.getBooking_id());
        assertEquals(showtimeId, bookedTicket.getShowtime().getId());
        assertEquals(seatNumber, bookedTicket.getSeat_number());
        assertEquals(customerId, bookedTicket.getCustomer_id());

        verify(showtimeRepository, times(1)).findById(showtimeId);
        verify(ticketRepository, times(1)).findByShowtime(showtime);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void bookTicket_showtimeNotFound() {
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> ticketService.bookTicket(showtimeId, seatNumber, customerId));

        assertEquals("Showtime not found with ID: " + showtimeId, exception.getMessage());
        verify(ticketRepository, never()).findByShowtime(any(Showtime.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void bookTicket_seatOccupied() {
        Ticket existingTicket = new Ticket(showtime, seatNumber, UUID.randomUUID()); // Another customer booked this seat

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(ticketRepository.findByShowtime(showtime)).thenReturn(List.of(existingTicket));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(showtimeId, seatNumber, customerId));

        assertEquals("Seat " + seatNumber + " is already occupied for showtime " + showtimeId, exception.getMessage());
        verify(ticketRepository, times(1)).findByShowtime(showtime);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void bookTicket_seatNumberNull() {
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        // No need to mock ticketRepository.findByShowtime as validation for seatNumber happens before

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(showtimeId, null, customerId));

        assertEquals("Seat number must be a positive number", exception.getMessage());
        verify(ticketRepository, never()).findByShowtime(any(Showtime.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void bookTicket_seatNumberZero() {
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(showtimeId, 0, customerId));

        assertEquals("Seat number must be a positive number", exception.getMessage());
        verify(ticketRepository, never()).findByShowtime(any(Showtime.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void bookTicket_seatNumberNegative() {
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(showtimeId, -1, customerId));

        assertEquals("Seat number must be a positive number", exception.getMessage());
        verify(ticketRepository, never()).findByShowtime(any(Showtime.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}