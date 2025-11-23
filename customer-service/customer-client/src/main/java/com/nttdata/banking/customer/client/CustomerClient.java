package com.nttdata.banking.customer.client;

import com.nttdata.banking.customer.dto.CustomerDto;
import com.nttdata.banking.customer.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * WebClient for communicating with customer-service from other microservices.
 */
@Slf4j
@Component
public class CustomerClient {

    private final WebClient webClient;

    public CustomerClient(@Value("${customer.service.url:http://localhost:8081}") String customerServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    /**
     * Retrieves a customer by customer ID.
     *
     * @param customerId the customer ID
     * @return the customer DTO
     */
    public Mono<CustomerDto> getCustomerByCustomerId(Long customerId) {
        log.debug("Fetching customer from customer-service by customerId: {}", customerId);
        return webClient.get()
                .uri("/api/v1/customers/{customerId}", customerId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Response<CustomerDto>>() {})
                .map(Response::getData)
                .doOnError(error -> log.error("Error fetching customer: {}", error.getMessage()));
    }

    /**
     * Checks if a customer exists by customer ID.
     *
     * @param customerId the customer ID
     * @return true if exists
     */
    public Mono<Boolean> existsByCustomerId(Long customerId) {
        log.debug("Checking if customer exists by customerId: {}", customerId);
        return webClient.get()
                .uri("/api/v1/customers/exists/{customerId}", customerId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Response<Boolean>>() {})
                .map(Response::getData)
                .onErrorReturn(false);
    }
}
