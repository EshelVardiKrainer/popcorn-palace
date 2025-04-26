package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)   
public class DuplicateMovieTitleException extends RuntimeException {

    public DuplicateMovieTitleException(String title) {
        super("Movie title '" + title + "' already exists");
    }
}
