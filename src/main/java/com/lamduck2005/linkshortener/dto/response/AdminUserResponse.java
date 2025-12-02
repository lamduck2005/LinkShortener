package com.lamduck2005.linkshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> roles;
}


