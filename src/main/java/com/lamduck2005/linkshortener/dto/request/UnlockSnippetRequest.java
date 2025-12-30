package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnlockSnippetRequest(
        @NotBlank @Size(max = 255, message = "Mật khẩu tối đa 255 ký tự") String password
) {}