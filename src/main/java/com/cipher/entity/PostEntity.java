package com.cipher.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "POST", schema = "cipherium", catalog = "")
public class PostEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "msg")
    private String msg;
}
