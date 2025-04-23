package com.att.tdp.popcorn_palace.service;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;

@Service
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    public Showtime fetchShowtimes(Long showtimeId) {
        // Check if the showtimeId is valid
        if(!showtimeRepository.existsById(showtimeId)){
            throw new IllegalArgumentException("Showtime ID not found");
               
        }
        return showtimeRepository.findById(showtimeId).get();
    }

}
