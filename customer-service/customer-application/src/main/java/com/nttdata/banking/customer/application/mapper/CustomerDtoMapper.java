package com.nttdata.banking.customer.application.mapper;

import com.nttdata.banking.customer.domain.model.Customer;
import com.nttdata.banking.customer.dto.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Customer domain model and CustomerDto.
 */
@Component
@RequiredArgsConstructor
public class CustomerDtoMapper {

    private final PersonDtoMapper personDtoMapper;

    /**
     * Converts a Customer domain model to CustomerDto.
     *
     * @param customer the domain model
     * @return the DTO
     */
    public CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .person(personDtoMapper.toDto(customer.getPerson()))
                .password(customer.getPassword())
                .status(customer.getStatus())
                .build();
    }

    /**
     * Converts a CustomerDto to Customer domain model.
     *
     * @param dto the DTO
     * @return the domain model
     */
    public Customer toDomain(CustomerDto dto) {
        if (dto == null) {
            return null;
        }
        return Customer.builder()
                .customerId(dto.getCustomerId())
                .person(personDtoMapper.toDomain(dto.getPerson()))
                .password(dto.getPassword())
                .status(dto.getStatus() != null ? dto.getStatus() : true)
                .build();
    }
}
