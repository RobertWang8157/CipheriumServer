package com.cipher.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "USER_DATA")
public class User {
    @Id
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "PASSWORD")
    private String password;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "face_id")
    private byte[] faceId;

}
