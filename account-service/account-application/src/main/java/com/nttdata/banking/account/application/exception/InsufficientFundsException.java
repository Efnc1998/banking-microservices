package com.nttdata.banking.account.application.exception;

/**
 * Exception thrown when there are insufficient funds for a transaction.
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Saldo no disponible");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}
