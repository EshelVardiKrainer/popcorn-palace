package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.controler.TicketController;
import com.att.tdp.popcorn_palace.dto.TicketDTO;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private TicketDTO validTicketDTO;
    private Ticket bookedTicket;
    private UUID customerId;
    private UUID bookingId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        bookingId = UUID.randomUUID();

        validTicketDTO = new TicketDTO();
        validTicketDTO.setShowtimeId(1L);
        validTicketDTO.setSeatNumber(10);
        validTicketDTO.setCustomer_id(customerId);

        // Mocked Showtime, not strictly needed for controller test if service handles creation
        Showtime mockShowtime = new Showtime(); 
        mockShowtime.setId(1L);

        bookedTicket = new Ticket(mockShowtime, validTicketDTO.getSeatNumber(), customerId);
        
        bookedTicket.setBooking_id(bookingId); 
    }

    @Test
    void bookTicket_shouldReturnBookingId_whenSuccessful() throws Exception {
        given(ticketService.bookTicket(anyLong(), anyInt(), any(UUID.class))).willReturn(bookedTicket);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTicketDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId", is(bookingId.toString())));
    }

    @Test
    void bookTicket_shouldReturnBadRequest_whenShowtimeIdIsNull() throws Exception {
        TicketDTO invalidDTO = new TicketDTO();
        invalidDTO.setSeatNumber(10);
        invalidDTO.setCustomer_id(customerId);
        // ShowtimeId is null, which should be caught by @Valid on TicketDTO

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookTicket_shouldReturnBadRequest_whenSeatNumberIsNull() throws Exception {
        TicketDTO invalidDTO = new TicketDTO();
        invalidDTO.setShowtimeId(1L);
        invalidDTO.setCustomer_id(customerId);
        // SeatNumber is null

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookTicket_shouldReturnBadRequest_whenSeatNumberIsNegative() throws Exception {
        TicketDTO invalidDTO = new TicketDTO();
        invalidDTO.setShowtimeId(1L);
        invalidDTO.setSeatNumber(-5);
        invalidDTO.setCustomer_id(customerId);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookTicket_shouldReturnBadRequest_whenCustomerIdIsNull() throws Exception {
        TicketDTO invalidDTO = new TicketDTO();
        invalidDTO.setShowtimeId(1L);
        invalidDTO.setSeatNumber(10);
        // CustomerId is null

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookTicket_shouldReturnNotFound_whenShowtimeDoesNotExist() throws Exception {
        given(ticketService.bookTicket(anyLong(), anyInt(), any(UUID.class)))
                .willThrow(new EntityNotFoundException("Showtime not found"));

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTicketDTO)))
                .andExpect(status().isNotFound()); // Assuming a global exception handler translates this
    }

    @Test
    void bookTicket_shouldReturnBadRequest_whenSeatIsInvalidOrOccupied() throws Exception {
        given(ticketService.bookTicket(anyLong(), anyInt(), any(UUID.class)))
                .willThrow(new IllegalArgumentException("Seat issue"));

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTicketDTO)))
                .andExpect(status().isBadRequest()); // Assuming a global exception handler translates this
    }
}
