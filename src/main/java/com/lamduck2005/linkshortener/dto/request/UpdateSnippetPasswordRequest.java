package com.lamduck2005.linkshortener.dto.request;

import lombok.Data;

@Data
public class UpdateSnippetPasswordRequest {

    /**
     * Mật khẩu mới. Nếu null hoặc rỗng -> xóa mật khẩu (biến snippet thành public).
     */
    private String newPassword;
}


