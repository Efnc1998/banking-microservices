package com.nttdata.banking.account.infrastructure.mapper;

import com.nttdata.banking.account.domain.model.Transaction;
import com.nttdata.banking.account.infrastructure.entity.TransactionEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Transaction domain model and TransactionEntity.
 */
@Component
public class TransactionEntityMapper {

    /**
     * Converts a TransactionEntity to Transaction domain model.
     *
     * @param entity the JPA entity
     * @return the domain model
     */
    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Transaction.builder()
                .movementId(entity.getMovementId())
                .date(entity.getDate())
                .type(entity.getType())
                .amount(entity.getAmount())
                .balance(entity.getBalance())
                .accountId(entity.getAccountId())
                .build();
    }

    /**
     * Converts a Transaction domain model to TransactionEntity.
     *
     * @param domain the domain model
     * @return the JPA entity
     */
    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null) {
            return null;
        }
        return TransactionEntity.builder()
                .movementId(domain.getMovementId())
                .date(domain.getDate())
                .type(domain.getType())
                .amount(domain.getAmount())
                .balance(domain.getBalance())
                .accountId(domain.getAccountId())
                .build();
    }
}
