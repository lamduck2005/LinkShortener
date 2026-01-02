package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.config.JwtTokenProvider;
import com.lamduck2005.linkshortener.constant.ERole;
import com.lamduck2005.linkshortener.dto.request.LoginRequest;
import com.lamduck2005.linkshortener.dto.request.SignupRequest;
import com.lamduck2005.linkshortener.dto.response.JwtResponse;
import com.lamduck2005.linkshortener.dto.response.SignupResponse;
import com.lamduck2005.linkshortener.entity.Role;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.exception.DuplicateResourceException;
import com.lamduck2005.linkshortener.repository.RoleRepository;
import com.lamduck2005.linkshortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()));
        } catch (Exception e) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateJwtToken(authentication);

        User user = userRepository.findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> new BadCredentialsException("Không tìm thấy user"));

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, user.getId(), user.getEmail(), user.getUsername(), roles);
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

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
                passwordEncoder.encode(request.password()));
        user.setIsActive(true);

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_USER chưa được cấu hình trong hệ thống."));

        user.setRoles(Collections.singleton(userRole));
        User savedUser = userRepository.save(user);

        return new SignupResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    @Transactional
    public JwtResponse handleOAuth2Login(String email, OAuth2User oauth2User) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    String emailPrefix = extractUsernameFromEmail(email);
                    long timestamp = System.currentTimeMillis();
                    int random = new Random().nextInt(1000, 9999);
                    String username = emailPrefix + timestamp + random;

                    User newUser = new User(
                            email,
                            username.toLowerCase(),
                            passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                    newUser.setIsActive(true);

                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new IllegalStateException(
                                    "ROLE_USER chưa được cấu hình trong hệ thống."));

                    newUser.setRoles(Collections.singleton(userRole));
                    return userRepository.save(newUser);
                });

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());

        String jwt = jwtTokenProvider.generateJwtToken(user.getUsername(), roles);

        return new JwtResponse(jwt, user.getId(), user.getEmail(), user.getUsername(), roles);
    }

    private String extractUsernameFromEmail(String email) {
        return email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}
