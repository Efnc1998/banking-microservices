package com.nttdata.banking.account.infrastructure.mapper;

import com.nttdata.banking.account.domain.model.Account;
import com.nttdata.banking.account.infrastructure.entity.AccountEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Account domain model and AccountEntity.
 */
@Component
public class AccountEntityMapper {

    /**
     * Converts an AccountEntity to Account domain model.
     *
     * @param entity the JPA entity
     * @return the domain model
     */
    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        return Account.builder()
                .accountId(entity.getAccountId())
                .accountNumber(entity.getAccountNumber())
                .accountType(entity.getAccountType())
                .initialBalance(entity.getInitialBalance())
                .status(entity.getStatus())
                .customerId(entity.getCustomerId())
                .build();
    }

    /**
     * Converts an Account domain model to AccountEntity.
     *
     * @param domain the domain model
     * @return the JPA entity
     */
    public AccountEntity toEntity(Account domain) {
        if (domain == null) {
            return null;
        }
        return AccountEntity.builder()
                .accountId(domain.getAccountId())
                .accountNumber(domain.getAccountNumber())
                .accountType(domain.getAccountType())
                .initialBalance(domain.getInitialBalance())
                .status(domain.getStatus())
                .customerId(domain.getCustomerId())
                .build();
    }
}
