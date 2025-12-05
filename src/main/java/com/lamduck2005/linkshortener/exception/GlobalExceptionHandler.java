package com.lamduck2005.linkshortener.exception;

import com.lamduck2005.linkshortener.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus; // Vẫn dùng cách ngắn gọn
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi khi mã tùy chỉnh (customCode) bị trùng.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 1. Set Status Code 409
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {

        final HttpStatus status = HttpStatus.CONFLICT; // Định nghĩa status

        return new ErrorResponse(
                status.value(), // 409
                status.getReasonPhrase(), // "Conflict" (Lấy tự động)
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
    }

    /**
     * Xử lý lỗi validation DTO (khi @Valid thất bại).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 1. Set Status Code 400
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {

        final HttpStatus status = HttpStatus.BAD_REQUEST; // Định nghĩa status

        // tự lấy message lỗi
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ErrorResponse(
                status.value(), // 400
                status.getReasonPhrase(), // "Bad Request" (Lấy tự động)
                message,
                request.getDescription(false).replace("uri=", ""));
    }

    /**
     * BẮT LỖI 400 (JSON SAI ĐỊNH DẠNG / SAI ENUM)
     * Ví dụ: "type": "TEX1T"
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidJsonFormat(HttpMessageNotReadableException ex, WebRequest request) {

        final HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Định dạng JSON không hợp lệ.";

        // Logic "sạch" để lấy thông báo lỗi cụ thể (tùy chọn)
        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            if (cause.getMessage().contains("ContentType")) {
                message = "Giá trị 'type' không hợp lệ. Chỉ chấp nhận 'URL' hoặc 'TEXT'.";
            }
            // (Thêm check cho Instant nếu bạn dùng cách DTO Instant)
        }

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(), // "Bad Request"
                message,
                request.getDescription(false).replace("uri=", ""));
    }

    /**
     * Xử lý lỗi không có api //400
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {

        final HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ErrorResponse(
                status.value(), // 400
                status.getReasonPhrase(), // "Bad Request" (Lấy tự động)
                "Không tìm thấy tài nguyên! Kiểm tra lại đường dẫn API hoặc phương thức (GET, POST, ...)",
                request.getDescription(false).replace("uri=", ""));
    }

    /**
     * Xử lý lỗi xác thực //401
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {

        final HttpStatus status = HttpStatus.UNAUTHORIZED;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInsufficientAuth(InsufficientAuthenticationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
    }

    /**
     * Xử lý lỗi 403 - không đủ quyền.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {

        final HttpStatus status = HttpStatus.FORBIDDEN;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
    }

    /**
     * Xử lý tất cả các lỗi chung khác (lỗi 500).
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 1. Set Status Code 500
    public ErrorResponse handleGenericException(Exception ex, WebRequest request) {

        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Định nghĩa status

        log.error("Unhandled exception occurred: ", ex);

        return new ErrorResponse(
                status.value(), // 500
                status.getReasonPhrase(), // "Internal Server Error" (Lấy tự động)
                "Đã có lỗi xảy ra phía máy chủ.",
                request.getDescription(false).replace("uri=", ""));
    }
}