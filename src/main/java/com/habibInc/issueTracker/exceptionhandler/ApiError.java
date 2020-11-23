package com.habibInc.issueTracker.exceptionhandler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ApiError {
    private String errorMessage;
    private HttpStatus status;
    private LocalDateTime timestamp;

    public ApiError(String errorMessage, LocalDateTime timestamp) {
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }
}
