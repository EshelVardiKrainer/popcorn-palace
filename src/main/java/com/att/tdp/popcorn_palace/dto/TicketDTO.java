package com.att.tdp.popcorn_palace.dto;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Data
@NoArgsConstructor
public class TicketDTO {
    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;

    @NotBlank(message = "Seat number is required")
    @Positive(message = "Seat number must be a positive number")
    private Integer seatNumber;

    @NotNull(message = "User ID is required")
    private UUID customer_id;
}
