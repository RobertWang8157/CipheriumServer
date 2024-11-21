package com.cipher.exception;

public class NotExistException extends RuntimeException {

    public NotExistException() {
    }

    public NotExistException(Throwable e) {
        super(e);
    }

    public NotExistException(String message) {
        super(message);
    }
}
