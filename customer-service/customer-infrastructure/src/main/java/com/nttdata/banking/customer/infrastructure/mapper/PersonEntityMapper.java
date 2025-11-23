package com.nttdata.banking.customer.infrastructure.mapper;

import com.nttdata.banking.customer.domain.model.Person;
import com.nttdata.banking.customer.infrastructure.entity.PersonEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Person domain model and PersonEntity.
 */
@Component
public class PersonEntityMapper {

    /**
     * Converts a PersonEntity to Person domain model.
     *
     * @param entity the JPA entity
     * @return the domain model
     */
    public Person toDomain(PersonEntity entity) {
        if (entity == null) {
            return null;
        }
        return Person.builder()
                .personId(entity.getPersonId())
                .name(entity.getName())
                .identification(entity.getIdentification())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .build();
    }

    /**
     * Converts a Person domain model to PersonEntity.
     *
     * @param domain the domain model
     * @return the JPA entity
     */
    public PersonEntity toEntity(Person domain) {
        if (domain == null) {
            return null;
        }
        return PersonEntity.builder()
                .personId(domain.getPersonId())
                .name(domain.getName())
                .identification(domain.getIdentification())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .build();
    }
}
