package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.dto.request.CreateSnippetRequest;
import com.lamduck2005.linkshortener.dto.response.CreateSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.MySnippetResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.dto.response.SnippetContentResponse;
import com.lamduck2005.linkshortener.entity.ContentType;
import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.mapper.SnippetMapper;
import com.lamduck2005.linkshortener.repository.ClickAnalyticsRepository;
import com.lamduck2005.linkshortener.repository.SnippetRepository;
import com.lamduck2005.linkshortener.service.AnalyticsService;
import com.lamduck2005.linkshortener.service.Base62Service;
import com.lamduck2005.linkshortener.service.QrCodeService;
import com.lamduck2005.linkshortener.service.SnippetService;
import com.lamduck2005.linkshortener.service.UserService;
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
import java.util.Optional;
import java.util.regex.Pattern;

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

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.validation.url-max-length}")
    private int urlMaxLength;

    @Value("${app.validation.text-max-length}")
    private int textMaxLength;

    private static final Pattern URL_REGEX =
            Pattern.compile("^(https?://)?[\\w\\-]{1,}(\\.[\\w\\-]{1,}){1,}[\\w\\-.,@?^=%&:/~+#]*$");

    @Override
    @Transactional
    public SnippetContentResponse getSnippetContent(String shortCode, String rawPassword) {

        // 1. Tìm Snippet
        Optional<Snippet> snippetOptional = snippetRepository.findByShortCode(shortCode);
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
    @Transactional
    public CreateSnippetResponse createSnippet(CreateSnippetRequest request) {
        //valid cơ bản
        String content = request.getContent().trim();
        if (request.getType() == ContentType.URL) {
            // 1. Check URL hợp lệ
            if (!URL_REGEX.matcher(content).matches()) {
                throw new IllegalArgumentException("Nội dung không phải là một URL hợp lệ.");
            }
            // 2. Check độ dài URL
            if (content.length() > urlMaxLength) {
                throw new IllegalArgumentException("URL quá dài, tối đa " + urlMaxLength + " ký tự.");
            }
            if (!content.startsWith("http://") && !content.startsWith("https://")) {
                content = "http://" + content;
            }
        } else if (request.getType() == ContentType.TEXT) {
            // 3. Check độ dài TEXT
            if (content.length() > textMaxLength) {
                throw new IllegalArgumentException("Nội dung text quá dài, tối đa " + textMaxLength + " ký tự.");
            }
        }

        //Tạo snippet mới, gán lại content đã validate
        Snippet newSnippet = snippetMapper.toEntity(request);
        newSnippet.setContentData(content);

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

        //lưu khi có custom code
        if (request.getCustomCode() != null && !request.getCustomCode().isBlank()) {
            try {
                newSnippet = snippetRepository.save(newSnippet); // Chỉ 1 chuyến đi DB
            } catch (DataIntegrityViolationException e) {
                throw new IllegalArgumentException("Mã rút gọn '" + request.getCustomCode() + "' đã được sử dụng!");
            }
        } else {
            //lưu với shortcode null
            Snippet savedInitial = snippetRepository.save(newSnippet);

            //gán shortcode đã tạo rồi lưu lại lần 2
            savedInitial.setShortCode(base62Service.encode(savedInitial.getId()));
            newSnippet = snippetRepository.save(savedInitial);
        }

        //tạo response
        CreateSnippetResponse response = snippetMapper.toResponse(newSnippet);

        //tạo qr code và gán response
        String shortUrl = baseUrl + "/" + newSnippet.getShortCode();
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
            String shortUrl = baseUrl + "/" + snippet.getShortCode();
            boolean hasPassword = snippet.getPasswordHash() != null && !snippet.getPasswordHash().isBlank();
            return new MySnippetResponse(
                    snippet.getId(),
                    snippet.getShortCode(),
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
        User currentUser = userService.getCurrentUser();

        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet không tồn tại."));

        if (snippet.getUser() == null || !currentUser.getId().equals(snippet.getUser().getId())) {
            throw new AccessDeniedException("Bạn không có quyền xóa link của người khác.");
        }

        snippetRepository.delete(snippet);
    }

    @Override
    @Transactional
    public void updateSnippetPassword(Long id, String newPassword) {
        User currentUser = userService.getCurrentUser();

        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet không tồn tại."));

        if (snippet.getUser() == null || !currentUser.getId().equals(snippet.getUser().getId())) {
            throw new AccessDeniedException("Bạn không có quyền sửa mật khẩu của link này.");
        }

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
        User currentUser = userService.getCurrentUser();

        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet không tồn tại."));

        if (snippet.getUser() == null || !currentUser.getId().equals(snippet.getUser().getId())) {
            throw new AccessDeniedException("Bạn không có quyền sửa ngày hết hạn của link này.");
        }

        snippet.setExpiresAt(newExpiresAt);
        snippetRepository.save(snippet);
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