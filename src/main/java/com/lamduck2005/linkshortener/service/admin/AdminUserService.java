package com.lamduck2005.linkshortener.service.admin;

import com.lamduck2005.linkshortener.config.DefaultUserInitializer;
import com.lamduck2005.linkshortener.constant.ERole;
import com.lamduck2005.linkshortener.dto.request.AdminCreateUserRequest;
import com.lamduck2005.linkshortener.dto.request.AdminUpdateUserRequest;
import com.lamduck2005.linkshortener.dto.response.UserResponse;
import com.lamduck2005.linkshortener.dto.response.PagedResponse;
import com.lamduck2005.linkshortener.entity.Role;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.exception.DuplicateResourceException;
import com.lamduck2005.linkshortener.exception.ResourceNotFoundException;
import com.lamduck2005.linkshortener.repository.RoleRepository;
import com.lamduck2005.linkshortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);

        List<UserResponse> content = page.map(user -> {
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .map(Enum::name)
                    .collect(Collectors.toList());
            // Tạo record mới với tất cả fields
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getIsActive(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    roles
            );
        }).getContent();

        return new PagedResponse<UserResponse>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public UserResponse createUser(AdminCreateUserRequest request) {
        String normalizedUsername = request.username().toLowerCase();

        userRepository.findByUsernameIgnoreCase(normalizedUsername)
                .ifPresent(user -> {
                    throw new DuplicateResourceException("Username đã được sử dụng.");
                });

        userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new DuplicateResourceException("Email đã được sử dụng.");
                });

        User user = new User(
                request.email(),
                normalizedUsername,
                passwordEncoder.encode(request.password()) // Encode password trước khi lưu
        );
        user.setIsActive(request.isActive() == null ? Boolean.TRUE : request.isActive());

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER chưa được cấu hình trong hệ thống."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        if (Boolean.TRUE.equals(request.isAdmin())) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN chưa được cấu hình trong hệ thống."));
            roles.add(adminRole);
        }

        user.setRoles(roles);
        User saved = userRepository.save(user);

        List<String> roleNames = saved.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());
        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getIsActive(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                roleNames
        );
    }

    @Transactional
    public UserResponse updateUser(Long id, AdminUpdateUserRequest request) {
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
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại."));

        // Bảo vệ 2 tài khoản test: admin và user (không thể sửa bất kỳ thông tin nào)
        DefaultUserInitializer.throwIfTestAccount(user.getUsername(), "chỉnh sửa");

        if (request.username() != null && !request.username().equals(user.getUsername())) {
            String normalizedUsername = request.username().toLowerCase();
            userRepository.findByUsernameIgnoreCase(normalizedUsername)
                    .ifPresent(u -> {
                        if (!u.getId().equals(user.getId())) {
                            throw new DuplicateResourceException("Username đã được sử dụng.");
                        }
                    });
            user.setUsername(normalizedUsername);
        }

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            userRepository.findByEmail(request.email())
                    .ifPresent(u -> {
                        if (!u.getId().equals(user.getId())) {
                            throw new DuplicateResourceException("Email đã được sử dụng.");
                        }
                    });
            user.setEmail(request.email());
        }

        if (request.isActive() != null) {
            user.setIsActive(request.isActive());
        }

        if (request.isAdmin() != null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new IllegalStateException("ROLE_USER chưa được cấu hình trong hệ thống."));
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN chưa được cấu hình trong hệ thống."));

            Set<Role> roles = new HashSet<>(user.getRoles());
            // luôn đảm bảo có ROLE_USER
            roles.add(userRole);

            if (Boolean.TRUE.equals(request.isAdmin())) {
                roles.add(adminRole);
            } else {
                roles.remove(adminRole);
            }

            user.setRoles(roles);
        }

        User saved = userRepository.save(user);

        List<String> roleNames = saved.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());
        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getIsActive(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                roleNames
        );
    }
}

