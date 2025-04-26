package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor 
public class TicketService {

    
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;

    
    public Ticket bookTicket(Long showtimeId, Integer seatNumber, UUID customerId) {

        // 1. Find the Showtime entity
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new EntityNotFoundException("Showtime not found with ID: " + showtimeId));

        // 2. Check if the seat number is valid and available for that specific showtime
        if (seatNumber == null || seatNumber <= 0) {
             throw new IllegalArgumentException("Seat number must be a positive number");
        }
        if (seatIsOccupied(showtime, seatNumber)) {
            throw new IllegalArgumentException("Seat " + seatNumber + " is already occupied for showtime " + showtimeId);
        }

        // 3. Create the new Ticket object using the constructor
        //    The constructor will generate the booking_id
        Ticket newTicket = new Ticket(showtime, seatNumber, customerId);

        // 4. Save the new ticket
        return ticketRepository.save(newTicket);
    }

    // Helper method to check seat availability for a given showtime
    private boolean seatIsOccupied(Showtime showtime, int seatNumber) {
        List<Ticket> tickets = ticketRepository.findByShowtime(showtime);
        // Check if any existing ticket for this showtime has the same seat number
        return tickets.stream().anyMatch(ticket -> ticket.getSeat_number() == seatNumber);
    }
}
