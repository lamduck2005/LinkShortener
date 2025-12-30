package com.lamduck2005.linkshortener.dto.response;

import java.util.List;

public record JwtResponse(
        String token,
        String type,
        String username,
        List<String> roles
) {
    public JwtResponse(String token, String username, List<String> roles) {
        this(token, "Bearer", username, roles);
    }
}
