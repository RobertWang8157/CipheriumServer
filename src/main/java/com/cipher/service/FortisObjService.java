package com.cipher.service;

import com.cipher.auth.AesEncryptUtil;
import com.cipher.entity.FortisObj;
import com.cipher.repostory.FortisObjRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service(value = "FortisObjService")

public class FortisObjService {

@Autowired
private FortisObjRepository fortisObjRepository;


public Optional<FortisObj> findByPostId(Integer id){

    return fortisObjRepository.findByPostId(id);
}
    public void encryptAndSaveImage(Integer postId, byte[] fileBytes)  {
        try {
            LocalDateTime nowDate = LocalDateTime.now();
            // 生成随机 IV
            byte[] iv = AesEncryptUtil.generateIV();

            byte[] decodedKey = Base64.getDecoder().decode("M2OO6K2y2SNCbR+VX/TWHYzQEeJDr8y1n6tKMWmxIqw=");

            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            byte[] encryptedBytes = AesEncryptUtil.encrypt(fileBytes, secretKey, iv);

            FortisObj obj = new FortisObj();
            obj.setData(encryptedBytes);
            obj.setUploadTime(nowDate);
            obj.setPostId(postId);
            obj.setIv(iv);
            fortisObjRepository.save(obj);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
