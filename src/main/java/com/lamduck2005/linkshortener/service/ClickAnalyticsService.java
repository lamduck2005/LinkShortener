package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.response.ClickAnalyticsResponse;

import java.util.List;

public interface ClickAnalyticsService {
    List<ClickAnalyticsResponse> getAllClicksBySnippetId(Long snippetId);
}
