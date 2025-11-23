package com.nttdata.banking.account.application.service;

import com.nttdata.banking.account.application.exception.BusinessException;
import com.nttdata.banking.account.application.exception.InsufficientFundsException;
import com.nttdata.banking.account.application.mapper.TransactionDtoMapper;
import com.nttdata.banking.account.domain.model.Account;
import com.nttdata.banking.account.domain.model.Transaction;
import com.nttdata.banking.account.domain.repository.AccountRepository;
import com.nttdata.banking.account.domain.repository.TransactionRepository;
import com.nttdata.banking.account.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Application service for Movement (Transaction) operations.
 * Implements business logic for F2 and F3 requirements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService {

    private static final String DEBIT = "Débito";
    private static final String CREDIT = "Crédito";

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionDtoMapper transactionDtoMapper;

    /**
     * Creates a new transaction (movement) with business logic validation.
     * - Debit subtracts from balance
     * - Credit adds to balance
     * - Validates sufficient funds for debit transactions
     *
     * @param dto the transaction DTO
     * @return the created transaction DTO
     */
    public Mono<TransactionDto> createMovement(TransactionDto dto) {
        log.info("Creating movement for account: {}, type: {}, amount: {}",
                dto.getAccountId(), dto.getType(), dto.getAmount());

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new BusinessException("Amount must be greater than 0"));
        }

        return accountRepository.findByAccountId(dto.getAccountId())
                .switchIfEmpty(Mono.error(new BusinessException("Account not found with id: " + dto.getAccountId())))
                .flatMap(account -> getCurrentBalance(account)
                        .flatMap(currentBalance -> {
                            BigDecimal newBalance = calculateNewBalance(currentBalance, dto.getType(), dto.getAmount());

                            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                                log.warn("Insufficient funds for account: {}, current balance: {}, requested: {}",
                                        account.getAccountNumber(), currentBalance, dto.getAmount());
                                return Mono.error(new InsufficientFundsException());
                            }

                            Transaction transaction = Transaction.builder()
                                    .date(LocalDateTime.now())
                                    .type(dto.getType())
                                    .amount(dto.getAmount())
                                    .balance(newBalance)
                                    .accountId(dto.getAccountId())
                                    .build();

                            return transactionRepository.save(transaction);
                        }))
                .map(transactionDtoMapper::toDto)
                .doOnSuccess(t -> log.info("Movement created successfully with movementId: {}", t.getMovementId()));
    }

    /**
     * Retrieves a transaction by ID.
     *
     * @param id the transaction ID
     * @return the transaction DTO
     */
    public Mono<TransactionDto> getMovementById(Long id) {
        log.debug("Fetching movement by id: {}", id);
        return transactionRepository.findById(id)
                .map(transactionDtoMapper::toDto);
    }

    /**
     * Retrieves all transactions for an account.
     *
     * @param accountId the account ID
     * @return flux of transaction DTOs
     */
    public Flux<TransactionDto> getMovementsByAccountId(Long accountId) {
        log.debug("Fetching movements for account: {}", accountId);
        return transactionRepository.findByAccountId(accountId)
                .map(transactionDtoMapper::toDto);
    }

    /**
     * Retrieves all transactions.
     *
     * @return flux of transaction DTOs
     */
    public Flux<TransactionDto> getAllMovements() {
        log.debug("Fetching all movements");
        return transactionRepository.findAll()
                .map(transactionDtoMapper::toDto);
    }

    /**
     * Updates an existing transaction.
     *
     * @param id  the transaction ID
     * @param dto the updated transaction DTO
     * @return the updated transaction DTO
     */
    public Mono<TransactionDto> updateMovement(Long id, TransactionDto dto) {
        log.info("Updating movement with id: {}", id);

        return transactionRepository.findById(id)
                .flatMap(existingTransaction -> {
                    Transaction transactionToUpdate = Transaction.builder()
                            .movementId(existingTransaction.getMovementId())
                            .date(existingTransaction.getDate())
                            .type(dto.getType() != null ? dto.getType() : existingTransaction.getType())
                            .amount(dto.getAmount() != null ? dto.getAmount() : existingTransaction.getAmount())
                            .balance(existingTransaction.getBalance())
                            .accountId(existingTransaction.getAccountId())
                            .build();
                    return transactionRepository.save(transactionToUpdate);
                })
                .map(transactionDtoMapper::toDto)
                .doOnSuccess(t -> log.info("Movement updated successfully with movementId: {}", t.getMovementId()));
    }

    /**
     * Deletes a transaction by ID.
     *
     * @param id the transaction ID
     * @return void mono
     */
    public Mono<Void> deleteMovement(Long id) {
        log.info("Deleting movement with id: {}", id);
        return transactionRepository.deleteById(id)
                .doOnSuccess(v -> log.info("Movement deleted successfully with id: {}", id));
    }

    /**
     * Gets the current balance for an account.
     * Uses the last transaction balance or initial balance if no transactions exist.
     */
    private Mono<BigDecimal> getCurrentBalance(Account account) {
        return transactionRepository.findLastByAccountId(account.getAccountId())
                .map(Transaction::getBalance)
                .defaultIfEmpty(account.getInitialBalance());
    }

    /**
     * Calculates the new balance based on transaction type.
     * - Debit: subtracts amount from balance
     * - Credit: adds amount to balance
     */
    private BigDecimal calculateNewBalance(BigDecimal currentBalance, String type, BigDecimal amount) {
        if (DEBIT.equals(type)) {
            return currentBalance.subtract(amount);
        } else if (CREDIT.equals(type)) {
            return currentBalance.add(amount);
        }
        throw new BusinessException("Invalid transaction type: " + type);
    }
}
