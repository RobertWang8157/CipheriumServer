package com.cipher.test;

import org.tensorflow.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class FaceNetExampleFromDeepSeek {

    public static void main(String[] args) {
        String modelPath = "/Users/johnny/IdeaProjects/Cipherium/src/main/resources/20180408-102900.pb";
        String imagePath1 = "/Users/johnny/Pictures/face/MV5BMTgzMTA0MDUzNF5BMl5BanBnXkFtZTcwOTM2ODcwNw@@._V1_.jpg";
        String imagePath2 = "/Users/johnny/Pictures/face/MV5BOTgzNjAxNTMtZTY4MC00N2M3LTk3YWItZTFkNmU1MDcwZjNkXkEyXkFqcGc@._V1_.jpg";

        try {
            byte[] graphDef = Files.readAllBytes(Paths.get(modelPath));

            try (Graph graph = new Graph()) {
                graph.importGraphDef(graphDef);

                // 列出 TensorFlow 模型中的所有运算名称
                for (Iterator<Operation> it = graph.operations(); it.hasNext(); ) {
                    Operation op = it.next();
                    System.out.println(op.name());
                }

                try (Session session = new Session(graph)) {
                    float[] embedding1 = getFaceEmbedding(session, imagePath1);
                    float[] embedding2 = getFaceEmbedding(session, imagePath2);

                    double similarity = cosineSimilarity(embedding1, embedding2);
                    double threshold = 0.7;
                    System.out.println(similarity > threshold ? "同一人" : "不同人");
                }
            }
        } catch (IOException e) {
            System.err.println("讀取模型文件失敗：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float[] getFaceEmbedding(Session session, String imagePath) throws IOException {
        float[][][][] input = preprocessImage(imagePath);

        try (Tensor<?> inputTensor = Tensor.create(input);
             Tensor<Boolean> phaseTrainTensor = (Tensor<Boolean>) Tensor.create(false)) { // 设置为推论模式

            Tensor<?> outputTensor = session.runner()
                    .feed("input:0", inputTensor)
                    .feed("phase_train:0", phaseTrainTensor)
                    .fetch("embeddings:0")
                    .run()
                    .get(0);

            // Update to 512 dimensions as per the model's output
            float[][] embeddings = new float[1][512]; // 512-dimensional embedding
            outputTensor.copyTo(embeddings);
            return embeddings[0]; // Return the 512-dimensional vector
        } catch (Exception e) {
            throw new RuntimeException("無法生成嵌入：" + imagePath, e);
        }
    }

    private static float[][][][] preprocessImage(String imagePath) throws IOException {
        BufferedImage img = ImageIO.read(new File(imagePath));
        if (img == null) {
            throw new IOException("無法讀取圖片：" + imagePath);
        }

        BufferedImage resized = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(img, 0, 0, 160, 160, null);
        g2d.dispose();

        float[][][][] pixels = new float[1][160][160][3]; // Ensure 4D input
        for (int y = 0; y < 160; y++) {
            for (int x = 0; x < 160; x++) {
                int rgb = resized.getRGB(x, y);
                pixels[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f; // R
                pixels[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f;  // G
                pixels[0][y][x][2] = (rgb & 0xFF) / 255.0f;         // B
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
}
