package com.hyperativa.card_api.domain.exception;

public class InvalidPanException extends RuntimeException {
    
    public InvalidPanException(String message) {
        super(message);
    }
    
    public InvalidPanException(String message, Throwable cause) {
        super(message, cause);
    }
}
