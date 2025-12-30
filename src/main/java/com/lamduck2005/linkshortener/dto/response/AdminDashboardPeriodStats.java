package com.lamduck2005.linkshortener.dto.response;

public record AdminDashboardPeriodStats(
        long newUsers,
        long newSnippets,
        long clicks
) {}


