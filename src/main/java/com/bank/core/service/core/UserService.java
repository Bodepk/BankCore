package com.bank.core.service.core;

import com.bank.core.api.dto.request.LoginRequest;
import com.bank.core.api.dto.request.RegisterRequest;
import com.bank.core.api.dto.response.AuthResponse;
import com.bank.core.api.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(String username, RegisterRequest request);
}