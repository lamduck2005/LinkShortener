package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.dto.request.ChangeEmailRequest;
import com.lamduck2005.linkshortener.dto.request.ChangePasswordRequest;
import com.lamduck2005.linkshortener.dto.response.UserProfileResponse;
import com.lamduck2005.linkshortener.entity.Role;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.mapper.UserMapper;
import com.lamduck2005.linkshortener.repository.UserRepository;
import com.lamduck2005.linkshortener.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new InsufficientAuthenticationException("Bạn chưa đăng nhập hoặc phiên không hợp lệ.");
        }

        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String s) {
            username = s;
        } else {
            throw new AccessDeniedException("Không thể xác định tài khoản hiện tại.");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElseThrow(() ->
                new AccessDeniedException("Tài khoản không tồn tại hoặc đã bị khóa."));
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUserOrNull() {
        try {
            return getCurrentUser();
        } catch (AccessDeniedException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = getCurrentUser();

        UserProfileResponse response = userMapper.toUserProfile(currentUser);

        List<String> roles = currentUser.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());
        response.setRoles(roles);

        return response;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new BadCredentialsException("Mật khẩu hiện tại không chính xác.");
        }

        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void changeEmail(ChangeEmailRequest request) {
        User currentUser = getCurrentUser();

        userRepository.findByEmail(request.getNewEmail())
                .ifPresent(user -> {
                    // Nếu email đã thuộc về user khác
                    if (!user.getId().equals(currentUser.getId())) {
                        throw new IllegalArgumentException("Email đã được sử dụng.");
                    }
                });

        currentUser.setEmail(request.getNewEmail());
        userRepository.save(currentUser);
    }

}
