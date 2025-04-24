package com.att.tdp.popcorn_palace.model;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Ticket {
    private Long id;

    @NotNull(message = "Showtime is required")
    private Showtime showtime;

    @NotNull(message = "Seat number is required")
    private int seat_number;

    @NotNull(message = "Customer ID is required")
    private Long customer_id;

    @NotNull(message = "Booking ID is required")
    private Long booking_id;

    public Ticket(Showtime showtime, int seat_number, Long customer_id, Long booking_id) {
        this.showtime = showtime;
        this.seat_number = seat_number;
        this.customer_id = customer_id;
        this.booking_id = booking_id;
    }
}
