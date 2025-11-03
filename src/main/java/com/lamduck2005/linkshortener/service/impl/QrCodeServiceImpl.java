package com.lamduck2005.linkshortener.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lamduck2005.linkshortener.service.QrCodeService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Override
    public String generateQrCodeBase64(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            // Ghi ảnh vào một luồng byte trong bộ nhớ (RAM)
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            // Chuyển mảng byte[] thành chuỗi Base64
            byte[] pngData = pngOutputStream.toByteArray();
            String base64String = Base64.getEncoder().encodeToString(pngData);

            // Trả về chuỗi chuẩn Data URL mà <img> của HTML/Vue có thể đọc được
            return "data:image/png;base64," + base64String;

        } catch (WriterException | IOException e) {
            // Nếu có lỗi, log ra và trả về null (hoặc ném exception)
            // (Theo Rule, ném Exception tùy chỉnh sẽ "sạch" hơn)
            System.err.println("Không thể tạo mã QR: " + e.getMessage());
            return null;
        }
    }
}