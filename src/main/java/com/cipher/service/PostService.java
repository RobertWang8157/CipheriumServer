package com.cipher.service;

import com.cipher.entity.PostEntity;
import com.cipher.repostory.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service(value = "PostService")
public class PostService {
    Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private PostRepository postRepository;

    public Integer add(String msg){
        PostEntity post= new PostEntity();
        post.setMsg(msg);
        post.setUploadTime(LocalDateTime.now());
        post = postRepository.save(post);

        return post.getId();
    }
    public List<PostEntity> findPosts(){
        return postRepository.findAll();
    }


}
