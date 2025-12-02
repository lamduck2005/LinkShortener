package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.request.ChangeEmailRequest;
import com.lamduck2005.linkshortener.dto.request.ChangePasswordRequest;
import com.lamduck2005.linkshortener.dto.response.UserProfileResponse;
import com.lamduck2005.linkshortener.entity.User;

public interface UserService {

    /**
     * Lấy User hiện tại từ SecurityContext.
     * Nếu chưa đăng nhập hoặc anonymous -> ném AccessDeniedException.
     */
    User getCurrentUser();

    /**
     * Phiên bản an toàn cho các API public (cho phép khách vãng lai).
     * Nếu chưa đăng nhập -> trả về null.
     */
    User getCurrentUserOrNull();

    UserProfileResponse getCurrentUserProfile();

    void changePassword(ChangePasswordRequest request);

    void changeEmail(ChangeEmailRequest request);
}
