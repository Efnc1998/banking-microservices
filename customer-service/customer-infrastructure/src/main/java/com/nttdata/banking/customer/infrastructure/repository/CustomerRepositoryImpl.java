package com.nttdata.banking.customer.infrastructure.repository;

import com.nttdata.banking.customer.domain.model.Customer;
import com.nttdata.banking.customer.domain.repository.CustomerRepository;
import com.nttdata.banking.customer.infrastructure.jpa.CustomerJpaRepository;
import com.nttdata.banking.customer.infrastructure.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementation of CustomerRepository using JPA.
 * Wraps blocking JPA calls with Schedulers.boundedElastic() for WebFlux compatibility.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerEntityMapper entityMapper;

    @Override
    public Mono<Customer> save(Customer customer) {
        log.debug("Saving customer with customerId: {}", customer.getCustomerId());
        return Mono.fromCallable(() -> jpaRepository.save(entityMapper.toEntity(customer)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Customer> findByCustomerId(Long customerId) {
        return Mono.fromCallable(() -> jpaRepository.findById(customerId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt
                        .map(entityMapper::toDomain)
                        .map(Mono::just)// Convierte Optional<Customer> → Optional<Mono<Customer>>
                        .orElse(Mono.empty()));
    }

    @Override
    public Flux<Customer> findAll() {
        return Mono.fromCallable(jpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable) // Convierte Mono en Flux
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByCustomerId(Long customerId) {
        log.debug("Soft deleting customer with customerId: {}", customerId);
        return Mono.fromCallable(() -> jpaRepository.findById(customerId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(entity -> {
                    entity.setStatus(false);
                    return Mono.fromCallable(() -> jpaRepository.save(entity))
                            .subscribeOn(Schedulers.boundedElastic());
                }).orElse(Mono.empty()))
                .then(); // return void
    }

    @Override
    public Mono<Boolean> existsByCustomerId(Long customerId) {
        return Mono.fromCallable(() -> jpaRepository.existsById(customerId))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
