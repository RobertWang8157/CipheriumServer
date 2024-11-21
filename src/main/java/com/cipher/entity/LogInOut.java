package com.cipher.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@IdClass(LogLogInOutPK.class)
@Table(name = "LOG_IN_OUT", schema = "cipherium", catalog = "")
public class LogInOut {
    @Basic
    @Id
    @Column(name = "USER_NAME")
    private String userName;
    @Basic
    @Id
    @Column(name = "ACCESS_TIME")
    private LocalDateTime accessTime;
    @Basic
    @Column(name = "ACTION")
    private String action;
    @Basic
    @Column(name = "RESULT")
    private String result;

}
