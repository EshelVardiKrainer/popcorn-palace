package com.att.tdp.popcorn_palace.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Showtime is required")
    private Showtime showtime;

    @NotNull(message = "Seat number is required")
    @Positive(message = "Seat number must be a positive number")
    private Integer seat_number;

    @NotNull(message = "Customer ID is required")
    private Long customer_id;

    @NotNull(message = "Booking ID is required")
    private String booking_id;

    public Ticket(Showtime showtime, Integer seat_number, Long customer_id, String booking_id) {
        this.showtime = showtime;
        this.seat_number = seat_number;
        this.customer_id = customer_id;
        this.booking_id = booking_id;
    }
}
