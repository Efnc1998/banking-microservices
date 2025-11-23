package com.nttdata.banking.customer.infrastructure.mapper;

import com.nttdata.banking.customer.domain.model.Customer;
import com.nttdata.banking.customer.infrastructure.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Customer domain model and CustomerEntity.
 */
@Component
@RequiredArgsConstructor
public class CustomerEntityMapper {

    private final PersonEntityMapper personEntityMapper;

    /**
     * Converts a CustomerEntity to Customer domain model.
     *
     * @param entity the JPA entity
     * @return the domain model
     */
    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return Customer.builder()
                .customerId(entity.getCustomerId())
                .person(personEntityMapper.toDomain(entity.getPerson()))
                .password(entity.getPassword())
                .status(entity.getStatus())
                .build();
    }

    /**
     * Converts a Customer domain model to CustomerEntity.
     *
     * @param domain the domain model
     * @return the JPA entity
     */
    public CustomerEntity toEntity(Customer domain) {
        if (domain == null) {
            return null;
        }
        return CustomerEntity.builder()
                .customerId(domain.getCustomerId())
                .person(personEntityMapper.toEntity(domain.getPerson()))
                .password(domain.getPassword())
                .status(domain.getStatus())
                .build();
    }
}
