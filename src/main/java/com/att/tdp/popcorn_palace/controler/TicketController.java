package com.att.tdp.popcorn_palace.controler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import com.att.tdp.popcorn_palace.service.TicketService;
import com.att.tdp.popcorn_palace.model.Ticket;

@RestController
@RequestMapping("/bookings")
public class TicketController {
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<Map<String, String>> bookTicket(Ticket ticket) {
        Ticket bookedTicket = ticketService.bookTicket(ticket);
        Map<String, String> response = new HashMap<>();

        response.put("bookingId", bookedTicket.getBooking_id());

        return ResponseEntity.ok(response);
    }
    
}
