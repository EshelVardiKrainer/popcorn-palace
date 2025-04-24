package com.att.tdp.popcorn_palace.mapper;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.model.Movie;

public class ShowtimeMapper {
    private MovieService movieService;
    
    public ShowtimeDTO toDTO(Showtime showtime){
        if (showtime == null) {
            return null;
        }
        ShowtimeDTO showtimeDTO = new ShowtimeDTO();
        showtimeDTO.setId(showtime.getId());
        showtimeDTO.setMovieId(showtime.getMovie().getId());
        showtimeDTO.setTheater(showtime.getTheater());
        showtimeDTO.setPrice(showtime.getPrice());
        showtimeDTO.setStartTime(showtime.getStart_time());
        showtimeDTO.setEndTime(showtime.getEnd_time());
        return showtimeDTO;
    }

    public Showtime fromDTO(ShowtimeDTO showtimeDTO){
        if (showtimeDTO == null) {
            return null;
        }
        Movie showtimeMovie = movieService.getMovieById(showtimeDTO.getMovieId());
        if (showtimeMovie == null) {
            throw new IllegalArgumentException("Movie not found with ID: " + showtimeDTO.getMovieId());
        }
        Showtime showtime = new Showtime();
        showtime.setId(showtimeDTO.getId());
        showtime.setTheater(showtimeDTO.getTheater());
        showtime.setPrice(showtimeDTO.getPrice());
        showtime.setStart_time(showtimeDTO.getStartTime());
        showtime.setEnd_time(showtimeDTO.getEndTime());
        return showtime;
    }


}
