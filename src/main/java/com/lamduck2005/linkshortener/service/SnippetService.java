package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.request.CreateSnippetRequest;
import com.lamduck2005.linkshortener.dto.response.CreateSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.SnippetContentResponse;
import org.springframework.http.ResponseEntity;

public interface SnippetService {
    public CreateSnippetResponse createSnippet(CreateSnippetRequest createSnippetRequest);

    SnippetContentResponse getSnippetContent(String shortCode, String rawPassword);
}
