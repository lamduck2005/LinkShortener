package com.lamduck2005.linkshortener.validator;

import com.lamduck2005.linkshortener.entity.ContentType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SnippetValidator {

    private static final Pattern URL_REGEX =
            Pattern.compile("^(https?://)?[\\w\\-]{1,}(\\.[\\w\\-]{1,}){1,}[\\w\\-.,@?^=%&:/~+#]*$");

    private static final Pattern CUSTOM_ALIAS_REGEX = Pattern.compile("^[A-Za-z0-9_-]{1,100}$");

    /**
     * Validate and normalize content based on type.
     */
    public String validateContent(String rawContent, ContentType type, int urlMaxLength, int textMaxLength) {
        if (rawContent == null) {
            throw new IllegalArgumentException("Nội dung không được để trống.");
        }
        if (type == ContentType.URL) {
            String content = rawContent.trim();
            if (!URL_REGEX.matcher(content).matches()) {
                throw new IllegalArgumentException("Nội dung không phải là một URL hợp lệ.");
            }
            if (content.length() > urlMaxLength) {
                throw new IllegalArgumentException("URL quá dài, tối đa " + urlMaxLength + " ký tự.");
            }
            if (!content.startsWith("http://") && !content.startsWith("https://")) {
                content = "http://" + content;
            }
            return content;
        }

        // TEXT: giữ nguyên để không mất khoảng trắng đầu/cuối
        if (rawContent.length() > textMaxLength) {
            throw new IllegalArgumentException("Nội dung text quá dài, tối đa " + textMaxLength + " ký tự.");
        }
        return rawContent;
    }

    /**
     * Validate custom alias; return normalized alias or null.
     */
    public String validateCustomAlias(String customAlias) {
        if (customAlias == null) {
            return null;
        }
        String normalized = customAlias.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.contains("~")) {
            throw new IllegalArgumentException("Alias không được chứa ký tự '~'.");
        }
        if (!CUSTOM_ALIAS_REGEX.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Alias chỉ được chứa chữ, số, dấu gạch ngang hoặc gạch dưới, độ dài 1-100 ký tự, không có khoảng trắng.");
        }
        return normalized;
    }
}

