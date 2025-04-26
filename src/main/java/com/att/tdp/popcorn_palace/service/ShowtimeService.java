package com.att.tdp.popcorn_palace.service;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

import com.att.tdp.popcorn_palace.exceptions.MovieNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.ShowtimeNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.ShowtimeOverlapException;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public Showtime addShowtime(Showtime showtime) {
        System.out.println("Adding showtime: " + showtime);
        // Check if the movie exists
        if(movieRepository.findById(showtime.getMovie().getId()).isEmpty()){
            throw new MovieNotFoundException(showtime.getMovie().getTitle());
        }
       // Check if start time, end time and the time range are valid
       if (!showtime.getStart_time().isBefore(showtime.getEnd_time())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Check if the showtime overlaps with an existing showtime
        if (existsByTheaterAndTime(showtime.getTheater(), showtime.getStart_time(), showtime.getEnd_time())) {
            throw new ShowtimeOverlapException(showtime.getTheater(), showtime.getStart_time().toString(), showtime.getEnd_time().toString());
        }
    
        return showtimeRepository.save(showtime);
    }
    public Showtime updateShowtime(Showtime showtime, Long showtimeId) {

        // 1. Validate show-time id
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new ShowtimeNotFoundException(showtimeId);
        }
    
        // 2. Validate movie id
        movieRepository.findById(showtime.getMovie().getId())
                       .orElseThrow(() ->
                           new MovieNotFoundException(showtime.getMovie().getTitle()));
    
        // 3. Validate time range
        if (!showtime.getStart_time().isBefore(showtime.getEnd_time())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    
        // 4. Check for overlap in the same theatre
        if (existsByTheaterAndTime(
                showtime.getTheater(),
                showtime.getStart_time(),
                showtime.getEnd_time())) {
    
            throw new ShowtimeOverlapException(
                    showtime.getTheater(),
                    showtime.getStart_time().toString(),
                    showtime.getEnd_time().toString());
        }
    
        // 5. Apply updates and persist
        Showtime existing = showtimeRepository.findById(showtimeId).get();
        existing.setMovie(showtime.getMovie());
        existing.setTheater(showtime.getTheater());
        existing.setStart_time(showtime.getStart_time());
        existing.setEnd_time(showtime.getEnd_time());
        existing.setPrice(showtime.getPrice());
    
        return showtimeRepository.save(existing);
    }
    

    public Showtime fetchShowtimeByID(Long showtimeId) {
        // Check if the showtimeId is valid
        if(!showtimeRepository.existsById(showtimeId)){
            throw new ShowtimeNotFoundException(showtimeId);
               
        }
        return showtimeRepository.findById(showtimeId).get();
    }

    public boolean deleteShowtime(Long showtimeId) {
        // Check if the showtimeId is valid
        if(!showtimeRepository.existsById(showtimeId)){
            throw new ShowtimeNotFoundException(showtimeId);
               
        }
        showtimeRepository.deleteById(showtimeId);
        return true;
    }

    private boolean existsByTheaterAndTime(String theater, Instant start_time, Instant end_time) {
        List<Showtime> showtimes = showtimeRepository.findByTheater(theater);
        for (Showtime showtime : showtimes) {
            if (isOverLapping(showtime.getStart_time(), showtime.getEnd_time(), start_time, end_time)) {
                return true;
            }
        }
        // Check if the showtime overlaps with an existing showtime
        return false;
    }

    private boolean isOverLapping(Instant existingStart, Instant existingEnd, Instant start_time, Instant end_time) {
        if (!(existingEnd.isBefore(start_time) || existingStart.isAfter(end_time))) {
            // The existing showtime overlaps with the new showtime
            return true;
        }
        return false;
    }

}
