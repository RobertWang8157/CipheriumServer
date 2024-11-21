package com.cipher.auth;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AesPasswordEncoder implements PasswordEncoder {

    public AesPasswordEncoder(){

    }

    @Override
    public String encode(CharSequence rawPassword) {
        return AesEncryptUtil.encrypt(rawPassword.toString(), "IMALIVE");
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return AesEncryptUtil.plainEncryptEquals(rawPassword.toString(), encodedPassword, "IMALIVE");
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return PasswordEncoder.super.upgradeEncoding(encodedPassword);
    }
}
