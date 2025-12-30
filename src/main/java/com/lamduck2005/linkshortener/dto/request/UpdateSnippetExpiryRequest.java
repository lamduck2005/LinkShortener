package com.lamduck2005.linkshortener.dto.request;

import java.time.Instant;

public record UpdateSnippetExpiryRequest(
        /**
         * Thời gian hết hạn mới. Nếu null -> bỏ hết hạn (vĩnh viễn).
         */
        Instant newExpiresAt
) {}


