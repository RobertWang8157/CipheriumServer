package com.cipher.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDto {
    private Date timestamp;
    private String message;
    private Boolean isNeedResetPassword;

    public ExceptionDto(String message) {
        this.timestamp = new Date();
        this.message = message;
    }

    public ExceptionDto(String message, Boolean isNeedResetPassword) {
        this.timestamp = new Date();
        this.message = message;
        this.isNeedResetPassword = isNeedResetPassword;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsNeedResetPassword() {
        return isNeedResetPassword;
    }

    public void setIsNeedResetPassword(Boolean needResetPassword) {
        isNeedResetPassword = needResetPassword;
    }
}
