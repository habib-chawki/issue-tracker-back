package com.habibInc.issueTracker.exceptionhandler;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
