package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an operation would create or update a movie
 * to a title that already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)   
public class DuplicateMovieTitleException extends RuntimeException {

    public DuplicateMovieTitleException(String title) {
        super("Movie title '" + title + "' already exists");
    }
}
