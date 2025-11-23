package com.nttdata.banking.account.infrastructure.jpa;

import com.nttdata.banking.account.infrastructure.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for TransactionEntity.
 */
@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByAccountId(Long accountId);

    List<TransactionEntity> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<TransactionEntity> findFirstByAccountIdOrderByDateDescMovementIdDesc(Long accountId);
}
