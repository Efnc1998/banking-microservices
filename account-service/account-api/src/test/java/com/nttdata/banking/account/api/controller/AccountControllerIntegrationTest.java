package com.nttdata.banking.account.api.controller;

import com.nttdata.banking.account.application.service.AccountService;
import com.nttdata.banking.account.dto.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration test for AccountController.
 */
@WebFluxTest(AccountController.class)
class AccountControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountService accountService;

    @Test
    @DisplayName("Should return all accounts")
    void shouldReturnAllAccounts() {

        AccountDto account1 = AccountDto.builder()
                .accountId(1L)
                .accountNumber("478758")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("2000.00"))
                .status(true)
                .customerId(1L)
                .build();

        AccountDto account2 = AccountDto.builder()
                .accountId(2L)
                .accountNumber("225487")
                .accountType("Corriente")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .customerId(2L)
                .build();

        when(accountService.getAllAccounts()).thenReturn(Flux.just(account1, account2));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(200)
                .jsonPath("$.status").isEqualTo("Ok")
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].accountNumber").isEqualTo("478758")
                .jsonPath("$.data[1].accountNumber").isEqualTo("225487");
    }

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccountSuccessfully() {
        AccountDto inputDto = AccountDto.builder()
                .accountNumber("123456")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("1000.00"))
                .customerId(1L)
                .build();

        AccountDto savedDto = AccountDto.builder()
                .accountId(1L)
                .accountNumber("123456")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("1000.00"))
                .status(true)
                .customerId(1L)
                .build();

        when(accountService.createAccount(any(AccountDto.class))).thenReturn(Mono.just(savedDto));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inputDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(201)
                .jsonPath("$.status").isEqualTo("Created")
                .jsonPath("$.data.accountId").isEqualTo(1)
                .jsonPath("$.data.accountNumber").isEqualTo("123456");
    }

    @Test
    @DisplayName("Should return account by ID")
    void shouldReturnAccountById() {

        AccountDto account = AccountDto.builder()
                .accountId(1L)
                .accountNumber("478758")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("2000.00"))
                .status(true)
                .customerId(1L)
                .build();

        when(accountService.getAccountById(1L)).thenReturn(Mono.just(account));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/accounts/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(200)
                .jsonPath("$.data.accountId").isEqualTo(1)
                .jsonPath("$.data.accountNumber").isEqualTo("478758")
                .jsonPath("$.data.accountType").isEqualTo("Ahorro");
    }

    @Test
    @DisplayName("Should return 404 when account not found")
    void shouldReturn404WhenAccountNotFound() {

        when(accountService.getAccountById(999L)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/accounts/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(404);
    }
}
