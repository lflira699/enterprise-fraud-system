package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByTransactionIdAndDeletedAtIsNull(
            UUID transactionId
    );

    Optional<Transaction> findByTransactionReferenceAndDeletedAtIsNull(
            String transactionReference
    );

    List<Transaction> findByCustomerIdAndDeletedAtIsNullOrderByTransactionDatetimeDesc(
            UUID customerId
    );

    boolean existsByTransactionReferenceAndDeletedAtIsNull(
            String transactionReference
    );
}