package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionStatusHistoryRepository
        extends JpaRepository<TransactionStatusHistory, UUID> {

    Optional<TransactionStatusHistory> findByHistoryId(
            UUID historyId
    );

    List<TransactionStatusHistory> findByTransactionIdOrderByChangedAtDesc(
            UUID transactionId
    );

    List<TransactionStatusHistory> findByCurrentStatusOrderByChangedAtDesc(
            String currentStatus
    );

    List<TransactionStatusHistory> findByChangedByOrderByChangedAtDesc(
            UUID changedBy
    );
}