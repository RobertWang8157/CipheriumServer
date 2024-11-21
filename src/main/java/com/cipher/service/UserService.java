package com.cipher.service;

import com.cipher.entity.User;
import com.cipher.repostory.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "UserService")
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public Long getCount(){
        return userRepository.count();
    }

    public User findByUserName(String name){
        return userRepository.findOfficerByUserName(name);
    }

}
