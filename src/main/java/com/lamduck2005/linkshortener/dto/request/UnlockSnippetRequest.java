package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UnlockSnippetRequest {
    @NotBlank
    private String shortCode;
    @NotBlank
    @Size(max = 255, message = "Mật khẩu tối đa 255 ký tự")
    private String password;
}