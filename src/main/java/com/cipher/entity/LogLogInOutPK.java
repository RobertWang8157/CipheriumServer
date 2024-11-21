package com.cipher.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class LogLogInOutPK implements Serializable {

    private String userName;
    private LocalDateTime accessTime;

    public LogLogInOutPK(){

    }

    public LogLogInOutPK(String userName, LocalDateTime functionCode) {
        this.userName = userName;
        this.accessTime = functionCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogLogInOutPK that = (LogLogInOutPK) o;
        return Objects.equals(userName, that.userName) && Objects.equals(accessTime, that.accessTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, accessTime);
    }
}
