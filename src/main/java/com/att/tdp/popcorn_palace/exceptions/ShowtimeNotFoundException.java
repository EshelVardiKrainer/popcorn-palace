package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShowtimeNotFoundException extends RuntimeException {
    public ShowtimeNotFoundException(Long id) {
        super("Showtime with id " + id + " not found");
    }
}

