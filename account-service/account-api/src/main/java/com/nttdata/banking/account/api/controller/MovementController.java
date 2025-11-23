package com.nttdata.banking.account.api.controller;

import com.nttdata.banking.account.api.exception.ResourceNotFoundException;
import com.nttdata.banking.account.application.service.MovementService;
import com.nttdata.banking.account.dto.Response;
import com.nttdata.banking.account.dto.TransactionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST Controller for Movement (Transaction) operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @PostMapping
    public Mono<ResponseEntity<Response<TransactionDto>>> createMovement(@Valid @RequestBody TransactionDto dto) {
        log.info("REST request to create movement");
        return movementService.createMovement(dto)
                .map(created -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Response.created(created)));
    }

    @GetMapping("/{movementId}")
    public Mono<ResponseEntity<Response<TransactionDto>>> getMovementById(@PathVariable Long movementId) {
        log.info("REST request to get movement by movementId: {}", movementId);
        return movementService.getMovementById(movementId)
                .map(movement -> ResponseEntity.ok(Response.ok(movement)))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movement", movementId)));
    }

    @GetMapping("/by-account/{accountId}")
    public Mono<ResponseEntity<Response<List<TransactionDto>>>> getMovementsByAccountId(@PathVariable Long accountId) {
        log.info("REST request to get movements for account: {}", accountId);
        return movementService.getMovementsByAccountId(accountId)
                .collectList()
                .map(movements -> ResponseEntity.ok(Response.ok(movements)));
    }

    @GetMapping
    public Mono<ResponseEntity<Response<List<TransactionDto>>>> getAllMovements() {
        log.info("REST request to get all movements");
        return movementService.getAllMovements()
                .collectList()
                .map(movements -> ResponseEntity.ok(Response.ok(movements)));
    }

    @PutMapping("/{movementId}")
    public Mono<ResponseEntity<Response<TransactionDto>>> updateMovement(
            @PathVariable Long movementId,
            @Valid @RequestBody TransactionDto dto) {
        log.info("REST request to update movement with movementId: {}", movementId);
        return movementService.getMovementById(movementId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movement", movementId)))
                .flatMap(existing -> movementService.updateMovement(movementId, dto))
                .map(updated -> ResponseEntity.ok(Response.updated(updated)));
    }

    @DeleteMapping("/{movementId}")
    public Mono<ResponseEntity<Response<Void>>> deleteMovement(@PathVariable Long movementId) {
        log.info("REST request to delete movement with movementId: {}", movementId);
        return movementService.getMovementById(movementId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movement", movementId)))
                .flatMap(movement -> movementService.deleteMovement(movementId))
                .then(Mono.just(ResponseEntity.ok(Response.<Void>deleted())));
    }
}
