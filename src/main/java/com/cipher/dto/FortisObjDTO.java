package com.cipher.dto;

import java.time.LocalDateTime;

public class FortisObjDTO {
    private int id;
    private int postId;
    private LocalDateTime uploadTime;

    // Constructor
    public FortisObjDTO(int id, int postId, LocalDateTime uploadTime) {
        this.id = id;
        this.postId = postId;
        this.uploadTime = uploadTime;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }
}


