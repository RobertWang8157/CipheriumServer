package com.cipher.test;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class QRCodeGenerator {

    public static void generateQRCodeWithLogo(String text, int width, int height, String qrPath, String logoPath)
            throws WriterException, IOException {
        // 設定 QR Code 參數，提升糾錯能力，讓 Logo 可以被放上去
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H 等級糾錯 (30% 可遮擋)

        // 生成 QR Code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = qrImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }
        graphics.dispose();

        // 加載 Logo 圖片
        BufferedImage logo = ImageIO.read(new File(logoPath));
        int logoWidth = width / 5;  // Logo 大小為 QR Code 的 1/5
        int logoHeight = height / 5;
        int centerX = (width - logoWidth) / 2;
        int centerY = (height - logoHeight) / 2;

        // 把 Logo 疊加到 QR Code
        Graphics2D g = qrImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(logo, centerX, centerY, logoWidth, logoHeight, null);
        g.dispose();

        // 輸出圖片
        ImageIO.write(qrImage, "PNG", new File(qrPath));
        System.out.println("QR Code with Logo 生成成功，儲存於：" + qrPath);
    }

    public static void main(String[] args) {
        try {
            String qrText = "https://www.example.com";
            String qrPath = "qrcode_with_logo.png";
            String logoPath = "/Users/johnny/AndroidStudioProjects/CiperiumApplication/app/src/main/ic_launcher-playstore.png"; // 確保這個檔案存在

            generateQRCodeWithLogo(qrText, 300, 300, qrPath, logoPath);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }
}


