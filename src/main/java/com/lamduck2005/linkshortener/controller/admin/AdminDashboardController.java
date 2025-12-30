package com.lamduck2005.linkshortener.controller.admin;

import com.lamduck2005.linkshortener.dto.response.AdminDashboardResponse;
import com.lamduck2005.linkshortener.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard(
            @RequestParam(required = false) Integer days
    ) {
        AdminDashboardResponse response = dashboardService.getDashboard(days);
        return ResponseEntity.ok(response);
    }
}

