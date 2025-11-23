package com.nttdata.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * DTO for Account data transfer.
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {

    Long accountId;

    @NotBlank(message = "Account number is required")
    @Size(max = 20, message = "Account number must not exceed 20 characters")
    String accountNumber;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "^(Ahorro|Corriente)$", message = "Account type must be 'Ahorro' or 'Corriente'")
    String accountType;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.00", message = "Initial balance must be at least 0")
    BigDecimal initialBalance;

    Boolean status;

    @NotNull(message = "Customer ID is required")
    Long customerId;
}
