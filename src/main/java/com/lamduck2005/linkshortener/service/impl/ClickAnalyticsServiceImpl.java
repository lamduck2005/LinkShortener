package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.dto.response.ClickAnalyticsResponse;
import com.lamduck2005.linkshortener.entity.ClickAnalytics;
import com.lamduck2005.linkshortener.repository.ClickAnalyticsRepository;
import com.lamduck2005.linkshortener.service.ClickAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickAnalyticsServiceImpl implements ClickAnalyticsService {

    private final ClickAnalyticsRepository clickAnalyticsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClickAnalyticsResponse> getAllClicksBySnippetId(Long snippetId) {
        List<ClickAnalytics> clicks = clickAnalyticsRepository
                .findBySnippetIdOrderByClickTimeDesc(snippetId);

        return clicks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ClickAnalyticsResponse toResponse(ClickAnalytics analytics) {
        return new ClickAnalyticsResponse(
                analytics.getId(),
                analytics.getClickTime(),
                analytics.getIpAddress(),
                analytics.getUserAgent()
        );
    }
}