package com.att.tdp.popcorn_palace.mapper;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.model.Movie;

import org.checkerframework.checker.units.qual.m;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MovieMapperTest {

    private final MovieMapper movieMapper = Mappers.getMapper(MovieMapper.class);

    @Test
    void toDTO() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setDuration(148);
        movie.setRating(8.8);
        movie.setRelease_year(2010);

        MovieDTO movieDTO = movieMapper.toDTO(movie);

        assertNotNull(movieDTO);
        assertEquals(movie.getId(), movieDTO.getId());
        assertEquals(movie.getTitle(), movieDTO.getTitle());
        assertEquals(movie.getGenre(), movieDTO.getGenre());
        assertEquals(movie.getDuration(), movieDTO.getDuration());
        assertEquals(movie.getRating(), movieDTO.getRating());
        assertEquals(movie.getRelease_year(), movieDTO.getRelease_year());
        
    }

    @Test
    void fromDTO() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        movieDTO.setTitle("Inception");
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setRelease_year(2010);
        movieDTO.setDuration(148);
        movieDTO.setRating(8.8);
       

        Movie movie = movieMapper.fromDTO(movieDTO);

        assertNotNull(movie);
        assertEquals(movieDTO.getId(), movie.getId());
        assertEquals(movieDTO.getTitle(), movie.getTitle());
        assertEquals(movieDTO.getGenre(), movie.getGenre());
        assertEquals(movieDTO.getRelease_year(), movie.getRelease_year());
        assertEquals(movieDTO.getDuration(), movie.getDuration());
        assertEquals(movieDTO.getRating(), movie.getRating());
    }

    @Test
    void toDTO_whenMovieIsNull_shouldReturnNull() {
        MovieDTO movieDTO = movieMapper.toDTO(null);
        assertNull(movieDTO);
    }

    @Test
    void fromDTO_whenMovieDTOIsNull_shouldReturnNull() {
        Movie movie = movieMapper.fromDTO(null);
        assertNull(movie);
    }
}
