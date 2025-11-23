package com.nttdata.banking.account.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model representing a Transaction (Movement).
 * Pure POJO without any framework dependencies.
 */
@Value
@Builder
public class Transaction {
    Long movementId;
    LocalDateTime date;
    String type;
    BigDecimal amount;
    BigDecimal balance;
    Long accountId;
}
