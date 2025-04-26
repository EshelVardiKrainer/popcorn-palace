package com.att.tdp.popcorn_palace.controler;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.mapper.ShowtimeMapper;
import com.att.tdp.popcorn_palace.model.Showtime;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {
    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private ShowtimeMapper showtimeMapper;
   
    @GetMapping("{showtimeId}")
    public ResponseEntity<ShowtimeDTO> getShowtimeById(@PathVariable Long showtimeId) {
        Showtime showtime = showtimeService.fetchShowtimeByID(showtimeId);
        ShowtimeDTO showtimeDTO = showtimeMapper.toDTO(showtime);
        return ResponseEntity.ok(showtimeDTO);
    }

    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@RequestBody ShowtimeDTO showtimeDTO) {
        Showtime showtime = showtimeMapper.fromDTO(showtimeDTO);
        Showtime addedShowtime = showtimeService.addShowtime(showtime);
        return ResponseEntity.ok(addedShowtime);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Showtime> updateShowtime(@RequestBody ShowtimeDTO updatedShowtimeDTO, @PathVariable Long showtimeId) {
        Showtime updatedShowtime = showtimeMapper.fromDTO(updatedShowtimeDTO);
        showtimeService.updateShowtime(updatedShowtime, showtimeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok().build();
    }

    
}
