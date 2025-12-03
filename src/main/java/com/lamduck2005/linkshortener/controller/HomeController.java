package com.lamduck2005.linkshortener.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

//    @GetMapping("/")
//    public String redirectToFrontend() {
//        return "redirect:" + frontendUrl;
//    }
}
