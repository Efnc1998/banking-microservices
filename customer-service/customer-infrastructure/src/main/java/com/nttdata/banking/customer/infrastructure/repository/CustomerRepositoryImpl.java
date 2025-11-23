package com.nttdata.banking.customer.infrastructure.repository;

import com.nttdata.banking.customer.domain.model.Customer;
import com.nttdata.banking.customer.domain.repository.CustomerRepository;
import com.nttdata.banking.customer.infrastructure.entity.CustomerEntity;
import com.nttdata.banking.customer.infrastructure.jpa.CustomerJpaRepository;
import com.nttdata.banking.customer.infrastructure.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementation of CustomerRepository using JPA adapted for WebFlux.
 * Converts between Domain Model and Entity, never exposes Entity outside.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerEntityMapper entityMapper;

    @Override
    public Mono<Customer> save(Customer customer) {
        return Mono.fromCallable(() -> {
            log.debug("Saving customer with customerId: {}", customer.getCustomerId());
            CustomerEntity entity = entityMapper.toEntity(customer);
            CustomerEntity saved = jpaRepository.save(entity);
            return entityMapper.toDomain(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> findByCustomerId(Long customerId) {
        return Mono.fromCallable(() -> jpaRepository.findById(customerId)
                        .map(entityMapper::toDomain)
                        .orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Customer> findAll() {
        return Mono.fromCallable(() -> jpaRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByCustomerId(Long customerId) {
        return Mono.fromRunnable(() -> {
            log.debug("Deleting customer with customerId: {}", customerId);
            jpaRepository.deleteById(customerId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Boolean> existsByCustomerId(Long customerId) {
        return Mono.fromCallable(() -> jpaRepository.existsById(customerId))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
