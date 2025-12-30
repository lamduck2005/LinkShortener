package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AdminUpdateUserRequest(
        @Size(min = 3, max = 100, message = "Username phải từ 3 đến 100 ký tự") String username,
        @Email(message = "Email không hợp lệ") @Size(max = 255, message = "Email tối đa 255 ký tự") String email,
        Boolean isActive,
        Boolean isAdmin
) {}


