package com.att.tdp.popcorn_palace.service;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.model.Showtime;
import java.util.List;

@Service
public class TicketService {
    private TicketRepository ticketRepository;
    private ShowtimeRepository showtimeRepository;

    public Ticket bookTicket(Ticket ticket) {
        // Check if the ticketId is valid
        if (ticketRepository.existsById(ticket.getId())) {
            throw new IllegalArgumentException("Ticket ID already exists");
        }
        // Check if the showtime exists
        if (showtimeRepository.findById(ticket.getShowtime().getId()).isEmpty()) {
            throw new IllegalArgumentException("Showtime is not found");
        }
        // Check if the seat number is valid
        if (ticket.getSeat_number() <= 0 || seatIsOccupied(ticket.getShowtime(), ticket.getSeat_number())) {
            throw new IllegalArgumentException("Seat number must be greater than 0");
        }

        return ticketRepository.save(ticket);
    }

    private boolean seatIsOccupied(Showtime showtime, int seat_number) {
        List <Ticket> tickets = ticketRepository.findByShowtime(showtime);
        for (Ticket ticket : tickets) {
            if (ticket.getSeat_number() == seat_number) {
                return true; // Seat is already occupied
            }
        }
        return false; // Seat is available
    }
}
