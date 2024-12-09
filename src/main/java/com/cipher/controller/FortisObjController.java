package com.cipher.controller;

import com.cipher.auth.AesEncryptUtil;
import com.cipher.dto.BasicDto;
import com.cipher.entity.FortisObj;
import com.cipher.entity.PostEntity;
import com.cipher.service.FortisObjService;
import com.cipher.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(value = "/fortis/obj", produces = MediaType.APPLICATION_JSON_VALUE)
public class FortisObjController extends AbstractController{
private final String secretKey="M2OO6K2y2SNCbR+VX/TWHYzQEeJDr8y1n6tKMWmxIqw=";
    @Autowired
    FortisObjService fortisObjService;
    @Autowired
    PostService postService;

    @RequestMapping(value = "/uploadPost", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<BasicDto> uploadPost(@RequestBody PostEntity post) {
        BasicDto basicDto = new BasicDto();
        try {
            Integer id =postService.add(post.getMsg());
            basicDto.setId(id);
            return ResponseEntity.ok().body(basicDto);
        } catch (Exception e) {
            basicDto.setMsg(e.getMessage());
            return ResponseEntity.ok().body(basicDto);
        }
    }
    @RequestMapping(value = "/sursum", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity uploadImage(@RequestHeader("X-post-id") Integer postId, @RequestParam("files") MultipartFile[] uploadFiles) {
        try {
            for (MultipartFile uploadFile : uploadFiles) {

                byte[] decompressedImageBytes = decompressImage(uploadFile);

                fortisObjService.encryptAndSaveImage(postId, decompressedImageBytes);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload: " + e.getMessage());
        }
    }

    public byte[] decompressImage(MultipartFile file) throws IOException {
        // 获取文件的输入流
        InputStream inputStream = file.getInputStream();

        // 创建 ZipInputStream 来读取压缩文件
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        // 获取压缩文件中的第一个文件
        ZipEntry entry = zipInputStream.getNextEntry();

        // 检查是否有文件
        if (entry == null) {
            throw new IOException("No files in the zip archive");
        }

        // 将解压后的数据保存到 ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }

        // 关闭流
        zipInputStream.closeEntry();
        zipInputStream.close();

        return outputStream.toByteArray();
    }


//    @RequestMapping(value = "/deorsum", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//    public ResponseEntity<?> downloadImages(@RequestParam("id") Integer id) {
//        Optional<FortisObj> optionalFiles = fortisObjService.findById(id);
//
//        if (optionalFiles.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
//        }
//
//        FortisObj encryptedFile = optionalFiles.get();
//        byte[] encryptedData = encryptedFile.getData();
//        byte[] iv = encryptedFile.getIv();
//
//        try {
//            // 解密密钥
//            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
//            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//
//            // 使用AES解密数据
//            byte[] decryptedData = AesEncryptUtil.decrypt(encryptedData, secretKey, iv);
//
//            // 设置响应头，标明文件是下载类型
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=\"image.jpg\"");  // 文件名
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentType(MediaType.IMAGE_JPEG)  // 设置文件类型为图片
//                    .body(decryptedData);  // 响应文件内容
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error occurred during decryption");
//
//        }
//    }

    @RequestMapping(value = "/deorsum", method = RequestMethod.POST)
    public ResponseEntity<Resource> downloadImages(@RequestParam("id") Integer id) {
        List<FortisObj> optionalFiles = fortisObjService.findByPostId(id);

        if (optionalFiles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        FortisObj encryptedFile = optionalFiles.get(0);
        byte[] encryptedData = encryptedFile.getData();
        byte[] iv = encryptedFile.getIv();

        try {
            // 解密密钥
            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            // 解密文件数据
            byte[] decryptedData = AesEncryptUtil.decrypt(encryptedData, secretKey, iv);

            // 创建 ZIP 文件流
            ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(zipOutputStream)) {
                // 创建 ZIP 条目并写入解密数据
                ZipEntry zipEntry = new ZipEntry("image.jpg");
                zos.putNextEntry(zipEntry);
                zos.write(decryptedData);
                zos.closeEntry();
            }

            // 将 ZIP 文件流包装为资源
            ByteArrayResource resource = new ByteArrayResource(zipOutputStream.toByteArray());

            // 设置响应头，标明文件类型和文件名
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zipOutputStream.size())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)  // 用于通用文件下载
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @RequestMapping(value = "/getPost", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<PostEntity>> getPost() {
        try {
            List<PostEntity> posts = postService.findPosts();
            return ResponseEntity.ok().body(posts);
        } catch (Exception e) {
            return null;
        }
    }

}