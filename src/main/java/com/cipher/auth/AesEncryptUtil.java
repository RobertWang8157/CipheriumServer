package com.cipher.auth;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesEncryptUtil {

    private static Logger logger = LoggerFactory.getLogger(AesEncryptUtil.class);

    private static String SALT = "t6OyhBDg)y5F";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // 认证标签长度
    private static final int IV_SIZE = 12; // 推荐的 GCM IV 长度

    public static byte[] encrypt(byte[] data, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
        return cipher.doFinal(encryptedData);
    }

    public static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // 256 位密钥
        return keyGen.generateKey();
    }
    public static String encrypt(String stringToEncrypt, String secretKey) {

        try {
            String hexSecretKey = new String(Hex.encode(secretKey.getBytes()));
            String hexSalt = new String(Hex.encode(SALT.getBytes()));
            BytesEncryptor encryptor = Encryptors.stronger(hexSecretKey, hexSalt);

            byte[] byteToEncrypt = stringToEncrypt.getBytes(StandardCharsets.UTF_8.toString());
            byte[] encryptByte = encryptor.encrypt(byteToEncrypt);
            return Base64.getEncoder().encodeToString(encryptByte);
        }catch (Exception ex){
            logger.error("Error in AesEncryptUtil.encrypt", ex);
            return null;
        }
    }

    public static String decrypt(String stringToDecrypt, String secretKey) {
        try{
            String hexSecretKey = new String(Hex.encode(secretKey.getBytes()));
            String hexSalt = new String(Hex.encode(SALT.getBytes()));
            BytesEncryptor encryptor = Encryptors.stronger(hexSecretKey, hexSalt);
            byte[] byteToDecrypt = stringToDecrypt.getBytes(StandardCharsets.UTF_8.toString());
            byte[] decryptByte = encryptor.decrypt(Base64.getDecoder().decode(byteToDecrypt));
            return new String(decryptByte);
        }catch (Exception ex){
            logger.error("Error in AesEncryptUtil.decrypt", ex);
            return null;
        }
    }

    public static boolean plainEncryptEquals(String plain, String encrypted, String secretKey){
        if(StringUtils.isNotEmpty(plain) && StringUtils.isNotEmpty(encrypted)){
            return StringUtils.equals(
                    plain, decrypt(encrypted, secretKey));
        }

        return false;
    }


    public static void main(String[] args) {
        String pwd = "averagemaxmingone";
        String encryptPwd=encrypt(pwd,"IMALIVE");
        System.out.println(encryptPwd);

    }

}