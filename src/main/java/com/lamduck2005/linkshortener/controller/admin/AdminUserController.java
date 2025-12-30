package com.lamduck2005.linkshortener.controller.admin;

import com.lamduck2005.linkshortener.dto.request.AdminCreateUserRequest;
import com.lamduck2005.linkshortener.dto.request.AdminUpdateUserRequest;
import com.lamduck2005.linkshortener.dto.response.UserResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService userService;

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        PagedResponse<UserResponse> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request
    ) {
        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }
}

