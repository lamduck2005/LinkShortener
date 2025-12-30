package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.dto.response.ClickAnalyticsResponse;
import com.lamduck2005.linkshortener.entity.ClickAnalytics;
import com.lamduck2005.linkshortener.entity.ERole;
import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.exception.ResourceNotFoundException;
import com.lamduck2005.linkshortener.repository.ClickAnalyticsRepository;
import com.lamduck2005.linkshortener.repository.SnippetRepository;
import com.lamduck2005.linkshortener.service.ClickAnalyticsService;
import com.lamduck2005.linkshortener.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickAnalyticsServiceImpl implements ClickAnalyticsService {

    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final SnippetRepository snippetRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<ClickAnalyticsResponse> getAllClicksBySnippetId(Long snippetId) {
        Snippet snippet = snippetRepository.findById(snippetId)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet không tồn tại."));

        User currentUser = userService.getCurrentUser();
        boolean isAdmin = isAdmin(currentUser);

        if (!isAdmin) {
            if (snippet.getUser() == null || !currentUser.getId().equals(snippet.getUser().getId())) {
                throw new AccessDeniedException("Bạn không có quyền xem analytics của snippet này.");
            }
        }

        List<ClickAnalytics> clicks = clickAnalyticsRepository
                .findBySnippetIdOrderByClickTimeDesc(snippetId);

        return clicks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
    }

    private ClickAnalyticsResponse toResponse(ClickAnalytics analytics) {
        return new ClickAnalyticsResponse(
                analytics.getId(),
                analytics.getClickTime(),
                analytics.getIpAddress(),
                analytics.getUserAgent()
        );
    }
}