package com.bo4um.wordsappback.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration.
 * Access UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${wordsapp.api-docs.version:1.0.0}")
    private String apiVersion;

    @Bean
    public OpenAPI wordsAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WordsApp API")
                        .description("REST API для платформы изучения иностранных слов с AI-компаньоном.\n\n" +
                                "## Возможности:\n" +
                                "- 🤖 AI-объяснения слов через OpenAI\n" +
                                "- 📚 Тесты уровня языка\n" +
                                "- 📊 Трекинг прогресса\n" +
                                "- 🎭 Персонажи-компаньоны\n" +
                                "- 💬 AI диалоги (в разработке)")
                        .version(apiVersion)
                        .contact(new Contact()
                                .name("WordsApp Team")
                                .email("support@bo4um.com"))
                        .license(new License()
                                .name("Proprietary License")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("https://api.wordsapp.com")
                                .description("Production server")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT токен, полученный через /api/auth/login")));
    }
}
