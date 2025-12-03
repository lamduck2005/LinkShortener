package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.dto.response.AdminSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.repository.ClickAnalyticsRepository;
import com.lamduck2005.linkshortener.repository.SnippetRepository;
import com.lamduck2005.linkshortener.service.AdminSnippetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminSnippetServiceImpl implements AdminSnippetService {

    private final SnippetRepository snippetRepository;
    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AdminSnippetResponse> getSnippets(
            Pageable pageable,
            Long userId,
            String shortCode,
            Boolean hasPassword,
            Boolean expired
    ) {
        Specification<Snippet> spec = Specification.where(null);

        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }

        if (shortCode != null && !shortCode.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("shortCode")), "%" + shortCode.toLowerCase() + "%"));
        }

        if (hasPassword != null) {
            if (Boolean.TRUE.equals(hasPassword)) {
                spec = spec.and((root, query, cb) ->
                        cb.isNotNull(root.get("passwordHash")));
            } else {
                spec = spec.and((root, query, cb) ->
                        cb.or(
                                cb.isNull(root.get("passwordHash")),
                                cb.equal(root.get("passwordHash"), "")
                        ));
            }
        }

        if (expired != null) {
            Instant now = Instant.now();
            if (Boolean.TRUE.equals(expired)) {
                spec = spec.and((root, query, cb) ->
                        cb.and(
                                cb.isNotNull(root.get("expiresAt")),
                                cb.lessThan(root.get("expiresAt"), now)
                        ));
            } else {
                spec = spec.and((root, query, cb) ->
                        cb.or(
                                cb.isNull(root.get("expiresAt")),
                                cb.greaterThanOrEqualTo(root.get("expiresAt"), now)
                        ));
            }
        }

        Page<Snippet> page = snippetRepository.findAll(spec, pageable);

        return mapToPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminSnippetResponse getSnippet(Long id) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet không tồn tại."));

        return mapToAdminSnippetResponse(snippet);
    }

    @Override
    @Transactional
    public void deleteSnippet(Long id) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet không tồn tại."));

        snippetRepository.delete(snippet);
    }

    @Override
    @Transactional
    public void updateSnippetExpiry(Long id, Instant newExpiresAt) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet không tồn tại."));

        snippet.setExpiresAt(newExpiresAt);
        snippetRepository.save(snippet);
    }

    @Override
    @Transactional
    public void updateSnippetPassword(Long id, String newPassword) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet không tồn tại."));

        if (newPassword == null || newPassword.isBlank()) {
            snippet.setPasswordHash(null);
        } else {
            snippet.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        snippetRepository.save(snippet);
    }

    private PagedResponse<AdminSnippetResponse> mapToPagedResponse(Page<Snippet> page) {
        return new PagedResponse<>(
                page.map(this::mapToAdminSnippetResponse).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private AdminSnippetResponse mapToAdminSnippetResponse(Snippet snippet) {
        long clickCount = clickAnalyticsRepository.countBySnippetId(snippet.getId());
        String shortUrl = baseUrl + "/" + snippet.getShortCode();
        boolean snippetHasPassword = snippet.getPasswordHash() != null && !snippet.getPasswordHash().isBlank();
        boolean isExpired = snippet.getExpiresAt() != null && Instant.now().isAfter(snippet.getExpiresAt());

        User owner = snippet.getUser();

        Long ownerId = null;
        String ownerUsername = null;
        String ownerEmail = null;
        if (owner != null) {
            ownerId = owner.getId();
            ownerUsername = owner.getUsername();
            ownerEmail = owner.getEmail();
        }

        return new AdminSnippetResponse(
                snippet.getId(),
                snippet.getShortCode(),
                shortUrl,
                snippet.getContentType().name(),
                snippet.getContentData(),
                snippetHasPassword,
                snippet.getCreatedAt(),
                snippet.getExpiresAt(),
                clickCount,
                ownerId,
                ownerUsername,
                ownerEmail,
                isExpired
        );
    }
}


