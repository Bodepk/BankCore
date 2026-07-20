package com.bank.core.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger / OpenAPI.
 * Documentación interactiva disponible en /swagger-ui.html
 *
 * Define el esquema de seguridad "Bearer Token" para que se pueda
 * probar cualquier endpoint protegido pegando el JWT en el botón
 * "Authorize" de Swagger UI, sin tener que armar el header a mano.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI bankCoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BankCore API")
                        .description("API REST de un sistema bancario: autenticación con JWT, " +
                                "gestión de cuentas y transacciones (depósitos, retiros y transferencias) " +
                                "con control de acceso por dueño de cuenta y por rol (USER / ADMIN).")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Bode")
                                .url("https://github.com/Bodepk")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Pegá acá el accessToken que te devuelve /api/v1/auth/login " +
                                        "(sin la palabra \"Bearer\", Swagger la agrega sola).")));
    }
}
