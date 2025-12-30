package com.lamduck2005.linkshortener.dto.response;

import java.util.List;

public record AdminDashboardResponse(
        long totalUsers,
        long totalSnippets,
        long totalClicks,
        AdminDashboardPeriod period,
        AdminDashboardPeriodStats periodStats,
        List<AdminDashboardTopSnippetResponse> topSnippets
) {}


