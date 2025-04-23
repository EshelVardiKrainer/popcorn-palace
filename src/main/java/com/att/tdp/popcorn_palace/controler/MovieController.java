package com.att.tdp.popcorn_palace.controler;
import org.springframework.web.bind.annotation.RestController;

import com.att.tdp.popcorn_palace.service.MovieService;

@RestController
public class MovieController {
    private MovieService movieService;
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

}
