package com.cipher.exception;

public class SystemInternalErrorException extends RuntimeException {

    private String[] params;

    public SystemInternalErrorException() {
        super();
    }

    public SystemInternalErrorException(String message) {
        super(message);
    }

    public SystemInternalErrorException(String message, String... params) {
        super(message);
        this.params = params;
    }

    public SystemInternalErrorException(String message, String param) {
        super(message);
        this.params = new String[]{param};
    }

    public String[] getParams(){
        return params;
    }
}
