package com.nttdata.banking.account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Account Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Account Service API")
                        .version("1.0.0")
                        .description("API REST para la gestión de cuentas bancarias, movimientos y reportes. " +
                                "Permite crear cuentas, registrar movimientos (débitos/créditos) y generar estados de cuenta.")
                        .contact(new Contact()
                                .name("Edison Narváez")
                                .email("efnc1726@outlook.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local Development"),
                        new Server().url("http://account-service:8082").description("Docker Environment")
                ));
    }
}
