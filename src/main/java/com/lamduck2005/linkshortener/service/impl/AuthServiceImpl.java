package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.config.jwt.JwtUtils;
import com.lamduck2005.linkshortener.dto.request.LoginRequest;
import com.lamduck2005.linkshortener.dto.request.SignupRequest;
import com.lamduck2005.linkshortener.dto.response.JwtResponse;
import com.lamduck2005.linkshortener.dto.response.SignupResponse;
import com.lamduck2005.linkshortener.entity.ERole;
import com.lamduck2005.linkshortener.entity.Role;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.exception.DuplicateResourceException;
import com.lamduck2005.linkshortener.repository.RoleRepository;
import com.lamduck2005.linkshortener.repository.UserRepository;
import com.lamduck2005.linkshortener.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;

        @Override
        @Transactional(readOnly = true)
        public JwtResponse login(LoginRequest request) {
                Authentication authentication;
                try {
                        authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(),
                                                        request.getPassword()));
                } catch (Exception e) {
                        throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác");
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(auth -> auth.getAuthority())
                                .collect(Collectors.toList());

                return new JwtResponse(jwt, "Bearer", userDetails.getUsername(), roles);
        }

        @Override
        @Transactional
        public SignupResponse signup(SignupRequest request) {

                String normalizedUsername = request.getUsername().toLowerCase();

                userRepository.findByUsernameIgnoreCase(normalizedUsername)
                                .ifPresent(user -> {
                                        throw new DuplicateResourceException("Username đã được sử dụng.");
                                });

                userRepository.findByEmail(request.getEmail())
                                .ifPresent(user -> {
                                        throw new DuplicateResourceException("Email đã được sử dụng.");
                                });

                User user = new User(
                                request.getEmail(),
                                normalizedUsername,
                                passwordEncoder.encode(request.getPassword()));
                user.setIsActive(true);

                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new IllegalStateException(
                                                "ROLE_USER chưa được cấu hình trong hệ thống."));

                user.setRoles(Collections.singleton(userRole));
                User savedUser = userRepository.save(user);

                return new SignupResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
        }
}
