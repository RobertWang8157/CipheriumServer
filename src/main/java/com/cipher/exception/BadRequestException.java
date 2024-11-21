package com.cipher.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
    }

    public BadRequestException(Throwable e) {
        super(e);
    }

    public BadRequestException(String message) {
        super(message);
    }
}
