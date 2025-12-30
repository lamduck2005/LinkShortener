package com.lamduck2005.linkshortener.dto.response;

public record SignupResponse(
        Long id,
        String username,
        String email
) {}

