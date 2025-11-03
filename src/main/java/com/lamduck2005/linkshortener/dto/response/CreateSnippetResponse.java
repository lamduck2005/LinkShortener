package com.lamduck2005.linkshortener.dto.response;

import com.lamduck2005.linkshortener.entity.ContentType;
import lombok.Data;
import java.time.Instant; // Cần import Instant

@Data
public class CreateSnippetResponse {
    private Long id;
    private String shortCode; // Mã sau khi rút gọn
    private String shortUrl; // Base url + shortcode
    private String originalContent;
    private ContentType contentType;
    private String qrCode;

    private Instant createdAt;
    private Instant expiresAt;
}