package com.nttdata.banking.account.application.mapper;

import com.nttdata.banking.account.domain.model.Transaction;
import com.nttdata.banking.account.dto.TransactionDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between Transaction domain model and TransactionDto.
 */
@Component
public class TransactionDtoMapper {

    /**
     * Converts a Transaction domain model to TransactionDto.
     *
     * @param transaction the domain model
     * @return the DTO
     */
    public TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return TransactionDto.builder()
                .movementId(transaction.getMovementId())
                .date(transaction.getDate())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balance(transaction.getBalance())
                .accountId(transaction.getAccountId())
                .build();
    }

    /**
     * Converts a TransactionDto to Transaction domain model.
     *
     * @param dto the DTO
     * @return the domain model
     */
    public Transaction toDomain(TransactionDto dto) {
        if (dto == null) {
            return null;
        }
        return Transaction.builder()
                .movementId(dto.getMovementId())
                .date(dto.getDate() != null ? dto.getDate() : LocalDateTime.now())
                .type(dto.getType())
                .amount(dto.getAmount())
                .balance(dto.getBalance())
                .accountId(dto.getAccountId())
                .build();
    }
}
