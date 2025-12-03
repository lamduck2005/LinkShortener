package com.lamduck2005.linkshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSnippetResponse {

    private Long id;
    private String shortCode;
    private String shortUrl;
    private String contentType;
    private String contentData;
    private Boolean hasPassword;
    private Instant createdAt;
    private Instant expiresAt;
    private Long clickCount;

    private Long ownerId;
    private String ownerUsername;
    private String ownerEmail;

    private Boolean isExpired;
}


