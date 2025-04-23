package com.att.tdp.popcorn_palace.controler;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShowtimeController {
    private ShowtimeService showtimeService;
    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }
    
}
