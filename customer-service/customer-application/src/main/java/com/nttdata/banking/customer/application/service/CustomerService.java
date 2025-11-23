package com.nttdata.banking.customer.application.service;

import com.nttdata.banking.customer.application.mapper.CustomerDtoMapper;
import com.nttdata.banking.customer.domain.model.Customer;
import com.nttdata.banking.customer.domain.model.Person;
import com.nttdata.banking.customer.domain.repository.CustomerRepository;
import com.nttdata.banking.customer.domain.repository.PersonRepository;
import com.nttdata.banking.customer.dto.CustomerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service for Customer operations.
 * Handles DTO to Domain transformations and business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PersonRepository personRepository;
    private final CustomerDtoMapper customerDtoMapper;

    /**
     * Creates a new customer with associated person data.
     *
     * @param dto the customer DTO
     * @return the created customer DTO
     */
    public Mono<CustomerDto> createCustomer(CustomerDto dto) {
        log.info("Creating new customer with identification: {}", dto.getPerson().getIdentification());

        Customer customer = customerDtoMapper.toDomain(dto);
        Person person = customer.getPerson();

        return personRepository.save(person)
                .flatMap(savedPerson -> {
                    Customer customerToSave = Customer.builder()
                            .customerId(dto.getCustomerId())
                            .person(savedPerson)
                            .password(customer.getPassword())
                            .status(customer.getStatus())
                            .build();
                    return customerRepository.save(customerToSave);
                })
                .map(customerDtoMapper::toDto)
                .doOnSuccess(c -> log.info("Customer created successfully with customerId: {}", c.getCustomerId()));
    }

    /**
     * Retrieves a customer by customer ID.
     *
     * @param customerId the customer ID (primary key)
     * @return the customer DTO
     */
    public Mono<CustomerDto> getCustomerByCustomerId(Long customerId) {
        log.debug("Fetching customer by customerId: {}", customerId);
        return customerRepository.findByCustomerId(customerId)
                .map(customerDtoMapper::toDto);
    }

    /**
     * Retrieves all customers.
     *
     * @return flux of customer DTOs
     */
    public Flux<CustomerDto> getAllCustomers() {
        log.debug("Fetching all customers");
        return customerRepository.findAll()
                .map(customerDtoMapper::toDto);
    }

    /**
     * Updates an existing customer.
     *
     * @param customerId the customer ID (primary key)
     * @param dto        the updated customer DTO
     * @return the updated customer DTO
     */
    public Mono<CustomerDto> updateCustomer(Long customerId, CustomerDto dto) {
        log.info("Updating customer with customerId: {}", customerId);

        return customerRepository.findByCustomerId(customerId)
                .flatMap(existingCustomer -> {
                    Person updatedPerson = Person.builder()
                            .personId(existingCustomer.getPerson().getPersonId())
                            .name(dto.getPerson().getName())
                            .identification(dto.getPerson().getIdentification())
                            .address(dto.getPerson().getAddress())
                            .phone(dto.getPerson().getPhone())
                            .build();

                    return personRepository.save(updatedPerson)
                            .flatMap(savedPerson -> {
                                Customer customerToUpdate = Customer.builder()
                                        .customerId(customerId)
                                        .person(savedPerson)
                                        .password(dto.getPassword())
                                        .status(dto.getStatus() != null ? dto.getStatus() : existingCustomer.getStatus())
                                        .build();
                                return customerRepository.save(customerToUpdate);
                            });
                })
                .map(customerDtoMapper::toDto)
                .doOnSuccess(c -> log.info("Customer updated successfully with customerId: {}", c.getCustomerId()));
    }

    /**
     * Deletes a customer by customer ID.
     *
     * @param customerId the customer ID (primary key)
     * @return void mono
     */
    public Mono<Void> deleteCustomer(Long customerId) {
        log.info("Deleting customer with customerId: {}", customerId);
        return customerRepository.findByCustomerId(customerId)
                .flatMap(customer ->
                    customerRepository.deleteByCustomerId(customerId)
                        .then(personRepository.deleteByPersonId(customer.getPerson().getPersonId()))
                )
                .doOnSuccess(v -> log.info("Customer deleted successfully with customerId: {}", customerId));
    }

    /**
     * Checks if a customer exists by customer ID.
     *
     * @param customerId the customer ID
     * @return true if exists
     */
    public Mono<Boolean> existsByCustomerId(Long customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }
}
