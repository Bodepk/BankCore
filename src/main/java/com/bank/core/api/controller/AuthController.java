package com.bank.core.api.controller;

import com.bank.core.api.dto.request.LoginRequest;
import com.bank.core.api.dto.request.RegisterRequest;
import com.bank.core.api.dto.response.AuthResponse;
import com.bank.core.api.dto.response.UserResponse;
import com.bank.core.service.core.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        AuthResponse response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestParam String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }
}