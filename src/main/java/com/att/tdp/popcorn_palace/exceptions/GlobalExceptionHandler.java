package com.att.tdp.popcorn_palace.exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Catches application-specific exceptions and turns them
 * into JSON responses with a status, timestamp and message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateMovieTitleException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateTitle(
            DuplicateMovieTitleException ex) {

        Map<String, Object> body = Map.of(
            "timestamp", Instant.now(),
            "status", HttpStatus.CONFLICT.value(),
            "error",  "Duplicate Movie Title",
            "message", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMovieNotFound(
            MovieNotFoundException ex) {

        Map<String, Object> body = Map.of(
            "timestamp", Instant.now(),
            "status",    HttpStatus.NOT_FOUND.value(),
            "error",     "Movie Not Found",
            "message",   ex.getMessage()         
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
