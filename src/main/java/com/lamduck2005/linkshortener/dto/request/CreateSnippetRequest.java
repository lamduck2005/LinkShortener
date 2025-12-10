package com.lamduck2005.linkshortener.dto.request;

import com.lamduck2005.linkshortener.entity.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.Instant;

@Data // Lombok: Tự tạo Getters, Setters, No-arg constructor...
public class CreateSnippetRequest {

    @NotBlank(message = "Nội dung không được để trống")
    private String content; // Link dài hoặc đoạn text

    @NotNull(message = "Loại nội dung không được để trống")
    private ContentType type;

    // Alias tùy chọn (cấm ký tự '~', chỉ cho a-zA-Z0-9_-)
    @Pattern(
            regexp = "^$|^[A-Za-z0-9_-]{1,100}$",
            message = "Alias chỉ được chứa chữ, số, dấu gạch ngang hoặc gạch dưới, độ dài 1-100 ký tự, không có khoảng trắng, không chứa '~'."
    )
    private String customAlias;  // Link tùy chỉnh (ví dụ: "su-kien-cua-toi")

    @Size(max = 255, message = "Mật khẩu tối đa 255 ký tự")
    private String password;    // Mật khẩu (sẽ hash sau)
    private Instant expiresAt;   // Thời gian hết hạn (String, sẽ parse sang Instant trong Service)

    // Không nhận userId mà lấy ở JWT
}