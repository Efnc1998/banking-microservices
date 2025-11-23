package com.nttdata.banking.account.infrastructure.repository;

import com.nttdata.banking.account.domain.model.Transaction;
import com.nttdata.banking.account.domain.repository.TransactionRepository;
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
 * Implementation of TransactionRepository using JPA.
 * Wraps blocking JPA calls with Schedulers.boundedElastic() for WebFlux compatibility.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionEntityMapper entityMapper;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        log.debug("Saving transaction for account: {}", transaction.getAccountId());
        return Mono.fromCallable(() -> jpaRepository.save(entityMapper.toEntity(transaction)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Transaction> findById(Long id) {
        return Mono.fromCallable(() -> jpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(entityMapper::toDomain).map(Mono::just).orElse(Mono.empty()));
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
        return Mono.fromCallable(jpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Deleting transaction with id: {}", id);
        return Mono.fromRunnable(() -> jpaRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Transaction> findLastByAccountId(Long accountId) {
        return Mono.fromCallable(() -> jpaRepository.findFirstByAccountIdOrderByDateDescMovementIdDesc(accountId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(entityMapper::toDomain).map(Mono::just).orElse(Mono.empty()));
    }
}
