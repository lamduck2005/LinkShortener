package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.dto.response.AdminDashboardPeriod;
import com.lamduck2005.linkshortener.dto.response.AdminDashboardPeriodStats;
import com.lamduck2005.linkshortener.dto.response.AdminDashboardResponse;
import com.lamduck2005.linkshortener.dto.response.AdminDashboardTopSnippetResponse;
import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.repository.ClickAnalyticsRepository;
import com.lamduck2005.linkshortener.repository.SnippetRepository;
import com.lamduck2005.linkshortener.repository.UserRepository;
import com.lamduck2005.linkshortener.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final SnippetRepository snippetRepository;
    private final ClickAnalyticsRepository clickAnalyticsRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard(Integer days) {
        long totalUsers = userRepository.count();
        long totalSnippets = snippetRepository.count();
        long totalClicks = clickAnalyticsRepository.count();

        Instant now = Instant.now();
        Instant from = null;
        Instant to = now;
        Integer effectiveDays = null;

        if (days != null && days > 0) {
            effectiveDays = days;
            from = now.minus(days, ChronoUnit.DAYS);
        }

        long periodNewUsers;
        long periodNewSnippets;
        long periodClicks;

        if (from == null) {
            periodNewUsers = totalUsers;
            periodNewSnippets = totalSnippets;
            periodClicks = totalClicks;
        } else {
            periodNewUsers = userRepository.countByCreatedAtBetween(from, to);
            periodNewSnippets = snippetRepository.countByCreatedAtBetween(from, to);
            periodClicks = clickAnalyticsRepository.countByClickTimeBetween(from, to);
        }

        AdminDashboardPeriod period = new AdminDashboardPeriod(effectiveDays, from, to);
        AdminDashboardPeriodStats periodStats = new AdminDashboardPeriodStats(
                periodNewUsers,
                periodNewSnippets,
                periodClicks
        );

        List<AdminDashboardTopSnippetResponse> topSnippets = loadTopSnippets(from, to);

        AdminDashboardResponse response = new AdminDashboardResponse();
        response.setTotalUsers(totalUsers);
        response.setTotalSnippets(totalSnippets);
        response.setTotalClicks(totalClicks);
        response.setPeriod(period);
        response.setPeriodStats(periodStats);
        response.setTopSnippets(topSnippets);

        return response;
    }

    private List<AdminDashboardTopSnippetResponse> loadTopSnippets(Instant from, Instant to) {
        Pageable pageable = PageRequest.of(0, 20);
        List<ClickAnalyticsRepository.TopSnippetClicksProjection> projections;
        if (from == null) {
            projections = clickAnalyticsRepository.findTopSnippetsAllTime(pageable);
        } else {
            projections = clickAnalyticsRepository.findTopSnippetsInPeriod(from, to, pageable);
        }

        return projections.stream()
                .map(p -> mapToTopSnippetResponse(p.getSnippet(), p.getClickCount()))
                .collect(Collectors.toList());
    }

    private AdminDashboardTopSnippetResponse mapToTopSnippetResponse(Snippet snippet, long clickCount) {
        String shortUrl = baseUrl + "/" + snippet.getShortCode();

        User owner = snippet.getUser();
        Long ownerId = owner != null ? owner.getId() : null;
        String ownerUsername = owner != null ? owner.getUsername() : null;

        boolean isExpired = snippet.getExpiresAt() != null && Instant.now().isAfter(snippet.getExpiresAt());

        return new AdminDashboardTopSnippetResponse(
                snippet.getId(),
                snippet.getShortCode(),
                shortUrl,
                snippet.getContentData(),
                ownerId,
                ownerUsername,
                clickCount,
                snippet.getCreatedAt(),
                snippet.getExpiresAt(),
                isExpired
        );
    }
}


