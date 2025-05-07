package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.exceptions.MovieNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.ShowtimeNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.ShowtimeOverlapException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Showtime showtime;
    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");

        showtime = new Showtime();
        showtime.setId(1L);
        showtime.setMovie(movie);
        showtime.setTheater("Theater 1");
        showtime.setStart_time(Instant.now().plus(1, ChronoUnit.HOURS));
        showtime.setEnd_time(Instant.now().plus(3, ChronoUnit.HOURS));
    }

    @Test
    void addShowtime_success() {
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheater(showtime.getTheater())).thenReturn(Collections.emptyList());
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(showtime);

        Showtime addedShowtime = showtimeService.addShowtime(showtime);

        assertNotNull(addedShowtime);
        assertEquals(showtime.getTheater(), addedShowtime.getTheater());
        verify(showtimeRepository, times(1)).save(any(Showtime.class));
    }

    @Test
    void addShowtime_movieNotFound() {
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> showtimeService.addShowtime(showtime));
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void addShowtime_invalidTimeRange() {
        showtime.setEnd_time(showtime.getStart_time().minus(1, ChronoUnit.HOURS)); // End time before start time
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));

        assertThrows(IllegalArgumentException.class, () -> showtimeService.addShowtime(showtime));
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void addShowtime_overlap() {
        Showtime existingShowtime = new Showtime();
        BeanUtils.copyProperties(showtime, existingShowtime, "id");
        existingShowtime.setId(2L);

        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheater(showtime.getTheater())).thenReturn(List.of(existingShowtime));

        assertThrows(ShowtimeOverlapException.class, () -> showtimeService.addShowtime(showtime));
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }


    @Test
    void updateShowtime_success() {
        Showtime updatedShowtimeDetails = new Showtime();
        BeanUtils.copyProperties(showtime, updatedShowtimeDetails);
        updatedShowtimeDetails.setTheater("Theater 2");

        when(showtimeRepository.existsById(showtime.getId())).thenReturn(true);
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheater(updatedShowtimeDetails.getTheater())).thenReturn(Collections.emptyList());
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(updatedShowtimeDetails);


        Showtime updatedShowtime = showtimeService.updateShowtime(updatedShowtimeDetails, showtime.getId());

        assertNotNull(updatedShowtime);
        assertEquals("Theater 2", updatedShowtime.getTheater());
        verify(showtimeRepository, times(1)).save(any(Showtime.class));
    }

    @Test
    void updateShowtime_showtimeNotFound() {
        when(showtimeRepository.existsById(showtime.getId())).thenReturn(false);

        assertThrows(ShowtimeNotFoundException.class, () -> showtimeService.updateShowtime(showtime, showtime.getId()));
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void updateShowtime_movieNotFound() {
        when(showtimeRepository.existsById(showtime.getId())).thenReturn(true);
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> showtimeService.updateShowtime(showtime, showtime.getId()));
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }
    
    @Test
    void updateShowtime_invalidTimeRange() {
        showtime.setEnd_time(showtime.getStart_time().minus(1, ChronoUnit.HOURS)); // End time before start time
        when(showtimeRepository.existsById(showtime.getId())).thenReturn(true);
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));


        assertThrows(IllegalArgumentException.class, () -> showtimeService.updateShowtime(showtime, showtime.getId()));
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void updateShowtime_overlap() {
        Showtime existingShowtime = new Showtime(); // Simulates an existing showtime that would overlap
        existingShowtime.setId(2L); // Different ID
        existingShowtime.setMovie(movie);
        existingShowtime.setTheater(showtime.getTheater()); // Same theater
        existingShowtime.setStart_time(showtime.getStart_time().minus(30, ChronoUnit.MINUTES)); // Overlapping time
        existingShowtime.setEnd_time(showtime.getEnd_time().plus(30, ChronoUnit.MINUTES));

        when(showtimeRepository.existsById(showtime.getId())).thenReturn(true);
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(showtimeRepository.findByTheater(showtime.getTheater())).thenReturn(List.of(existingShowtime));
        // No need to mock findById for showtime as overlap check happens before fetching existing for update

        assertThrows(ShowtimeOverlapException.class, () -> showtimeService.updateShowtime(showtime, showtime.getId()));
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }


    @Test
    void fetchShowtimeByID_success() {
        when(showtimeRepository.existsById(showtime.getId())).thenReturn(true);
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));

        Showtime foundShowtime = showtimeService.fetchShowtimeByID(showtime.getId());

        assertNotNull(foundShowtime);
        assertEquals(showtime.getId(), foundShowtime.getId());
    }

    @Test
    void fetchShowtimeByID_notFound() {
        when(showtimeRepository.existsById(showtime.getId())).thenReturn(false);

        assertThrows(ShowtimeNotFoundException.class, () -> showtimeService.fetchShowtimeByID(showtime.getId()));
    }

    @Test
    void deleteShowtime_success() {
        when(showtimeRepository.existsById(showtime.getId())).thenReturn(true);
        doNothing().when(showtimeRepository).deleteById(showtime.getId());

        boolean deleted = showtimeService.deleteShowtime(showtime.getId());

        assertTrue(deleted);
        verify(showtimeRepository, times(1)).deleteById(showtime.getId());
    }

    @Test
    void deleteShowtime_notFound() {
        when(showtimeRepository.existsById(showtime.getId())).thenReturn(false);

        assertThrows(ShowtimeNotFoundException.class, () -> showtimeService.deleteShowtime(showtime.getId()));
        verify(showtimeRepository, never()).deleteById(anyLong());
    }
}
