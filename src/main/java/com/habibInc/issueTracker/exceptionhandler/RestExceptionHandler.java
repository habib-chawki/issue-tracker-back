package com.habibInc.issueTracker.exceptionhandler;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(HttpServletRequest request, ResourceNotFoundException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ApiError> handleInvalidIdException(HttpServletRequest request, InvalidIdException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedJwtException(HttpServletRequest request, MalformedJwtException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(HttpServletRequest request, ForbiddenOperationException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        ApiError error = new ApiError(
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException ex){
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI(), LocalDateTime.now());
          return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllOtherExceptions(HttpServletRequest request, Exception ex) {
        ApiError error = new ApiError("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
