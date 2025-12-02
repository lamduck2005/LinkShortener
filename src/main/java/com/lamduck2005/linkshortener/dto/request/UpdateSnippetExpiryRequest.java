package com.lamduck2005.linkshortener.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class UpdateSnippetExpiryRequest {

    /**
     * Thời gian hết hạn mới. Nếu null -> bỏ hết hạn (vĩnh viễn).
     */
    private Instant newExpiresAt;
}


