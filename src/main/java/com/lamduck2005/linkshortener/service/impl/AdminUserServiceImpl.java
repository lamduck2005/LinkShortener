package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.config.DefaultUserInitializer;
import com.lamduck2005.linkshortener.dto.request.AdminCreateUserRequest;
import com.lamduck2005.linkshortener.dto.request.AdminUpdateUserRequest;
import com.lamduck2005.linkshortener.dto.response.AdminUserResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.entity.ERole;
import com.lamduck2005.linkshortener.entity.Role;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.mapper.UserMapper;
import com.lamduck2005.linkshortener.repository.RoleRepository;
import com.lamduck2005.linkshortener.repository.UserRepository;
import com.lamduck2005.linkshortener.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AdminUserResponse> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);

        List<AdminUserResponse> content = page.map(user -> {
            AdminUserResponse dto = userMapper.toAdminUser(user);
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .map(Enum::name)
                    .collect(Collectors.toList());
            dto.setRoles(roles);
            return dto;
        }).getContent();

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional
    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        userRepository.findByUsername(request.getUsername())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username đã được sử dụng.");
                });

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email đã được sử dụng.");
                });

        User user = new User(
                request.getEmail(),
                request.getUsername(),
                request.getPassword() // password đã được hash trước khi lưu, nếu cần có thể encode ở layer khác
        );
        user.setIsActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive());

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER chưa được cấu hình trong hệ thống."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        if (Boolean.TRUE.equals(request.getIsAdmin())) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN chưa được cấu hình trong hệ thống."));
            roles.add(adminRole);
        }

        user.setRoles(roles);
        User saved = userRepository.save(user);

        AdminUserResponse dto = userMapper.toAdminUser(saved);
        List<String> roleNames = saved.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());
        dto.setRoles(roleNames);
        return dto;
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUpdateUserRequest request) {
        // Không cho admin tự sửa chính mình
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String currentUsername;
            if (principal instanceof UserDetails userDetails) {
                currentUsername = userDetails.getUsername();
            } else {
                currentUsername = principal.toString();
            }

            userRepository.findByUsername(currentUsername)
                    .ifPresent(currentUser -> {
                        if (currentUser.getId().equals(id)) {
                            throw new IllegalArgumentException("Admin không được phép sửa chính tài khoản của mình.");
                        }
                    });
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại."));

        // Bảo vệ 2 tài khoản test: admin và user (không thể sửa bất kỳ thông tin nào)
        DefaultUserInitializer.throwIfTestAccount(user.getUsername(), "chỉnh sửa");

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(u -> {
                        if (!u.getId().equals(user.getId())) {
                            throw new IllegalArgumentException("Username đã được sử dụng.");
                        }
                    });
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(u -> {
                        if (!u.getId().equals(user.getId())) {
                            throw new IllegalArgumentException("Email đã được sử dụng.");
                        }
                    });
            user.setEmail(request.getEmail());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        if (request.getIsAdmin() != null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new IllegalStateException("ROLE_USER chưa được cấu hình trong hệ thống."));
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN chưa được cấu hình trong hệ thống."));

            Set<Role> roles = new HashSet<>(user.getRoles());
            // luôn đảm bảo có ROLE_USER
            roles.add(userRole);

            if (Boolean.TRUE.equals(request.getIsAdmin())) {
                roles.add(adminRole);
            } else {
                roles.remove(adminRole);
            }

            user.setRoles(roles);
        }

        User saved = userRepository.save(user);

        AdminUserResponse dto = userMapper.toAdminUser(saved);
        List<String> roleNames = saved.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());
        dto.setRoles(roleNames);
        return dto;
    }
}


