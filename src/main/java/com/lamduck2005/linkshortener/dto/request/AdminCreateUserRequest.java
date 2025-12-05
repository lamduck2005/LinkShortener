package com.lamduck2005.linkshortener.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCreateUserRequest {

    @NotBlank
    @Email
    @Size(max = 255, message = "Email tối đa 255 ký tự")
    private String email;

    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    
    private Boolean isAdmin;

    
    private Boolean isActive;
}


