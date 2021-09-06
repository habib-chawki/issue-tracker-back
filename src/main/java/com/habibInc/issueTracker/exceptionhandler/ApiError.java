package com.habibInc.issueTracker.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String url;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError(String errorMessage, HttpStatus status, String url, LocalDateTime timestamp) {
        this.errorMessage = errorMessage;
        this.status = status;
        this.url = url;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Http error {" +
                "message: '" + errorMessage + '\'' +
                ", status: " + status +
                ", url: '" + url + '\'' +
                ", timestamp: " + timestamp +
                '}';
    }
}
