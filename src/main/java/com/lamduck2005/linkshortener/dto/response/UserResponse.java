package com.lamduck2005.linkshortener.dto.response;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        List<String> roles
) {}
