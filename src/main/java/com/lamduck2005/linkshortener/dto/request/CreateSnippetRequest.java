package com.lamduck2005.linkshortener.dto.request;

import com.lamduck2005.linkshortener.entity.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.Instant;

@Data // Lombok: Tự tạo Getters, Setters, No-arg constructor...
public class CreateSnippetRequest {

    @NotBlank(message = "Nội dung không được để trống")
    private String content; // Link dài hoặc đoạn text

    @NotNull(message = "Loại nội dung không được để trống")
    private ContentType type;

    // Các trường tùy chọn
    private String customCode;  // Link tùy chỉnh (ví dụ: "su-kien-cua-toi")
    private String password;    // Mật khẩu (sẽ hash sau)
    private Instant expiresAt;   // Thời gian hết hạn (String, sẽ parse sang Instant trong Service)

    // Không nhận userId mà lấy ở JWT
}