package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Email @Size(max = 255, message = "Email tối đa 255 ký tự") String email,
        @NotBlank @Size(min = 3, max = 100) String username,
        @NotBlank @Size(min = 6, max = 255) String password
) {}
