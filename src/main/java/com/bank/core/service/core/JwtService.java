package com.bank.core.service.core;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);
}