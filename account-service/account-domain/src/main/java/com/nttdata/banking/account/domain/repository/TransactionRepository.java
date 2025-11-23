package com.nttdata.banking.account.domain.repository;

import com.nttdata.banking.account.domain.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Repository interface for Transaction domain model.
 * Works exclusively with domain models, never with entities.
 */
public interface TransactionRepository {

    Mono<Transaction> save(Transaction transaction);

    Mono<Transaction> findById(Long id);

    Flux<Transaction> findByAccountId(Long accountId);

    Flux<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    Flux<Transaction> findAll();

    Mono<Void> deleteById(Long id);

    Mono<Transaction> findLastByAccountId(Long accountId);
}
