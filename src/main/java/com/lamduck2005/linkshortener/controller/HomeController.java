package com.lamduck2005.linkshortener.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Endpoint đơn giản để cron job keep-alive gọi (tránh Render sleep)
     * Không cần authentication, trả về 200 OK
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Service is running"));
    }

//    @GetMapping("/")
//    public String redirectToFrontend() {
//        return "redirect:" + frontendUrl;
//    }
}
