package com.lamduck2005.linkshortener.dto.response;

import java.time.Instant;

public record AdminSnippetResponse(
        Long id,
        String shortCode,
        String shortUrl,
        String contentType,
        String contentData,
        Boolean hasPassword,
        Instant createdAt,
        Instant expiresAt,
        Long clickCount,
        Long ownerId,
        String ownerUsername,
        String ownerEmail,
        Boolean isExpired
) {}


