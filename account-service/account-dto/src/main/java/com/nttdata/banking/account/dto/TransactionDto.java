package com.nttdata.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Transaction (Movement) data transfer.
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

    Long movementId;

    LocalDateTime date;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "^(Débito|Crédito)$", message = "Transaction type must be 'Débito' or 'Crédito'")
    String type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    BigDecimal amount;

    BigDecimal balance;

    @NotNull(message = "Account ID is required")
    Long accountId;
}
