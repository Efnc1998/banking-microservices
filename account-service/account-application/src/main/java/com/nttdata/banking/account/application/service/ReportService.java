package com.nttdata.banking.account.application.service;

import com.nttdata.banking.account.domain.model.Account;
import com.nttdata.banking.account.domain.model.Transaction;
import com.nttdata.banking.account.domain.repository.AccountRepository;
import com.nttdata.banking.account.domain.repository.TransactionRepository;
import com.nttdata.banking.account.dto.AccountStatementDto;
import com.nttdata.banking.customer.client.CustomerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Service for generating reports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerClient customerClient;

    /**
     * Generates account statement report for a customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate  the start date
     * @param endDate    the end date
     * @return flux of account statement DTOs
     */
    public Flux<AccountStatementDto> generateAccountStatement(Long customerId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating account statement for customer: {} from {} to {}", customerId, startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return customerClient.getCustomerByCustomerId(customerId)
                .flatMapMany(customer -> accountRepository.findByCustomerId(customerId)
                        .flatMap(account -> buildAccountStatement(account, customer.getPerson().getName(), startDateTime, endDateTime)));
    }

    private Mono<AccountStatementDto> buildAccountStatement(Account account, String customerName,
                                                             LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return transactionRepository.findByAccountIdAndDateBetween(account.getAccountId(), startDateTime, endDateTime)
                .collectList()
                .map(transactions -> AccountStatementDto.builder()
                        .date(LocalDateTime.now())
                        .customerName(customerName)
                        .accountNumber(account.getAccountNumber())
                        .accountType(account.getAccountType())
                        .initialBalance(account.getInitialBalance())
                        .status(account.getStatus())
                        .transactions(mapTransactions(transactions))
                        .build());
    }

    private List<AccountStatementDto.TransactionDetailDto> mapTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .map(t -> AccountStatementDto.TransactionDetailDto.builder()
                        .date(t.getDate())
                        .type(t.getType())
                        .amount(t.getAmount())
                        .balance(t.getBalance())
                        .build())
                .toList();
    }
}
