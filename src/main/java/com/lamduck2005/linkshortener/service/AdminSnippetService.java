package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.response.AdminSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface AdminSnippetService {

    PagedResponse<AdminSnippetResponse> getSnippets(
            Pageable pageable,
            Long userId,
            String shortCode,
            Boolean hasPassword,
            Boolean expired
    );

    AdminSnippetResponse getSnippet(Long id);

    void deleteSnippet(Long id);

    void updateSnippetExpiry(Long id, Instant newExpiresAt);

    void updateSnippetPassword(Long id, String newPassword);
}


