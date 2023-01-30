package com.example.restwithspringbootandjavaerudio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info()
                .title("RESTful API treino com curso")
                .version("v1")
                .description("Descrição teste")
                .termsOfService("link para os termos de serviço")
                .license(new License().name("Apache 2.0")
                        .url("link da licensa")));
    }
}
