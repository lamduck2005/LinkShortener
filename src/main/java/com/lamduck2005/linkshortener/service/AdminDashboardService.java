package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.response.AdminDashboardResponse;

public interface AdminDashboardService {

    AdminDashboardResponse getDashboard(Integer days);
}


