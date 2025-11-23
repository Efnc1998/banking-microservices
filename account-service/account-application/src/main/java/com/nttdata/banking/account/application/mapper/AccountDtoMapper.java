package com.nttdata.banking.account.application.mapper;

import com.nttdata.banking.account.domain.model.Account;
import com.nttdata.banking.account.dto.AccountDto;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Account domain model and AccountDto.
 */
@Component
public class AccountDtoMapper {

    /**
     * Converts an Account domain model to AccountDto.
     *
     * @param account the domain model
     * @return the DTO
     */
    public AccountDto toDto(Account account) {
        if (account == null) {
            return null;
        }
        return AccountDto.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .initialBalance(account.getInitialBalance())
                .status(account.getStatus())
                .customerId(account.getCustomerId())
                .build();
    }

    /**
     * Converts an AccountDto to Account domain model.
     *
     * @param dto the DTO
     * @return the domain model
     */
    public Account toDomain(AccountDto dto) {
        if (dto == null) {
            return null;
        }
        return Account.builder()
                .accountId(dto.getAccountId())
                .accountNumber(dto.getAccountNumber())
                .accountType(dto.getAccountType())
                .initialBalance(dto.getInitialBalance())
                .status(dto.getStatus() != null ? dto.getStatus() : true)
                .customerId(dto.getCustomerId())
                .build();
    }
}
