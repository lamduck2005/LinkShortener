package com.lamduck2005.linkshortener.controller;

import com.lamduck2005.linkshortener.dto.request.CreateSnippetRequest;
import com.lamduck2005.linkshortener.dto.request.UnlockSnippetRequest;
import com.lamduck2005.linkshortener.dto.response.CreateSnippetResponse;
import com.lamduck2005.linkshortener.dto.response.SnippetContentResponse;
import com.lamduck2005.linkshortener.entity.ContentType;
import com.lamduck2005.linkshortener.service.SnippetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SnippetController {

    private final SnippetService snippetService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    //lấy liên kết lần đầu
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirectToContent(@PathVariable String shortCode) {
        // Gọi Service (gửi null vì chưa có pass)
        SnippetContentResponse response = snippetService.getSnippetContent(shortCode, null);

        switch (response.getStatus()) {
            case OK:
                // LUỒNG NHANH: Redirect (URL) hoặc Trả về (Text)
                if (response.getContentType() == ContentType.URL) {
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .header(HttpHeaders.LOCATION, response.getContent())
                            .build();
                } else {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                            .body(response.getContent());
                }

            case PASSWORD_REQUIRED:
                String passwordUrl = frontendUrl + "/unlock/" + shortCode; // (Vue Router sẽ bắt /unlock/:code)
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
                // LUỒNG CHUYỂN HƯỚNG SANG FRONTEND (trang 404)
                String errorUrl = frontendUrl + "/not-found"; // (Vue Router sẽ bắt /not-found)
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, errorUrl)
                        .build();
        }
    }

    @PostMapping("/api/v1/snippets")
    public ResponseEntity<CreateSnippetResponse> createSnippet(@Valid @RequestBody CreateSnippetRequest request) {
        CreateSnippetResponse response = snippetService.createSnippet(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/snippets/{shortCode}")
    public ResponseEntity<SnippetContentResponse> getSnippetStatus(@PathVariable String shortCode) {

        // Chỉ gọi service (không cần pass)
        SnippetContentResponse response = snippetService.getSnippetContent(shortCode, null);

        // Trả về DTO (chứa Status) cho Vue.js
        return ResponseEntity.ok(response);
    }


    @PostMapping("/api/v1/unlock")
    public ResponseEntity<SnippetContentResponse> unlockSnippet(@Valid @RequestBody UnlockSnippetRequest request) {

        SnippetContentResponse response = snippetService.getSnippetContent(request.getShortCode(), request.getPassword());

        switch (response.getStatus()) {
            case OK:
                return ResponseEntity.ok(response);

            case WRONG_PASSWORD:
                throw new BadCredentialsException("Mật khẩu không chính xác.");

            default:
               return  ResponseEntity.notFound().build();
        }
    }
}