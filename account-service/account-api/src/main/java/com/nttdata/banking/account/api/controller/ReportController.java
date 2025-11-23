package com.nttdata.banking.account.api.controller;

import com.nttdata.banking.account.application.service.ReportService;
import com.nttdata.banking.account.dto.AccountStatementDto;
import com.nttdata.banking.account.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Report operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{clientId}")
    public Mono<ResponseEntity<Response<List<AccountStatementDto>>>> getAccountStatement(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get account statement for client: {} from {} to {}", clientId, startDate, endDate);
        return reportService.generateAccountStatement(clientId, startDate, endDate)
                .collectList()
                .map(statements -> ResponseEntity.ok(Response.ok(statements)));
    }
}
