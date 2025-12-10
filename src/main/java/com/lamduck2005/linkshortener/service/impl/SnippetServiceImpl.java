package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.dto.request.CreateSnippetRequest;
import com.lamduck2005.linkshortener.dto.response.CreateSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.MySnippetResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.dto.response.SnippetContentResponse;
import com.lamduck2005.linkshortener.entity.ContentType;
import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.exception.DuplicateResourceException;
import com.lamduck2005.linkshortener.exception.ResourceNotFoundException;
import com.lamduck2005.linkshortener.mapper.SnippetMapper;
import com.lamduck2005.linkshortener.repository.ClickAnalyticsRepository;
import com.lamduck2005.linkshortener.repository.SnippetRepository;
import com.lamduck2005.linkshortener.service.AnalyticsService;
import com.lamduck2005.linkshortener.service.Base62Service;
import com.lamduck2005.linkshortener.service.QrCodeService;
import com.lamduck2005.linkshortener.service.SnippetService;
import com.lamduck2005.linkshortener.service.UserService;
import com.lamduck2005.linkshortener.validator.SnippetValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SnippetServiceImpl implements SnippetService {

    private final SnippetRepository snippetRepository;
    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final Base62Service base62Service;
    private final SnippetMapper snippetMapper;
    private final PasswordEncoder passwordEncoder;
    private final QrCodeService qrCodeService;
    private final AnalyticsService analyticsService;
    private final HttpServletRequest httpRequest;
    private final UserService userService;
    private final SnippetValidator snippetValidator;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.validation.url-max-length}")
    private int urlMaxLength;

    @Value("${app.validation.text-max-length}")
    private int textMaxLength;

    @Value("${app.shortcode.prefix:~}")
    private String shortCodePrefix;

    @Override
    @Transactional
    public SnippetContentResponse getSnippetContent(String shortCode, String rawPassword) {

        Optional<Snippet> snippetOptional = resolveSnippetByCode(shortCode);
        if (snippetOptional.isEmpty()) {
            return new SnippetContentResponse(SnippetContentResponse.Status.NOT_FOUND, null, null);
        }

        Snippet snippet = snippetOptional.get();

        // 2. Kiểm tra Hết hạn
        if (snippet.getExpiresAt() != null && Instant.now().isAfter(snippet.getExpiresAt())) {
            return new SnippetContentResponse(SnippetContentResponse.Status.EXPIRED, null, null);
        }

        // 3. Kiểm tra Mật khẩu
        boolean hasPassword = snippet.getPasswordHash() != null && !snippet.getPasswordHash().isBlank();

        if (hasPassword) {
            if (rawPassword == null || rawPassword.isBlank()) {
                // (Luồng GET) -> Yêu cầu mật khẩu
                return new SnippetContentResponse(SnippetContentResponse.Status.PASSWORD_REQUIRED, null, null);
            }

            // (Luồng POST /unlock) -> Kiểm tra mật khẩu
            if (passwordEncoder.matches(rawPassword, snippet.getPasswordHash())) {
                // Đúng mật khẩu -> ghi log click
                logSnippetClick(snippet);
                return new SnippetContentResponse(SnippetContentResponse.Status.OK, snippet.getContentType(), snippet.getContentData());
            } else {
                return new SnippetContentResponse(SnippetContentResponse.Status.WRONG_PASSWORD, null, null);
            }
        }

        // 4. Link công khai, không pass, còn hạn -> ghi log click
        logSnippetClick(snippet);
        return new SnippetContentResponse(SnippetContentResponse.Status.OK, snippet.getContentType(), snippet.getContentData());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> handleSnippetRedirect(String shortCode) {
        SnippetContentResponse response = getSnippetContent(shortCode, null);

        switch (response.getStatus()) {
            case OK:
                if (response.getContentType() == ContentType.URL) {
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .header(HttpHeaders.LOCATION, response.getContent())
                            .build();
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                        .body(response.getContent());

            case PASSWORD_REQUIRED:
                String passwordUrl = frontendUrl + "/unlock/" + shortCode;
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, passwordUrl)
                        .build();

            case EXPIRED:
                String expiredUrl = frontendUrl + "/expired";
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, expiredUrl)
                        .build();

            case NOT_FOUND:
            default:
                String errorUrl = frontendUrl + "/not-found";
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, errorUrl)
                        .build();
        }
    }

    @Override
    @Transactional
    public CreateSnippetResponse createSnippet(CreateSnippetRequest request) {
        // validate & normalize content
        String content = snippetValidator.validateContent(
                request.getContent(),
                request.getType(),
                urlMaxLength,
                textMaxLength
        );

        // Tạo snippet mới, gán lại content đã validate
        Snippet newSnippet = snippetMapper.toEntity(request);
        newSnippet.setContentData(content);

        // Validate custom alias (nếu có)
        String customAlias = snippetValidator.validateCustomAlias(request.getCustomAlias());
        newSnippet.setCustomAlias(customAlias);

        // Nếu request có JWT (user đã login) -> gán user hiện tại cho snippet
        User currentUser = userService.getCurrentUserOrNull();
        if (currentUser != null) {
            newSnippet.setUser(currentUser);
        }

        //hash pass
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            newSnippet.setPasswordHash(hashedPassword);
        }

        // lưu (chỉ 1 query), customAlias đã unique ở DB
        try {
            newSnippet = snippetRepository.save(newSnippet);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Alias đã được sử dụng.");
        }

        //tạo response
        CreateSnippetResponse response = snippetMapper.toResponse(newSnippet);

        String displayCode = buildDisplayCode(newSnippet);
        String shortUrl = baseUrl + "/" + displayCode;
        response.setShortCode(displayCode);
        response.setShortUrl(shortUrl);
        response.setQrCode(qrCodeService.generateQrCodeBase64(shortUrl, 250, 250));


        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MySnippetResponse> getMySnippets(Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        Page<Snippet> page = snippetRepository.findAllByUser(currentUser, pageable);

        List<MySnippetResponse> content = page.map(snippet -> {
            long clickCount = clickAnalyticsRepository.countBySnippetId(snippet.getId());
            String displayCode = buildDisplayCode(snippet);
            String shortUrl = baseUrl + "/" + displayCode;
            boolean hasPassword = snippet.getPasswordHash() != null && !snippet.getPasswordHash().isBlank();
            return new MySnippetResponse(
                    snippet.getId(),
                    displayCode,
                    shortUrl,
                    snippet.getContentData(),
                    clickCount,
                    snippet.getCreatedAt(),
                    snippet.getExpiresAt(),
                    hasPassword,
                    snippet.getContentType().name()
            );
        }).getContent();

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void deleteMySnippet(Long id) {
        Snippet snippet = validateSnippetOwnership(id, "Bạn không có quyền xóa snippet của người khác.");
        snippetRepository.delete(snippet);
    }

    @Override
    @Transactional
    public void updateSnippetPassword(Long id, String newPassword) {
        Snippet snippet = validateSnippetOwnership(id, "Bạn không có quyền sửa mật khẩu của snippet này.");

        if (newPassword == null || newPassword.isBlank()) {
            snippet.setPasswordHash(null);
        } else {
            snippet.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        snippetRepository.save(snippet);
    }

    @Override
    @Transactional
    public void updateSnippetExpiry(Long id, Instant newExpiresAt) {
        Snippet snippet = validateSnippetOwnership(id, "Bạn không có quyền sửa ngày hết hạn của snippet này.");

        snippet.setExpiresAt(newExpiresAt);
        snippetRepository.save(snippet);
    }

    private Snippet validateSnippetOwnership(Long snippetId, String errorMessage) {
        User currentUser = userService.getCurrentUser();

        Snippet snippet = snippetRepository.findById(snippetId)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet không tồn tại."));

        if (snippet.getUser() == null || !currentUser.getId().equals(snippet.getUser().getId())) {
            throw new AccessDeniedException(errorMessage);
        }

        return snippet;
    }

    private Optional<Snippet> resolveSnippetByCode(String code) {
        if (code != null && !code.isBlank() && code.startsWith(shortCodePrefix)) {
            String base62Part = code.substring(shortCodePrefix.length());
            if (base62Part.isEmpty()) {
                return Optional.empty();
            }
            try {
                long id = base62Service.decode(base62Part);
                return snippetRepository.findById(id);
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return snippetRepository.findByCustomAlias(code);
    }

    private String buildDisplayCode(Snippet snippet) {
        if (snippet.getCustomAlias() != null && !snippet.getCustomAlias().isBlank()) {
            return snippet.getCustomAlias();
        }
        return shortCodePrefix + base62Service.encode(snippet.getId());
    }

    private void logSnippetClick(Snippet snippet) {
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        analyticsService.logClick(snippet, ipAddress, userAgent);
    }
}