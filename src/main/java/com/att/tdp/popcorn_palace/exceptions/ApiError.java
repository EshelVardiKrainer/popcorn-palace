package com.att.tdp.popcorn_palace.exceptions;

import java.time.Instant;

/**
 * Represents an API error response with a status, timestamp, and message.
 */
// Rename this file to ApiError.java to match the public class name
public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;

    public ApiError(Instant timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}