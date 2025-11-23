package com.nttdata.banking.account.infrastructure.jpa;

import com.nttdata.banking.account.infrastructure.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId ORDER BY t.date DESC, t.id DESC LIMIT 1")
    Optional<TransactionEntity> findLastByAccountId(@Param("accountId") Long accountId);
}
