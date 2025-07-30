package com.skillforge.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI skillForgeOpenAPI() {
        return new OpenAPI()
                // 1) Add JWT Bearer security globally
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                // 2) Top‑level API info
                .info(new Info()
                        .title("SkillForge API")
                        .version("v1")
                        .description("Full‑stack career roadmap & skill‑building platform API")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                        )
                )
                // 3) Link to GitHub, docs, etc.
                .externalDocs(new ExternalDocumentation()
                        .description("Project GitHub")
                        .url("https://github.com/Madinaza/SkillForge_backend")
                );
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("skillforge-all")
                .pathsToMatch("/api/**")
                .build();
    }
}
