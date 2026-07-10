package com.bank.core.service.impl;

import com.bank.core.api.dto.request.LoginRequest;
import com.bank.core.api.dto.request.RegisterRequest;
import com.bank.core.api.dto.response.AuthResponse;
import com.bank.core.api.dto.response.UserResponse;
import com.bank.core.domain.enums.Role;
import com.bank.core.domain.exception.UserNotFoundException;
import com.bank.core.domain.model.User;
import com.bank.core.infrastructure.persistence.UserRepository;
import com.bank.core.service.core.JwtService;
import com.bank.core.service.core.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole() != null ? request.getRole() : Role.USER
        );
        user.setFullName(request.getFullName());

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(token, refreshToken, toUserResponse(savedUser));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException(request.getUsername()));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(token, refreshToken, toUserResponse(user));
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            throw new IllegalArgumentException("Token inválido");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        String newToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(newToken, newRefreshToken, toUserResponse(user));
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String username, RegisterRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        return response;
    }
}