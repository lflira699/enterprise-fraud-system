package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionHistoryRepository
        extends JpaRepository<TransactionHistory, UUID> {

    Optional<TransactionHistory> findByHistoryId(
            UUID historyId
    );

    Optional<TransactionHistory> findByTransactionIdAndVersionNumber(
            UUID transactionId,
            Integer versionNumber
    );

    List<TransactionHistory> findByTransactionIdOrderByVersionNumberDesc(
            UUID transactionId
    );

    List<TransactionHistory> findByChangedByOrderByChangedAtDesc(
            UUID changedBy
    );
}