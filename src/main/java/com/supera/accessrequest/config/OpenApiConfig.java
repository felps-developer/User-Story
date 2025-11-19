package com.supera.accessrequest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Solicitação de Acesso a Módulos",
                version = "1.0.0",
                description = "Sistema corporativo para solicitação e gerenciamento de acessos a módulos do sistema",
                contact = @Contact(
                        name = "Supera Tecnologia",
                        email = "contato@supera.com.br"
                )
        ),
        servers = {
                @Server(url = "http://localhost", description = "Nginx Load Balancer"),
                @Server(url = "http://localhost:8081", description = "App Instance 1"),
                @Server(url = "http://localhost:8082", description = "App Instance 2"),
                @Server(url = "http://localhost:8083", description = "App Instance 3")
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}

