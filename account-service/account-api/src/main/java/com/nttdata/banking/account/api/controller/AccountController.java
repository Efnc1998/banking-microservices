package com.nttdata.banking.account.api.controller;

import com.nttdata.banking.account.api.exception.ResourceNotFoundException;
import com.nttdata.banking.account.application.service.AccountService;
import com.nttdata.banking.account.dto.AccountDto;
import com.nttdata.banking.account.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST Controller for Account operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public Mono<ResponseEntity<Response<AccountDto>>> createAccount(@Valid @RequestBody AccountDto dto) {
        log.info("REST request to create account");
        return accountService.createAccount(dto)
                .map(created -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Response.created(created)));
    }

    @GetMapping("/{accountId}")
    public Mono<ResponseEntity<Response<AccountDto>>> getAccountById(@PathVariable Long accountId) {
        log.info("REST request to get account by accountId: {}", accountId);
        return accountService.getAccountById(accountId)
                .map(account -> ResponseEntity.ok(Response.ok(account)))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account", accountId)));
    }

    @GetMapping("/by-number/{accountNumber}")
    public Mono<ResponseEntity<Response<AccountDto>>> getAccountByNumber(@PathVariable String accountNumber) {
        log.info("REST request to get account by number: {}", accountNumber);
        return accountService.getAccountByAccountNumber(accountNumber)
                .map(account -> ResponseEntity.ok(Response.ok(account)))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found with number: " + accountNumber)));
    }

    @GetMapping("/by-customer/{customerId}")
    public Mono<ResponseEntity<Response<List<AccountDto>>>> getAccountsByCustomerId(@PathVariable Long customerId) {
        log.info("REST request to get accounts for customer: {}", customerId);
        return accountService.getAccountsByCustomerId(customerId)
                .collectList()
                .map(accounts -> ResponseEntity.ok(Response.ok(accounts)));
    }

    @GetMapping
    public Mono<ResponseEntity<Response<List<AccountDto>>>> getAllAccounts() {
        log.info("REST request to get all accounts");
        return accountService.getAllAccounts()
                .collectList()
                .map(accounts -> ResponseEntity.ok(Response.ok(accounts)));
    }

    @PutMapping("/{accountId}")
    public Mono<ResponseEntity<Response<AccountDto>>> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountDto dto) {
        log.info("REST request to update account with accountId: {}", accountId);
        return accountService.getAccountById(accountId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account", accountId)))
                .flatMap(existing -> accountService.updateAccount(accountId, dto))
                .map(updated -> ResponseEntity.ok(Response.updated(updated)));
    }

    @DeleteMapping("/{accountId}")
    public Mono<ResponseEntity<Response<Void>>> deleteAccount(@PathVariable Long accountId) {
        log.info("REST request to delete account with accountId: {}", accountId);
        return accountService.getAccountById(accountId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account", accountId)))
                .flatMap(account -> accountService.deleteAccount(accountId))
                .then(Mono.just(ResponseEntity.ok(Response.<Void>deleted())));
    }
}
