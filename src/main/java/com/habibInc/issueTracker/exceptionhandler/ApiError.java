package com.habibInc.issueTracker.exceptionhandler;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiError {
    private String errorMessage;
    private HttpStatus status;
    private LocalDateTime timestamp;

    public ApiError(String errorMessage, LocalDateTime timestamp) {
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
