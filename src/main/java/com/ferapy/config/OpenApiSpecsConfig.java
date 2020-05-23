package com.ferapy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class OpenApiSpecsConfig {

    private static final License PUBLIC_USE_LICENSE = new License()
            .name("Apache 2.0")
            .url("http://springdoc.org");
    private static final Contact API_ADMIN_CONTACT_INFO = new Contact()
            .name("ferapy.com")
            .url("https://www.ferapy.com")
            .email("info@ferapy.com");
    private AppConfig appConfig;

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .packagesToScan("com.ferapy.security", "com.ferapy.api")
                .setGroup("api")
                .pathsToMatch("/auth/**", "/api/**")
                .build();
    }

    @Bean
    public OpenAPI financingLeadsOpenAPI() {
        return new OpenAPI().components(new Components().addSecuritySchemes("apikey",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                        .title(appConfig.getAppName())
                        .description(appConfig.getAppDescription())
                        .version(appConfig.getAppVersion())
                        .license(PUBLIC_USE_LICENSE)
                        .contact(API_ADMIN_CONTACT_INFO));
    }

}