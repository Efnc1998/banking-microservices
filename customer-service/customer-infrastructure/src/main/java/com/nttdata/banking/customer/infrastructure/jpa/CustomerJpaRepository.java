package com.nttdata.banking.customer.infrastructure.jpa;

import com.nttdata.banking.customer.infrastructure.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for CustomerEntity.
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
    // customerId is now the PK, so findById and existsById from JpaRepository are used directly
}
