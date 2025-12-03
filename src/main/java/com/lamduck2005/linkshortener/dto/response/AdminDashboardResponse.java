package com.lamduck2005.linkshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalSnippets;
    private long totalClicks;

    private AdminDashboardPeriod period;

    private AdminDashboardPeriodStats periodStats;

    private List<AdminDashboardTopSnippetResponse> topSnippets;
}


