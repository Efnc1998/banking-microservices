package com.nttdata.banking.customer.infrastructure.repository;

import com.nttdata.banking.customer.domain.model.Person;
import com.nttdata.banking.customer.domain.repository.PersonRepository;
import com.nttdata.banking.customer.infrastructure.entity.PersonEntity;
import com.nttdata.banking.customer.infrastructure.jpa.PersonJpaRepository;
import com.nttdata.banking.customer.infrastructure.mapper.PersonEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementation of PersonRepository using JPA adapted for WebFlux.
 * Converts between Domain Model and Entity, never exposes Entity outside.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {

    private final PersonJpaRepository jpaRepository;
    private final PersonEntityMapper entityMapper;

    @Override
    public Mono<Person> save(Person person) {
        return Mono.fromCallable(() -> {
            log.debug("Saving person with identification: {}", person.getIdentification());
            PersonEntity entity = entityMapper.toEntity(person);
            PersonEntity saved = jpaRepository.save(entity);
            return entityMapper.toDomain(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Person> findByPersonId(Long personId) {
        return Mono.fromCallable(() -> jpaRepository.findById(personId)
                        .map(entityMapper::toDomain)
                        .orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Person> findByIdentification(String identification) {
        return Mono.fromCallable(() -> jpaRepository.findByIdentification(identification)
                        .map(entityMapper::toDomain)
                        .orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Person> findAll() {
        return Mono.fromCallable(jpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(entityMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByPersonId(Long personId) {
        return Mono.fromRunnable(() -> {
            log.debug("Deleting person with personId: {}", personId);
            jpaRepository.deleteById(personId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Boolean> existsByIdentification(String identification) {
        return Mono.fromCallable(() -> jpaRepository.existsByIdentification(identification))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
