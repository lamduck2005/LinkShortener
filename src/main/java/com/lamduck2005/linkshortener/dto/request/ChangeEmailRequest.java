package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeEmailRequest(
        @NotBlank(message = "Email mới không được để trống") @Email(message = "Email mới không hợp lệ") @Size(max = 255, message = "Email tối đa 255 ký tự") String newEmail
) {}


