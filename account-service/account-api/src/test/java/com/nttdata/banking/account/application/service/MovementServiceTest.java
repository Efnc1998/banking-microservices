package com.nttdata.banking.account.application.service;

import com.nttdata.banking.account.application.exception.InsufficientFundsException;
import com.nttdata.banking.account.application.mapper.TransactionDtoMapper;
import com.nttdata.banking.account.domain.model.Account;
import com.nttdata.banking.account.domain.model.Transaction;
import com.nttdata.banking.account.domain.repository.AccountRepository;
import com.nttdata.banking.account.domain.repository.TransactionRepository;
import com.nttdata.banking.account.dto.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MovementService.
 */
@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionDtoMapper transactionDtoMapper;

    @InjectMocks
    private MovementService movementService;

    private Account testAccount;
    private TransactionDto debitDto;
    private TransactionDto creditDto;
    private Transaction savedTransaction;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .accountId(1L)
                .accountNumber("478758")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("2000.00"))
                .status(true)
                .customerId(1L)
                .build();

        debitDto = TransactionDto.builder()
                .type("Débito")
                .amount(new BigDecimal("575.00"))
                .accountId(1L)
                .build();

        creditDto = TransactionDto.builder()
                .type("Crédito")
                .amount(new BigDecimal("500.00"))
                .accountId(1L)
                .build();

        savedTransaction = Transaction.builder()
                .movementId(1L)
                .date(LocalDateTime.now())
                .type("Débito")
                .amount(new BigDecimal("575.00"))
                .balance(new BigDecimal("1425.00"))
                .accountId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create debit transaction and subtract from balance")
    void shouldCreateDebitTransactionAndSubtractFromBalance() {
        // Given
        when(accountRepository.findByAccountId(1L)).thenReturn(Mono.just(testAccount));
        when(transactionRepository.findLastByAccountId(1L)).thenReturn(Mono.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(savedTransaction));
        when(transactionDtoMapper.toDto(savedTransaction)).thenReturn(TransactionDto.builder()
                .movementId(1L)
                .type("Débito")
                .amount(new BigDecimal("575.00"))
                .balance(new BigDecimal("1425.00"))
                .accountId(1L)
                .build());

        // When & Then
        StepVerifier.create(movementService.createMovement(debitDto))
                .expectNextMatches(result ->
                        result.getBalance().compareTo(new BigDecimal("1425.00")) == 0 &&
                        "Débito".equals(result.getType()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when debit exceeds balance")
    void shouldThrowInsufficientFundsExceptionWhenDebitExceedsBalance() {
        // Given
        TransactionDto largeDebitDto = TransactionDto.builder()
                .type("Débito")
                .amount(new BigDecimal("3000.00"))
                .accountId(1L)
                .build();

        when(accountRepository.findByAccountId(1L)).thenReturn(Mono.just(testAccount));
        when(transactionRepository.findLastByAccountId(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(movementService.createMovement(largeDebitDto))
                .expectError(InsufficientFundsException.class)
                .verify();
    }
}
