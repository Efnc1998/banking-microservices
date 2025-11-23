package com.nttdata.banking.account.infrastructure.jpa;

import com.nttdata.banking.account.infrastructure.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for AccountEntity.
 */
@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    List<AccountEntity> findByCustomerId(Long customerId);

    boolean existsByAccountNumber(String accountNumber);
}
