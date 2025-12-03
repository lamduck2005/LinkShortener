package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.request.CreateSnippetRequest;
import com.lamduck2005.linkshortener.dto.response.CreateSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.MySnippetResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.dto.response.SnippetContentResponse;


import org.springframework.data.domain.Pageable;

public interface SnippetService {
    CreateSnippetResponse createSnippet(CreateSnippetRequest createSnippetRequest);

    SnippetContentResponse getSnippetContent(String shortCode, String rawPassword);

    PagedResponse<MySnippetResponse> getMySnippets(Pageable pageable);

    void deleteMySnippet(Long id);

    void updateSnippetPassword(Long id, String newPassword);

    void updateSnippetExpiry(Long id, java.time.Instant newExpiresAt);
}
