package com.lamduck2005.linkshortener.service;

import com.lamduck2005.linkshortener.dto.request.LoginRequest;
import com.lamduck2005.linkshortener.dto.request.SignupRequest;
import com.lamduck2005.linkshortener.dto.response.JwtResponse;
import com.lamduck2005.linkshortener.dto.response.SignupResponse;

public interface AuthService {

    JwtResponse login(LoginRequest request);

    SignupResponse signup(SignupRequest request);
}


