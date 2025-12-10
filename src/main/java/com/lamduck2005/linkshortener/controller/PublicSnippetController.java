package com.lamduck2005.linkshortener.controller;

import com.lamduck2005.linkshortener.service.SnippetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicSnippetController {

    private final SnippetService snippetService;

    // Public redirect endpoint: /{shortCode}
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirectToContent(@PathVariable String shortCode) {
        return snippetService.handleSnippetRedirect(shortCode);
    }
}

