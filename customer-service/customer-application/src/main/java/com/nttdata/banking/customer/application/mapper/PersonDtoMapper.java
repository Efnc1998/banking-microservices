package com.nttdata.banking.customer.application.mapper;

import com.nttdata.banking.customer.domain.model.Person;
import com.nttdata.banking.customer.dto.PersonDto;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Person domain model and PersonDto.
 */
@Component
public class PersonDtoMapper {

    /**
     * Converts a Person domain model to PersonDto.
     *
     * @param person the domain model
     * @return the DTO
     */
    public PersonDto toDto(Person person) {
        if (person == null) {
            return null;
        }
        return PersonDto.builder()
                .personId(person.getPersonId())
                .name(person.getName())
                .identification(person.getIdentification())
                .address(person.getAddress())
                .phone(person.getPhone())
                .build();
    }

    /**
     * Converts a PersonDto to Person domain model.
     *
     * @param dto the DTO
     * @return the domain model
     */
    public Person toDomain(PersonDto dto) {
        if (dto == null) {
            return null;
        }
        return Person.builder()
                .personId(dto.getPersonId())
                .name(dto.getName())
                .identification(dto.getIdentification())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .build();
    }
}
