package com.att.tdp.popcorn_palace.mapper;
import org.mapstruct.Mapper;
import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.model.Movie;


@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieDTO toDTO(Movie movie);

    Movie fromDTO(MovieDTO dto);
}
