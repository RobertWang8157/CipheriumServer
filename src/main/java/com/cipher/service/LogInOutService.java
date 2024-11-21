package com.cipher.service;

import com.cipher.entity.LogInOut;
import com.cipher.repostory.LogInOutRepository;
import com.cipher.repostory.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service(value = "LogInOutService")

public class LogInOutService {

    Logger logger = LoggerFactory.getLogger(LogInOutService.class);

    @Autowired
    private LogInOutRepository logInOutRepository;

    public void createLogin(String userName, LocalDateTime nowDate){
        LogInOut log = new LogInOut();
        log.setAction("I");
        log.setUserName(userName);
        log.setAccessTime(nowDate);
        log.setResult("S");
        logInOutRepository.save(log);
    }


    public void createLoginFail(String userName, LocalDateTime nowDate){
        LogInOut log = new LogInOut();
        log.setAction("I");
        log.setUserName(userName);
        log.setAccessTime(nowDate);
        log.setResult("E");
        logInOutRepository.save(log);
    }
}
