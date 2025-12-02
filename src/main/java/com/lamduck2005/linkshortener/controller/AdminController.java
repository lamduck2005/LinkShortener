package com.lamduck2005.linkshortener.controller;

import com.lamduck2005.linkshortener.dto.request.AdminCreateUserRequest;
import com.lamduck2005.linkshortener.dto.request.AdminUpdateUserRequest;
import com.lamduck2005.linkshortener.dto.response.AdminUserResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminUserService adminUserService;

    @GetMapping("/users")
    public ResponseEntity<PagedResponse<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        PagedResponse<AdminUserResponse> response = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    public ResponseEntity<AdminUserResponse> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        AdminUserResponse created = adminUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request
    ) {
        AdminUserResponse updated = adminUserService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }
}
