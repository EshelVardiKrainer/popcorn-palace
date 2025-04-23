package com.att.tdp.popcorn_palace.model;
import com.att.tdp.popcorn_palace.model.Showtime;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Tickets {
    private Long id;
    private Showtime showtime;
    private int seat_number;
    private Long customer_id;
    private Long booking_id;

    public Tickets(Showtime showtime, int seat_number, Long customer_id, Long booking_id) {
        this.showtime = showtime;
        this.seat_number = seat_number;
        this.customer_id = customer_id;
        this.booking_id = booking_id;
    }
}
