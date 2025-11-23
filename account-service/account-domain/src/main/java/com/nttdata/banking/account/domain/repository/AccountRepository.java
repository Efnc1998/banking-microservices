package com.nttdata.banking.account.domain.repository;

import com.nttdata.banking.account.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Account domain model.
 * Works exclusively with domain models, never with entities.
 */
public interface AccountRepository {

    Mono<Account> save(Account account);

    Mono<Account> findByAccountId(Long accountId);

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByCustomerId(Long customerId);

    Flux<Account> findAll();

    Mono<Void> deleteByAccountId(Long accountId);

    Mono<Boolean> existsByAccountNumber(String accountNumber);
}
