package com.att.tdp.popcorn_palace.service;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;

@Service
public class ShowtimeService {
    private ShowtimeRepository showtimeRepository;
    private MovieRepository movieRepository;

    public Showtime addShowtime(Showtime showtime) {
        // Check if the showtimeId is valid
        if (showtimeRepository.existsById(showtime.getId())) {
            throw new IllegalArgumentException("Showtime ID already exists");
        }
        // Check if the movie exists
        if(movieRepository.findById(showtime.getMovie().getId()).isEmpty()){
            throw new IllegalArgumentException("Movie is not found");
        }
       // Check if start time, end time and the time range are valid
       if (!showtime.getStart_time().isBefore(showtime.getEnd_time())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Check if the showtime overlaps with an existing showtime
        if (existsByTheaterAndTime(showtime.getTheater(), showtime.getStart_time(), showtime.getEnd_time())) {
            throw new IllegalArgumentException("Showtime overlaps with an existing showtime");
        }
    
        return showtimeRepository.save(showtime);
    }

    public Showtime updaShowtime(Showtime showtime, Long showtimeId) {
        // Check if the showtimeId is valid
        if(!showtimeRepository.existsById(showtimeId)){
            throw new IllegalArgumentException("Showtime ID not found");
               
        }
        // Check if the movie exists
        if(movieRepository.findById(showtime.getMovie().getId()).isEmpty()){
            throw new IllegalArgumentException("Movie is not found");
        }
       // Check if start time, end time and the time range are valid
       if (!showtime.getStart_time().isBefore(showtime.getEnd_time())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Check if the showtime overlaps with an existing showtime
        if (existsByTheaterAndTime(showtime.getTheater(), showtime.getStart_time(), showtime.getEnd_time())) {
            throw new IllegalArgumentException("Showtime overlaps with an existing showtime");
        }
    
        return showtimeRepository.save(showtime);
    }

    public Showtime fetchShowtimeByID(Long showtimeId) {
        // Check if the showtimeId is valid
        if(!showtimeRepository.existsById(showtimeId)){
            throw new IllegalArgumentException("Showtime ID not found");
               
        }
        return showtimeRepository.findById(showtimeId).get();
    }

    public boolean deleteShowtime(Long showtimeId) {
        // Check if the showtimeId is valid
        if(!showtimeRepository.existsById(showtimeId)){
            throw new IllegalArgumentException("Showtime ID not found");
               
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
