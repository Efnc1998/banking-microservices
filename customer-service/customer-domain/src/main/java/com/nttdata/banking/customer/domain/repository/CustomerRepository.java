package com.nttdata.banking.customer.domain.repository;

import com.nttdata.banking.customer.domain.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Customer domain model.
 * Works exclusively with domain models, never with entities.
 */
public interface CustomerRepository {

    Mono<Customer> save(Customer customer);

    Mono<Customer> findByCustomerId(Long customerId);

    Flux<Customer> findAll();

    Mono<Void> deleteByCustomerId(Long customerId);

    Mono<Boolean> existsByCustomerId(Long customerId);
}
