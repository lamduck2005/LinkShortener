package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.entity.ClickAnalytics;
import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.repository.ClickAnalyticsRepository;
import com.lamduck2005.linkshortener.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ClickAnalyticsRepository clickAnalyticsRepository;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW) 
    // Đảm bảo transaction mới cho log click ( dù lỗi gì cũng không ảnh hưởng đến transaction chính)
    public void logClick(Snippet snippet, String ipAddress, String userAgent) {
        if (snippet == null) {
            return;
        }

        try {
            ClickAnalytics analytics = new ClickAnalytics(snippet, ipAddress, userAgent);
            clickAnalyticsRepository.save(analytics);
        } catch (Exception e) {
            log.error("Không thể ghi log click cho snippet id={}: {}", snippet.getId(), e.getMessage());
        }
    }
}


