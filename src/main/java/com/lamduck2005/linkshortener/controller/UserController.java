package com.lamduck2005.linkshortener.controller;

import com.lamduck2005.linkshortener.dto.request.ChangeEmailRequest;
import com.lamduck2005.linkshortener.dto.request.ChangePasswordRequest;
import com.lamduck2005.linkshortener.dto.response.UserProfileResponse;
import com.lamduck2005.linkshortener.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        UserProfileResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/email")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        userService.changeEmail(request);
        return ResponseEntity.noContent().build();
    }
}
