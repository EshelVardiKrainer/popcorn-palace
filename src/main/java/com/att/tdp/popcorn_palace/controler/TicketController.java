package com.att.tdp.popcorn_palace.controler;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID; 

import org.springframework.http.ResponseEntity;
import com.att.tdp.popcorn_palace.service.TicketService;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.dto.TicketDTO;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor // Handles injection for final fields
public class TicketController {

    private final TicketService ticketService; // Injected by @RequiredArgsConstructor

    @PostMapping
    public ResponseEntity<Map<String, String>> bookTicket(@Valid @RequestBody TicketDTO ticketDTO) {

        // Call the service method with data from the DTO
        // It now expects Long, Integer, UUID as per the corrected TicketService
        Ticket bookedTicket = ticketService.bookTicket(
                ticketDTO.getShowtimeId(),
                ticketDTO.getSeatNumber(), // Use camelCase getter from DTO
                ticketDTO.getCustomer_id() // Use getter from DTO
        );

        // Create the response map
        Map<String, String> response = new HashMap<>();
        // Convert UUID to String for the JSON response
        response.put("bookingId", bookedTicket.getBooking_id().toString()); // <-- Added .toString()

        return ResponseEntity.ok(response);
    }

}
