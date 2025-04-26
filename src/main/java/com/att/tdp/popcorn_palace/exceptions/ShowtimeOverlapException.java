package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ShowtimeOverlapException extends RuntimeException {
    public ShowtimeOverlapException(String theater, String start, String end) {
        super("Showtime in '" + theater + "' between " + start + " and " + end + " overlaps an existing showtime");
    }
}