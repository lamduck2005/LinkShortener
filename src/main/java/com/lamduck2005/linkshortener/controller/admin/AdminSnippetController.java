package com.lamduck2005.linkshortener.controller.admin;

import com.lamduck2005.linkshortener.dto.request.UpdateSnippetExpiryRequest;
import com.lamduck2005.linkshortener.dto.request.UpdateSnippetPasswordRequest;
import com.lamduck2005.linkshortener.dto.response.AdminSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.service.admin.AdminSnippetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/snippets")
public class AdminSnippetController {

    private final AdminSnippetService snippetService;

    @GetMapping
    public ResponseEntity<PagedResponse<AdminSnippetResponse>> getSnippets(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String shortCode,
            @RequestParam(required = false) Boolean hasPassword,
            @RequestParam(required = false) Boolean expired
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        PagedResponse<AdminSnippetResponse> response =
                snippetService.getSnippets(pageable, userId, shortCode, hasPassword, expired);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminSnippetResponse> getSnippet(@PathVariable Long id) {
        AdminSnippetResponse response = snippetService.getSnippet(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable Long id) {
        snippetService.deleteSnippet(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/expires-at")
    public ResponseEntity<Void> updateSnippetExpiry(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSnippetExpiryRequest request
    ) {
        snippetService.updateSnippetExpiry(id, request.newExpiresAt());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updateSnippetPassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSnippetPasswordRequest request
    ) {
        snippetService.updateSnippetPassword(id, request.newPassword());
        return ResponseEntity.noContent().build();
    }
}

