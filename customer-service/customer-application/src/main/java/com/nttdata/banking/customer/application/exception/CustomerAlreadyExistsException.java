package com.nttdata.banking.customer.application.exception;

/**
 * Exception thrown when attempting to create a customer that already exists.
 */
public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String identification) {
        super(String.format("Customer with identification '%s' already exists", identification));
    }
}
