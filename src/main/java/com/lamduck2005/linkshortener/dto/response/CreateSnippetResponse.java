package com.lamduck2005.linkshortener.dto.response;

import com.lamduck2005.linkshortener.constant.ContentType;
import java.time.Instant;

public record CreateSnippetResponse(
        Long id,
        String shortCode,
        String shortUrl,
        String originalContent,
        ContentType contentType,
        String qrCode,
        Instant createdAt,
        Instant expiresAt
) {}