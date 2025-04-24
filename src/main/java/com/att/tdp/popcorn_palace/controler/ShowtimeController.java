package com.att.tdp.popcorn_palace.controler;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import com.att.tdp.popcorn_palace.model.Showtime;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {
    private ShowtimeService showtimeService;
   
    @GetMapping("{showtimeId}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long showtimeId) {
        Showtime showtime = showtimeService.fetchShowtimeByID(showtimeId);
        return ResponseEntity.ok(showtime);
    }

    @PostMapping
    public ResponseEntity<Showtime> addShowtime(Showtime showtime) {
        Showtime addedShowtime = showtimeService.addShowtime(showtime);
        return ResponseEntity.ok(addedShowtime);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Showtime> updateShowtime(Showtime updatedShowtime, @PathVariable Long showtimeId) {
        Showtime showtime = showtimeService.updaShowtime(updatedShowtime, showtimeId);
        return ResponseEntity.ok(showtime);
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok().build();
    }

    
}
