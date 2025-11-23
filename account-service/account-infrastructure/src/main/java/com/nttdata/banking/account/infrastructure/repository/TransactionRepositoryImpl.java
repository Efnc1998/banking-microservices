package com.nttdata.banking.account.infrastructure.repository;

import com.nttdata.banking.account.domain.model.Transaction;
import com.nttdata.banking.account.domain.repository.TransactionRepository;
import com.nttdata.banking.account.infrastructure.entity.TransactionEntity;
import com.nttdata.banking.account.infrastructure.jpa.TransactionJpaRepository;
import com.nttdata.banking.account.infrastructure.mapper.TransactionEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

/**
 * Implementation of TransactionRepository using JPA adapted for WebFlux.
 * Converts between Domain Model and Entity, never exposes Entity outside.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionEntityMapper entityMapper;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return Mono.fromCallable(() -> {
            log.debug("Saving transaction for account: {}", transaction.getAccountId());
            TransactionEntity entity = entityMapper.toEntity(transaction);
            TransactionEntity saved = jpaRepository.save(entity);
            return entityMapper.toDomain(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Transaction> findById(Long id) {
        return Mono.fromCallable(() -> jpaRepository.findById(id)
                        .map(entityMapper::toDomain)
                        .orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Transaction> findByAccountId(Long accountId) {
        return Mono.fromCallable(() -> jpaRepository.findByAccountId(accountId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Flux<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return Mono.fromCallable(() -> jpaRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Flux<Transaction> findAll() {
        return Mono.fromCallable(() -> jpaRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> {
            log.debug("Deleting transaction with id: {}", id);
            jpaRepository.deleteById(id);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Transaction> findLastByAccountId(Long accountId) {
        return Mono.fromCallable(() -> jpaRepository.findLastByAccountId(accountId)
                        .map(entityMapper::toDomain)
                        .orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
