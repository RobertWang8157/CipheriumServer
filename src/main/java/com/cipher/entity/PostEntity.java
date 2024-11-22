package com.cipher.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    @Basic
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PostWithoutData> fortisObjsList;
}
