package com.habibInc.issueTracker.exceptionhandler;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex){
        ApiError error = new ApiError(ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ApiError> handleInvalidIdException(InvalidIdException ex){
        ApiError error = new ApiError(ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedJwtException(MalformedJwtException ex) {
        ApiError error = new ApiError(ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(ForbiddenOperationException ex){
        ApiError error = new ApiError(ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

}
