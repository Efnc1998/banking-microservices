package com.nttdata.banking.customer.domain.model;

import lombok.Builder;
import lombok.Value;

/**
 * Domain model representing a Customer.
 * Extends Person through composition.
 * Pure POJO without any framework dependencies.
 */
@Value
@Builder
public class Customer {
    Long customerId;
    Person person;
    String password;
    Boolean status;
}
