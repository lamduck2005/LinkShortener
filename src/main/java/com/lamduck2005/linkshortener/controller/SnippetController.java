package com.lamduck2005.linkshortener.controller;

import com.lamduck2005.linkshortener.dto.request.CreateSnippetRequest;
import com.lamduck2005.linkshortener.dto.request.UnlockSnippetRequest;
import com.lamduck2005.linkshortener.dto.request.UpdateSnippetExpiryRequest;
import com.lamduck2005.linkshortener.dto.request.UpdateSnippetPasswordRequest;
import com.lamduck2005.linkshortener.dto.response.CreateSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.MySnippetResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.dto.response.SnippetContentResponse;
import com.lamduck2005.linkshortener.service.SnippetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    @PostMapping
    public ResponseEntity<CreateSnippetResponse> createSnippet(@Valid @RequestBody CreateSnippetRequest request) {
        CreateSnippetResponse response = snippetService.createSnippet(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<PagedResponse<MySnippetResponse>> getMySnippets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        PagedResponse<MySnippetResponse> snippets = snippetService.getMySnippets(pageable);
        return ResponseEntity.ok(snippets);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<SnippetContentResponse> getSnippetStatus(@PathVariable String shortCode) {

        // Chỉ gọi service (không cần pass)
        SnippetContentResponse response = snippetService.getSnippetContent(shortCode, null);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMySnippet(@PathVariable Long id) {
        snippetService.deleteMySnippet(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updateSnippetPassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSnippetPasswordRequest request
    ) {
        snippetService.updateSnippetPassword(id, request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/expires-at")
    public ResponseEntity<Void> updateSnippetExpiry(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSnippetExpiryRequest request
    ) {
        snippetService.updateSnippetExpiry(id, request.getNewExpiresAt());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{shortCode}/unlock")
    public ResponseEntity<SnippetContentResponse> unlockSnippet(
            @PathVariable String shortCode,
            @Valid @RequestBody UnlockSnippetRequest request) {

        SnippetContentResponse response = snippetService.getSnippetContent(shortCode, request.getPassword());

        switch (response.getStatus()) {
            case OK:
                return ResponseEntity.ok(response);

            case WRONG_PASSWORD:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

            default:
               return  ResponseEntity.notFound().build();
        }
    }
}