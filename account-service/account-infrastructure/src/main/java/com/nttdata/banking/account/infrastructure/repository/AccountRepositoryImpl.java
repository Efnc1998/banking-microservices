package com.nttdata.banking.account.infrastructure.repository;

import com.nttdata.banking.account.domain.model.Account;
import com.nttdata.banking.account.domain.repository.AccountRepository;
import com.nttdata.banking.account.infrastructure.jpa.AccountJpaRepository;
import com.nttdata.banking.account.infrastructure.mapper.AccountEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementation of AccountRepository using JPA.
 * Wraps blocking JPA calls with Schedulers.boundedElastic() for WebFlux compatibility.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    private final AccountEntityMapper entityMapper;

    @Override
    public Mono<Account> save(Account account) {
        log.debug("Saving account with number: {}", account.getAccountNumber());
        return Mono.fromCallable(() -> jpaRepository.save(entityMapper.toEntity(account)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Account> findByAccountId(Long accountId) {
        return Mono.fromCallable(() -> jpaRepository.findById(accountId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(entityMapper::toDomain).map(Mono::just).orElse(Mono.empty()));
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return Mono.fromCallable(() -> jpaRepository.findByAccountNumber(accountNumber))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(entityMapper::toDomain).map(Mono::just).orElse(Mono.empty()));
    }

    @Override
    public Flux<Account> findByCustomerId(Long customerId) {
        return Mono.fromCallable(() -> jpaRepository.findByCustomerId(customerId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Flux<Account> findAll() {
        return Mono.fromCallable(jpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByAccountId(Long accountId) {
        log.debug("Deleting account with accountId: {}", accountId);
        return Mono.fromRunnable(() -> jpaRepository.deleteById(accountId))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Boolean> existsByAccountNumber(String accountNumber) {
        return Mono.fromCallable(() -> jpaRepository.existsByAccountNumber(accountNumber))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
