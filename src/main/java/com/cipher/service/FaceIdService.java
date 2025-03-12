package com.cipher.service;

import com.cipher.entity.User;
import com.cipher.repostory.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class FaceIdService {
    private final Session session;
    private final UserRepository userRepository;

    public FaceIdService(Session session, UserRepository userRepository) {
        this.session = session;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void warmup() {
        System.out.println("Warming up TensorFlow model...");
        try {
            float[] dummyEmbedding = getFaceEmbedding("dummyBase64");
            System.out.println("Model warm-up completed, sample embedding size: " + dummyEmbedding.length);
        } catch (Exception e) {
            System.err.println("Warm-up failed: " + e.getMessage());
        }
    }

    public String identifyFaceId(String username, String faceIdBase64Str) {
        try {
            User user = userRepository.findOfficerByUserName(username);
            float[] embedding1 = getFaceEmbedding(new String(user.getFaceId(), StandardCharsets.UTF_8));
            float[] embedding2 = getFaceEmbedding(faceIdBase64Str);

            double similarity = cosineSimilarity(embedding1, embedding2);
            return similarity > 0.7 ? "Y" : "N";
        } catch (Exception e) {
            e.printStackTrace();
            return "N";
        }
    }

    private float[] getFaceEmbedding(String base64EncodeStr) throws IOException {
        BufferedImage img = convertBase64ToBufferedImage(base64EncodeStr);
        float[][][][] input = preprocessImage(img);

        try (Tensor<?> inputTensor = Tensor.create(input);
             Tensor<Boolean> phaseTrainTensor = (Tensor<Boolean>) Tensor.create(false)) {
            Tensor<?> outputTensor = session.runner()
                    .feed("input:0", inputTensor)
                    .feed("phase_train:0", phaseTrainTensor)
                    .fetch("embeddings:0")
                    .run()
                    .get(0);

            float[][] embeddings = new float[1][512];
            outputTensor.copyTo(embeddings);
            return embeddings[0];
        } catch (Exception e) {
            throw new RuntimeException("無法生成嵌入", e);
        }
    }

    private static float[][][][] preprocessImage(BufferedImage img) {
        BufferedImage resized = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(img, 0, 0, 160, 160, null);
        g2d.dispose();

        float[][][][] pixels = new float[1][160][160][3];
        for (int y = 0; y < 160; y++) {
            for (int x = 0; x < 160; x++) {
                int rgb = resized.getRGB(x, y);
                pixels[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f;
                pixels[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f;
                pixels[0][y][x][2] = (rgb & 0xFF) / 255.0f;
            }
        }
        return pixels;
    }

    private static double cosineSimilarity(float[] embedding1, float[] embedding2) {
        double dotProduct = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += Math.pow(embedding1[i], 2);
            norm2 += Math.pow(embedding2[i], 2);
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public static BufferedImage convertBase64ToBufferedImage(String base64String) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bis);
        }
    }
}
