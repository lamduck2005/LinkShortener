package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnlockSnippetRequest {
    @NotBlank
    private String shortCode;
    @NotBlank
    private String password;
}