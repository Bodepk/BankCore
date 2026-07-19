package com.bank.core.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración del flujo completo de autenticación:
 * register -> login -> acceder a un endpoint protegido (/me) con el token.
 *
 * Usa el contexto completo de Spring (@SpringBootTest) con el perfil "test"
 * (H2 en memoria, ver application-test.yml), así que ejercita la cadena real
 * de Spring Security, el JwtAuthenticationFilter y la base de datos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> registerBody(String username, String email, String role) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", "Test User");
        body.put("role", role);
        return body;
    }

    @Test
    void registerLoginYAccederAMe_flujoCompleto() throws Exception {
        // 1. Register
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerBody("carla", "carla@bank.com", "USER"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value("carla"));

        // 2. Login
        Map<String, String> loginBody = Map.of("username", "carla", "password", "password123");
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

        // 3. Acceder a /me CON el token -> debe devolver los datos de "carla"
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("carla"))
                .andExpect(jsonPath("$.email").value("carla@bank.com"));
    }

    @Test
    void me_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_conPasswordIncorrecta_devuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerBody("marcos", "marcos@bank.com", "USER"))))
                .andExpect(status().isCreated());

        Map<String, String> badLogin = Map.of("username", "marcos", "password", "contraseñaMala");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_conUsernameDuplicado_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerBody("duplicado", "duplicado1@bank.com", "USER"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerBody("duplicado", "duplicado2@bank.com", "USER"))))
                .andExpect(status().isBadRequest());
    }
}
