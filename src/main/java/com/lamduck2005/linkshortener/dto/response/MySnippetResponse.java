package com.lamduck2005.linkshortener.dto.response;

import java.time.Instant;

public record MySnippetResponse(
        Long id,
        String shortCode,
        String shortUrl,
        String originalContent,
        long clickCount,
        Instant createdAt,
        Instant expiresAt,
        boolean hasPassword,
        String contentType
) {}
