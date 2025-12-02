package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailRequest {

    @NotBlank(message = "Email mới không được để trống")
    @Email(message = "Email mới không hợp lệ")
    private String newEmail;
}


