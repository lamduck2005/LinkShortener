package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.entity.Snippet;

public interface AnalyticsService {

    void logClick(Snippet snippet, String ipAddress, String userAgent);
}


