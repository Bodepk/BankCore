package com.bank.core.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificar el estado de la API
 * Endpoint: GET /api/v1/health
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Verifica que la aplicación esté funcionando
     * @return Map con información del estado
     */
    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "bank-core");
        response.put("version", "0.0.1-SNAPSHOT");
        return response;
    }
}