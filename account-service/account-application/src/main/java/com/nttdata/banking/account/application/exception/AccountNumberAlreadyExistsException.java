package com.nttdata.banking.account.application.exception;

/**
 * Exception thrown when attempting to create an account with a number that already exists.
 */
public class AccountNumberAlreadyExistsException extends RuntimeException {

    public AccountNumberAlreadyExistsException(String accountNumber) {
        super(String.format("Account with number '%s' already exists", accountNumber));
    }
}
