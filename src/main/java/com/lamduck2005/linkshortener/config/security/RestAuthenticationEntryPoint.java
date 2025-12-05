package com.lamduck2005.linkshortener.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamduck2005.linkshortener.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        // Chưa đăng nhập / thiếu chứng thực (thường là InsufficientAuthenticationException)
        // Token không hợp lệ / đã hết hạn (các AuthenticationException khác)
        String message;
        if (authException instanceof InsufficientAuthenticationException) {
            message = "Bạn cần đăng nhập để sử dụng chức năng này.";
        } else {
            message = "Phiên đăng nhập không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại.";
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


