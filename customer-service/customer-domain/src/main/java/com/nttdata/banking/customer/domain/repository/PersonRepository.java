package com.nttdata.banking.customer.domain.repository;

import com.nttdata.banking.customer.domain.model.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Person domain model.
 * Works exclusively with domain models, never with entities.
 */
public interface PersonRepository {

    Mono<Person> save(Person person);

    Mono<Person> findByPersonId(Long personId);

    Mono<Person> findByIdentification(String identification);

    Flux<Person> findAll();

    Mono<Void> deleteByPersonId(Long personId);

    Mono<Boolean> existsByIdentification(String identification);
}
