package com.lamduck2005.linkshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySnippetResponse {

    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalContent;
    private long clickCount;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean hasPassword;
    private String contentType;
}
