package com.nttdata.banking.account.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Domain model representing an Account.
 * Pure POJO without any framework dependencies.
 */
@Value
@Builder
public class Account {
    Long accountId;
    String accountNumber;
    String accountType;
    BigDecimal initialBalance;
    Boolean status;
    Long customerId;
}
