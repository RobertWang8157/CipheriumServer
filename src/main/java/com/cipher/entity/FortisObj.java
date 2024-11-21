package com.cipher.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "FORTIS_OBJ", schema = "cipherium", catalog = "")
public class FortisObj {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "post_id")
    private int postId;

    @Basic
    @Column(name = "data")
    private byte[] data;

    @Column(name = "iv")
    private byte[] iv;

    @Basic
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

}
