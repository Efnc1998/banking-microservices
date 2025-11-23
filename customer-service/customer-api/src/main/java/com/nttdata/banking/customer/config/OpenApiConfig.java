package com.nttdata.banking.customer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Customer Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Service API")
                        .version("1.0.0")
                        .description("API REST para la gestión de clientes del sistema bancario. " +
                                "Permite crear, consultar, actualizar y eliminar clientes.")
                        .contact(new Contact()
                                .name("Edison Narváez")
                                .email("efnc1726@outlook.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development"),
                        new Server().url("http://customer-service:8081").description("Docker Environment")
                ));
    }
}
