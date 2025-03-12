package com.cipher.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class FaceBase64Decode {
    public static String encodeFileToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static void main(String[] args) {

        String filePath = "/Users/johnny/Pictures/face/照片2025-3-11 下午4.36.jpg";
        try {
            String base64String = encodeFileToBase64(filePath);
            System.out.println("Base64 編碼結果：");
            System.out.println(base64String);
        } catch (IOException e) {
            System.err.println("讀取檔案時發生錯誤: " + e.getMessage());
        }
    }

}
