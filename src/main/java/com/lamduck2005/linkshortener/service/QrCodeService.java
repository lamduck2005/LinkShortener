package com.lamduck2005.linkshortener.service;

import java.io.IOException;

public interface QrCodeService {
    /**
     * Tạo mã QR từ một đoạn text (URL) và trả về dưới dạng Base64.
     * @param text Nội dung (ví dụ: http://localhost:8080/gT)
     * @param width Chiều rộng
     * @param height Chiều cao
     * @return Chuỗi Base64 (ví dụ: "data:image/png;base64,iVBORw0KG...")
     * @throws IOException
     */
    String generateQrCodeBase64(String text, int width, int height);
}