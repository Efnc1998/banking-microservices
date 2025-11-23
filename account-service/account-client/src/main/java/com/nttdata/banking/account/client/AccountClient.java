package com.nttdata.banking.account.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient for communicating with account-service from other microservices.
 * This client is available for future inter-service communication needs.
 */
@Slf4j
@Component
public class AccountClient {

    private final WebClient webClient;

    public AccountClient(@Value("${account.service.url:http://localhost:8082}") String accountServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }
}
