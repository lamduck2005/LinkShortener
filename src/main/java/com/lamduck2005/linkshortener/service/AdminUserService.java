package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.request.AdminCreateUserRequest;
import com.lamduck2005.linkshortener.dto.request.AdminUpdateUserRequest;
import com.lamduck2005.linkshortener.dto.response.AdminUserResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    PagedResponse<AdminUserResponse> getAllUsers(Pageable pageable);

    AdminUserResponse createUser(AdminCreateUserRequest request);

    AdminUserResponse updateUser(Long id, AdminUpdateUserRequest request);
}


