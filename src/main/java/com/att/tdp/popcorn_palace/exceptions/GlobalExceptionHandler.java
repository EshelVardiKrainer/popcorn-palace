package com.att.tdp.popcorn_palace.exceptions;

import java.time.Instant;
import java.util.Map;
import com.att.tdp.popcorn_palace.exceptions.ShowtimeOverlapException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.att.tdp.popcorn_palace.exceptions.ApiError;


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

    private ResponseEntity<ApiError> buildError(HttpStatus status, Exception ex) {
        ApiError apiError = new ApiError(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        );
        return ResponseEntity.status(status).body(apiError);
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

    @ExceptionHandler(ShowtimeNotFoundException.class)
    public ResponseEntity<ApiError> handleShowtimeNotFound(ShowtimeNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(ShowtimeOverlapException.class)
    public ResponseEntity<ApiError> handleShowtimeOverlap(ShowtimeOverlapException ex) {
        return buildError(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex);
    }

}
