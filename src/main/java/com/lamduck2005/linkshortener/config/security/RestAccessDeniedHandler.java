package com.lamduck2005.linkshortener.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamduck2005.linkshortener.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        HttpStatus status = HttpStatus.FORBIDDEN;

        String message = accessDeniedException.getMessage();
        if (message == null || message.isBlank()) {
            message = "Bạn không có quyền truy cập tài nguyên này.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}


