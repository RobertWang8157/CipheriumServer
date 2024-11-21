package com.cipher.exception;

public class ParameterException extends RuntimeException {

    private String[] params;

    public ParameterException() {
        super();
    }

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, String... params) {
        super(message);
        this.params = params;
    }

    public ParameterException(String message, String param) {
        super(message);
        this.params = new String[]{param};
    }

    public String[] getParams(){
        return params;
    }
}
