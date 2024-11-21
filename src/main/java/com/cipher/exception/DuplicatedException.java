package com.cipher.exception;

public class DuplicatedException extends RuntimeException {

    private String[] params;

    public DuplicatedException() {
    }

    public DuplicatedException(Throwable e) {
        super(e);
    }

    public DuplicatedException(String message) {
        super(message);
    }

    public DuplicatedException(String message, String... params) {
        super(message);
        this.params = params;
    }

    public DuplicatedException(String message, String param) {
        super(message);
        this.params = new String[]{param};
    }

    public String[] getParams(){
        return params;
    }
}
