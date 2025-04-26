package com.att.tdp.popcorn_palace.dto;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TicketDTO {

    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;

    @NotNull(message = "Seat number is required")
    @Positive(message = "Seat number must be a positive number")
    private Integer seatNumber;

    @NotNull(message = "User ID is required")
    @JsonProperty("userId") 
    private UUID customer_id; // Keep Java field name as customer_id
}
