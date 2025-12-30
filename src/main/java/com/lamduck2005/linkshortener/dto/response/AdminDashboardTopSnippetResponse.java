package com.lamduck2005.linkshortener.dto.response;

import java.time.Instant;

public record AdminDashboardTopSnippetResponse(
        Long id,
        String shortCode,
        String shortUrl,
        String content,
        Long ownerId,
        String ownerUsername,
        long clicks,
        Instant createdAt,
        Instant expiresAt,
        boolean isExpired
) {}


