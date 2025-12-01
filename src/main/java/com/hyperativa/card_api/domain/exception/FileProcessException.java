package com.hyperativa.card_api.domain.exception;

public class FileProcessException extends RuntimeException {

    public FileProcessException(String message) {
        super(message);
    }

    public FileProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
