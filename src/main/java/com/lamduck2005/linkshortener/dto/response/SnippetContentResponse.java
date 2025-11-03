package com.lamduck2005.linkshortener.dto.response;

import com.lamduck2005.linkshortener.entity.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetContentResponse {

    // Enum này là "hợp đồng" giữa Service và Controller
    public enum Status {
        OK,                 // Thành công, trả về nội dung
        PASSWORD_REQUIRED,  // Cần mật khẩu
        WRONG_PASSWORD,     // Sai mật khẩu
        EXPIRED,            // Hết hạn
        NOT_FOUND           // Không tìm thấy
    }

    private Status status;
    private ContentType contentType; // "URL" hoặc "TEXT" (chỉ khi status=OK)
    private String content;     // Link gốc hoặc Text (chỉ khi status=OK)
}