package com.att.tdp.popcorn_palace.mapper;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.mapstruct.Mapper; 
import org.mapstruct.Mapping;

@Mapper 
public interface ShowtimeMapper {

    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "start_time", target = "startTime")
    @Mapping(source = "end_time", target = "endTime")
    ShowtimeDTO toDTO(Showtime showtime);

    @Mapping(source = "startTime", target = "start_time")
    @Mapping(source = "endTime", target = "end_time")
    Showtime fromDTO(ShowtimeDTO showtimeDTO);
}
