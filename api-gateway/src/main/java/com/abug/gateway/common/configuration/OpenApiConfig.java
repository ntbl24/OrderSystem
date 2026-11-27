package com.abug.gateway.common.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI().info(new Info().title("API-service document")
                .version("v1.0.0").description("description")
                .license(new License().name("API license").url("http://domain.lincense")))
                .servers(List.of(new Server().url("http://localhost:8080/")));
    }
}
