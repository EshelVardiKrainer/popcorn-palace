package com.att.tdp.popcorn_palace.model;

public class Showtime {
    private Long id;
    private Movie movie;
    private String theater;
    private String start_time;
    private String end_time;

    public Showtime(Movie movie, String theater, String start_time, String end_time) {
        this.movie = movie;
        this.theater = theater;
        this.start_time = start_time;
        this.end_time = end_time;
    }
}


