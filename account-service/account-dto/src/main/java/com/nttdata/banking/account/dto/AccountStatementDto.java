package com.nttdata.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Account Statement (Report) data transfer.
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountStatementDto {

    LocalDateTime date;
    String customerName;
    String accountNumber;
    String accountType;
    BigDecimal initialBalance;
    Boolean status;
    List<TransactionDetailDto> transactions;

    @Value
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TransactionDetailDto {
        LocalDateTime date;
        String type;
        BigDecimal amount;
        BigDecimal balance;
    }
}
