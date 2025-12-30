package com.lamduck2005.linkshortener.dto.response;

import java.time.Instant;

public record ClickAnalyticsResponse(
        Long id,
        Instant clickTime,
        String ipAddress,
        String userAgent
) {
}

