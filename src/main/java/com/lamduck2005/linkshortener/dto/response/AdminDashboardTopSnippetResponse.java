package com.lamduck2005.linkshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardTopSnippetResponse {

    private Long id;
    private String shortCode;
    private String shortUrl;
    private String content;

    private Long ownerId;
    private String ownerUsername;

    private long clicks;

    private Instant createdAt;
    private Instant expiresAt;
    private boolean isExpired;
}


