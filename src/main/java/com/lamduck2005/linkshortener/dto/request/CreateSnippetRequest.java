package com.lamduck2005.linkshortener.dto.request;

import com.lamduck2005.linkshortener.constant.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateSnippetRequest(
        @NotBlank(message = "Nội dung không được để trống") String content,
        @NotNull(message = "Loại nội dung không được để trống") ContentType type,
        String customAlias,  
        @Size(max = 255, message = "Mật khẩu tối đa 255 ký tự") String password,
        Instant expiresAt
) {}