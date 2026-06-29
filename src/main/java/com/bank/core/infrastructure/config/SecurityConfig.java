package com.bank.core.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para desarrollo.
 * Permite acceder a todos los endpoints sin autenticación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configuración para desarrollo: deshabilita la seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (solo para desarrollo)
                .csrf(AbstractHttpConfigurer::disable)

                // Permitir todas las solicitudes sin autenticación
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}