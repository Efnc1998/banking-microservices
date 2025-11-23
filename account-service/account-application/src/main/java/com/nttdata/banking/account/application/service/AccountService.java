package com.nttdata.banking.account.application.service;

import com.nttdata.banking.account.application.exception.BusinessException;
import com.nttdata.banking.account.application.mapper.AccountDtoMapper;
import com.nttdata.banking.account.domain.model.Account;
import com.nttdata.banking.account.domain.repository.AccountRepository;
import com.nttdata.banking.account.dto.AccountDto;
import com.nttdata.banking.customer.client.CustomerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service for Account operations.
 * Handles DTO to Domain transformations and business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private static final String ACCOUNT_TYPE_SAVINGS = "Ahorro";
    private static final String ACCOUNT_TYPE_CHECKING = "Corriente";

    private final AccountRepository accountRepository;
    private final AccountDtoMapper accountDtoMapper;
    private final CustomerClient customerClient;

    /**
     * Creates a new account after validating customer exists.
     *
     * @param dto the account DTO
     * @return the created account DTO
     */
    public Mono<AccountDto> createAccount(AccountDto dto) {
        log.info("Creating new account for customer: {}", dto.getCustomerId());

        if (!isValidAccountType(dto.getAccountType())) {
            return Mono.error(new BusinessException(
                    "Invalid account type: " + dto.getAccountType() + ". Must be 'Ahorro' or 'Corriente'"));
        }

        return customerClient.existsByCustomerId(dto.getCustomerId())
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(new BusinessException("Customer not found with id: " + dto.getCustomerId()));
                    }
                    Account account = accountDtoMapper.toDomain(dto);
                    return accountRepository.save(account);
                })
                .map(accountDtoMapper::toDto)
                .doOnSuccess(a -> log.info("Account created successfully with accountId: {}", a.getAccountId()));
    }

    /**
     * Retrieves an account by account ID.
     *
     * @param accountId the account ID (primary key)
     * @return the account DTO
     */
    public Mono<AccountDto> getAccountById(Long accountId) {
        log.debug("Fetching account by accountId: {}", accountId);
        return accountRepository.findByAccountId(accountId)
                .map(accountDtoMapper::toDto);
    }

    /**
     * Retrieves an account by account number.
     *
     * @param accountNumber the account number
     * @return the account DTO
     */
    public Mono<AccountDto> getAccountByAccountNumber(String accountNumber) {
        log.debug("Fetching account by account number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .map(accountDtoMapper::toDto);
    }

    /**
     * Retrieves all accounts for a customer.
     *
     * @param customerId the customer ID
     * @return flux of account DTOs
     */
    public Flux<AccountDto> getAccountsByCustomerId(Long customerId) {
        log.debug("Fetching accounts for customer: {}", customerId);
        return accountRepository.findByCustomerId(customerId)
                .map(accountDtoMapper::toDto);
    }

    /**
     * Retrieves all accounts.
     *
     * @return flux of account DTOs
     */
    public Flux<AccountDto> getAllAccounts() {
        log.debug("Fetching all accounts");
        return accountRepository.findAll()
                .map(accountDtoMapper::toDto);
    }

    /**
     * Updates an existing account.
     *
     * @param accountId the account ID (primary key)
     * @param dto       the updated account DTO
     * @return the updated account DTO
     */
    public Mono<AccountDto> updateAccount(Long accountId, AccountDto dto) {
        log.info("Updating account with accountId: {}", accountId);

        if (dto.getAccountType() != null && !isValidAccountType(dto.getAccountType())) {
            return Mono.error(new BusinessException(
                    "Invalid account type: " + dto.getAccountType() + ". Must be 'Ahorro' or 'Corriente'"));
        }

        return accountRepository.findByAccountId(accountId)
                .flatMap(existingAccount -> {
                    Account accountToUpdate = Account.builder()
                            .accountId(accountId)
                            .accountNumber(dto.getAccountNumber() != null ? dto.getAccountNumber() : existingAccount.getAccountNumber())
                            .accountType(dto.getAccountType() != null ? dto.getAccountType() : existingAccount.getAccountType())
                            .initialBalance(dto.getInitialBalance() != null ? dto.getInitialBalance() : existingAccount.getInitialBalance())
                            .status(dto.getStatus() != null ? dto.getStatus() : existingAccount.getStatus())
                            .customerId(existingAccount.getCustomerId())
                            .build();
                    return accountRepository.save(accountToUpdate);
                })
                .map(accountDtoMapper::toDto)
                .doOnSuccess(a -> log.info("Account updated successfully with accountId: {}", a.getAccountId()));
    }

    /**
     * Deletes an account by account ID.
     *
     * @param accountId the account ID (primary key)
     * @return void mono
     */
    public Mono<Void> deleteAccount(Long accountId) {
        log.info("Deleting account with accountId: {}", accountId);
        return accountRepository.deleteByAccountId(accountId)
                .doOnSuccess(v -> log.info("Account deleted successfully with accountId: {}", accountId));
    }

    private boolean isValidAccountType(String accountType) {
        return ACCOUNT_TYPE_SAVINGS.equals(accountType) || ACCOUNT_TYPE_CHECKING.equals(accountType);
    }
}
