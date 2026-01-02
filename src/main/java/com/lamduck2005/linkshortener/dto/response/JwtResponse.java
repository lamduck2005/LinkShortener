package com.lamduck2005.linkshortener.dto.response;

import java.util.List;

public record JwtResponse(
        String token,
        String type,
        UserInfo userInfo
) {
    public record UserInfo(
            Long id,
            String email,
            String username,
            List<String> roles
    ) {}

    public JwtResponse(String token, String type, Long id, String email, String username, List<String> roles) {
        this(token, type, new UserInfo(id, email, username, roles));
    }

    public JwtResponse(String token, Long id, String email, String username, List<String> roles) {
        this(token, "Bearer", id, email, username, roles);
    }
}
