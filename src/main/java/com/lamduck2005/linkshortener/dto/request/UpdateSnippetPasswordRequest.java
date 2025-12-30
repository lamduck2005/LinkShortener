package com.lamduck2005.linkshortener.dto.request;

public record UpdateSnippetPasswordRequest(
        /**
         * Mật khẩu mới. Nếu null hoặc rỗng -> xóa mật khẩu (biến snippet thành public).
         */
        String newPassword
) {}


