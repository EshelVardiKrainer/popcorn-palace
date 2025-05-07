package com.att.tdp.popcorn_palace.dto;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class TicketDTOTest {

    @Test
    public void testDefaultConstructorAndGettersSetters() {
        // Test default constructor
        TicketDTO ticketDTO = new TicketDTO();

        // Assert initial values are null (as they are object types)
        assertNull(ticketDTO.getShowtimeId(), "Default showtimeId should be null");
        assertNull(ticketDTO.getSeatNumber(), "Default seatNumber should be null");
        assertNull(ticketDTO.getCustomer_id(), "Default customer_id should be null");

        // Test setters and getters
        Long expectedShowtimeId = 1L;
        Integer expectedSeatNumber = 101;
        UUID expectedCustomerId = UUID.randomUUID();

        ticketDTO.setShowtimeId(expectedShowtimeId);
        ticketDTO.setSeatNumber(expectedSeatNumber);
        ticketDTO.setCustomer_id(expectedCustomerId);

        assertEquals(expectedShowtimeId, ticketDTO.getShowtimeId(), "Getter for showtimeId should return the set value");
        assertEquals(expectedSeatNumber, ticketDTO.getSeatNumber(), "Getter for seatNumber should return the set value");
        assertEquals(expectedCustomerId, ticketDTO.getCustomer_id(), "Getter for customer_id should return the set value");
    }

    @Test
    public void testSettersWithNullValues() {
        TicketDTO ticketDTO = new TicketDTO();

        // Set fields to some non-null values first
        ticketDTO.setShowtimeId(1L);
        ticketDTO.setSeatNumber(5);
        ticketDTO.setCustomer_id(UUID.randomUUID());

        // Now set them to null
        ticketDTO.setShowtimeId(null);
        ticketDTO.setSeatNumber(null);
        ticketDTO.setCustomer_id(null);

        assertNull(ticketDTO.getShowtimeId(), "showtimeId should be updatable to null");
        assertNull(ticketDTO.getSeatNumber(), "seatNumber should be updatable to null");
        assertNull(ticketDTO.getCustomer_id(), "customer_id should be updatable to null");
    }
}