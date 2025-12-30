package com.lamduck2005.linkshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickAnalyticsResponse {
    private Long id;
    private Instant clickTime;
    private String ipAddress;
    private String userAgent;
}

