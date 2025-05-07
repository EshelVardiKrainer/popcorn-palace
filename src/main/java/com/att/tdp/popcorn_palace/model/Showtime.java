package com.att.tdp.popcorn_palace.model;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.JoinColumn;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"movie", "tickets"})
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @NotNull(message = "Movie is required")
    private Movie movie;

    @NotNull(message = "Theater is required")
    private String theater;

    @NotNull(message = "Start time is required")
    private Instant start_time;

    @NotNull(message = "End time is required")
    private Instant end_time;

    @NotNull(message = "Price is required")
    private Double price;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    public Showtime(Movie movie, String theater, Instant start_time, Instant end_time, Double price) {
        this.movie = movie;
        this.theater = theater;
        this.start_time = start_time;
        this.end_time = end_time;
        this.price = price;
    }
}


