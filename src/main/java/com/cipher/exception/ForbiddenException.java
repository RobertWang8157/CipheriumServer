package com.cipher.exception;

public class ForbiddenException extends RuntimeException {

    private String[] params;

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, String... params) {
        super(message);
        this.params = params;
    }

    public ForbiddenException(String message, String param) {
        super(message);
        this.params = new String[]{param};
    }

    public String[] getParams(){
        return params;
    }
}
