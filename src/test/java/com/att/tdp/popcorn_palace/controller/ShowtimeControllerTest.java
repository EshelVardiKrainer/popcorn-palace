package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.controler.ShowtimeController;
import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.exceptions.MovieNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.ShowtimeNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.ShowtimeOverlapException;
import com.att.tdp.popcorn_palace.mapper.ShowtimeMapper;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(ShowtimeController.class)
class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowtimeService showtimeService;

    @MockBean
    private ShowtimeMapper showtimeMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie movie1;
    private Showtime showtime1;
    private ShowtimeDTO showtimeDTO1;
    private Showtime showtime2; // For add/update return

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule()); // Ensure Instant is handled correctly

        movie1 = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        movie1.setId(1L);

        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);

        showtime1 = new Showtime(movie1, "Theater A", startTime, endTime, 12.50);
        showtime1.setId(10L);

        showtimeDTO1 = new ShowtimeDTO(10L, 1L, "Theater A", 12.50, startTime, endTime);
        
        // Showtime instance that would be returned by service after add/update
        showtime2 = new Showtime(movie1, "Theater A", startTime, endTime, 12.50);
        showtime2.setId(10L); // Assuming same ID for update, or new ID for add
    }

    @Test
    void getShowtimeById_shouldReturnShowtimeDTO_whenFound() throws Exception {
        given(showtimeService.fetchShowtimeByID(10L)).willReturn(showtime1);
        given(showtimeMapper.toDTO(showtime1)).willReturn(showtimeDTO1);

        mockMvc.perform(get("/showtimes/{showtimeId}", 10L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(showtimeDTO1.getId().intValue())))
                .andExpect(jsonPath("$.theater", is(showtimeDTO1.getTheater())))
                .andExpect(jsonPath("$.movieId", is(showtimeDTO1.getMovieId().intValue())))
                .andExpect(jsonPath("$.price", is(showtimeDTO1.getPrice())))
                .andExpect(jsonPath("$.startTime", is(showtimeDTO1.getStartTime().toString())))
                .andExpect(jsonPath("$.endTime", is(showtimeDTO1.getEndTime().toString())));
    }

    @Test
    void getShowtimeById_shouldReturnNotFound_whenShowtimeDoesNotExist() throws Exception {
        given(showtimeService.fetchShowtimeByID(99L)).willThrow(new ShowtimeNotFoundException(99L));

        mockMvc.perform(get("/showtimes/{showtimeId}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void addShowtime_shouldReturnCreatedShowtime_whenValidInput() throws Exception {
        ShowtimeDTO newShowtimeDTO = new ShowtimeDTO(null, 1L, "New Theater", 15.00, 
                                                    Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS), 
                                                    Instant.now().plus(2, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS));
        Showtime showtimeFromDTO = new Showtime(); // Mapped by mapper
        Showtime addedShowtime = new Showtime(); // Returned by service
        addedShowtime.setId(11L);
        addedShowtime.setMovie(movie1); // Ensure movie is set for response check
        addedShowtime.setTheater(newShowtimeDTO.getTheater());
        addedShowtime.setStart_time(newShowtimeDTO.getStartTime());
        addedShowtime.setEnd_time(newShowtimeDTO.getEndTime());
        addedShowtime.setPrice(newShowtimeDTO.getPrice());

        given(showtimeMapper.fromDTO(any(ShowtimeDTO.class))).willReturn(showtimeFromDTO);
        given(showtimeService.addShowtime(showtimeFromDTO)).willReturn(addedShowtime);

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newShowtimeDTO)))
                .andExpect(status().isOk()) // Controller returns 200 OK
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.theater", is(newShowtimeDTO.getTheater())));
    }

    @Test
    void addShowtime_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        ShowtimeDTO invalidDTO = new ShowtimeDTO(null, null, "Bad Theater", 10.0, Instant.now(), Instant.now().plusSeconds(100)); // Null movieId

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShowtime_shouldReturnNotFound_whenMovieDoesNotExist() throws Exception {
        ShowtimeDTO dtoWithNonExistentMovie = new ShowtimeDTO(null, 999L, "Theater X", 10.0, Instant.now(), Instant.now().plus(2, ChronoUnit.HOURS));
        Showtime showtimeFromDTO = new Showtime();
        Movie tempMovie = new Movie(); tempMovie.setId(999L); tempMovie.setTitle("Ghost Movie");
        showtimeFromDTO.setMovie(tempMovie);

        given(showtimeMapper.fromDTO(any(ShowtimeDTO.class))).willReturn(showtimeFromDTO);
        given(showtimeService.addShowtime(showtimeFromDTO)).willThrow(new MovieNotFoundException("Ghost Movie"));

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithNonExistentMovie)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addShowtime_shouldReturnConflict_whenShowtimeOverlaps() throws Exception {
        ShowtimeDTO overlappingDTO = showtimeDTO1; // Use existing DTO that might cause overlap
        Showtime showtimeFromDTO = showtime1;

        given(showtimeMapper.fromDTO(any(ShowtimeDTO.class))).willReturn(showtimeFromDTO);
        given(showtimeService.addShowtime(showtimeFromDTO)).willThrow(new ShowtimeOverlapException("Theater A", "start", "end"));

        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShowtime_shouldReturnUpdatedShowtime_whenValidInput() throws Exception {
        Long showtimeIdToUpdate = 10L;
        ShowtimeDTO updatedDTO = new ShowtimeDTO(showtimeIdToUpdate, 1L, "Updated Theater", 20.00, 
                                                showtime1.getStart_time().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS), 
                                                showtime1.getEnd_time().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS));
        Showtime showtimeFromDTO = new Showtime(); // Mapped by mapper
        Showtime serviceUpdatedShowtime = new Showtime(); // Returned by service.update or service.fetch
        serviceUpdatedShowtime.setId(showtimeIdToUpdate);
        serviceUpdatedShowtime.setMovie(movie1);
        serviceUpdatedShowtime.setTheater(updatedDTO.getTheater());
        serviceUpdatedShowtime.setPrice(updatedDTO.getPrice());
        serviceUpdatedShowtime.setStart_time(updatedDTO.getStartTime());
        serviceUpdatedShowtime.setEnd_time(updatedDTO.getEndTime());

        given(showtimeMapper.fromDTO(any(ShowtimeDTO.class))).willReturn(showtimeFromDTO);
        // The controller's update method calls service.updateShowtime then service.fetchShowtimeByID
        // Fix: Use given().willReturn() as updateShowtime returns Showtime
        given(showtimeService.updateShowtime(showtimeFromDTO, showtimeIdToUpdate)).willReturn(serviceUpdatedShowtime); 
        given(showtimeService.fetchShowtimeByID(showtimeIdToUpdate)).willReturn(serviceUpdatedShowtime);

        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeIdToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(showtimeIdToUpdate.intValue())))
                .andExpect(jsonPath("$.theater", is(updatedDTO.getTheater())))
                .andExpect(jsonPath("$.price", is(updatedDTO.getPrice())));
    }

    @Test
    void updateShowtime_shouldReturnNotFound_whenShowtimeDoesNotExist() throws Exception {
        Long nonExistentId = 99L;
        ShowtimeDTO updatedDTO = showtimeDTO1; // Content doesn't matter as much here
        Showtime showtimeFromDTO = new Showtime();

        given(showtimeMapper.fromDTO(any(ShowtimeDTO.class))).willReturn(showtimeFromDTO);
        // Simulate service throwing ShowtimeNotFoundException during the update call
        doThrow(new ShowtimeNotFoundException(nonExistentId)).when(showtimeService).updateShowtime(showtimeFromDTO, nonExistentId);

        mockMvc.perform(post("/showtimes/update/{showtimeId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShowtime_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        ShowtimeDTO invalidDTO = new ShowtimeDTO(10L, null, "Updated Theater", 20.00, Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS)); // Null movieId

        mockMvc.perform(post("/showtimes/update/{showtimeId}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShowtime_shouldReturnOk_whenShowtimeExists() throws Exception {
        Long showtimeIdToDelete = 10L;
        given(showtimeService.deleteShowtime(showtimeIdToDelete)).willReturn(true);

        mockMvc.perform(delete("/showtimes/{showtimeId}", showtimeIdToDelete))
                .andExpect(status().isOk()); // Controller returns 200 OK
    }

    @Test
    void deleteShowtime_shouldReturnNotFound_whenShowtimeDoesNotExist() throws Exception {
        Long nonExistentId = 99L;
        given(showtimeService.deleteShowtime(nonExistentId)).willThrow(new ShowtimeNotFoundException(nonExistentId));

        mockMvc.perform(delete("/showtimes/{showtimeId}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
