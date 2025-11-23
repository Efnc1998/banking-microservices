package com.nttdata.banking.customer.api.controller;

import com.nttdata.banking.customer.api.exception.ResourceNotFoundException;
import com.nttdata.banking.customer.application.service.CustomerService;
import com.nttdata.banking.customer.dto.CustomerDto;
import com.nttdata.banking.customer.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST Controller for Customer operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public Mono<ResponseEntity<Response<CustomerDto>>> createCustomer(@Valid @RequestBody CustomerDto dto) {
        log.info("REST request to create customer");
        return customerService.createCustomer(dto)
                .map(created -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Response.created(created)));
    }

    @GetMapping("/{customerId}")
    public Mono<ResponseEntity<Response<CustomerDto>>> getCustomerByCustomerId(@PathVariable Long customerId) {
        log.info("REST request to get customer by customerId: {}", customerId);
        return customerService.getCustomerByCustomerId(customerId)
                .map(customer -> ResponseEntity.ok(Response.ok(customer)))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer", customerId)));
    }

    @GetMapping
    public Mono<ResponseEntity<Response<List<CustomerDto>>>> getAllCustomers() {
        log.info("REST request to get all customers");
        return customerService.getAllCustomers()
                .collectList()
                .map(customers -> ResponseEntity.ok(Response.ok(customers)));
    }

    @PutMapping("/{customerId}")
    public Mono<ResponseEntity<Response<CustomerDto>>> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto dto) {
        log.info("REST request to update customer with customerId: {}", customerId);
        return customerService.getCustomerByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer", customerId)))
                .flatMap(existing -> customerService.updateCustomer(customerId, dto))
                .map(updated -> ResponseEntity.ok(Response.updated(updated)));
    }

    @DeleteMapping("/{customerId}")
    public Mono<ResponseEntity<Response<Void>>> deleteCustomer(@PathVariable Long customerId) {
        log.info("REST request to delete customer with customerId: {}", customerId);
        return customerService.getCustomerByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer", customerId)))
                .flatMap(customer -> customerService.deleteCustomer(customerId))
                .then(Mono.just(ResponseEntity.ok(Response.<Void>deleted())));
    }

    @GetMapping("/exists/{customerId}")
    public Mono<ResponseEntity<Response<Boolean>>> existsByCustomerId(@PathVariable Long customerId) {
        log.info("REST request to check if customer exists by customerId: {}", customerId);
        return customerService.existsByCustomerId(customerId)
                .map(exists -> ResponseEntity.ok(Response.ok(exists)));
    }
}
