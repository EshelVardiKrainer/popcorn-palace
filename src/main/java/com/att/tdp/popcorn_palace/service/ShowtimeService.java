package com.att.tdp.popcorn_palace.service;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;

public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }
}
