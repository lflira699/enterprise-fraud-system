package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionTypeRepository
        extends JpaRepository<TransactionType, UUID> {

    Optional<TransactionType> findByTransactionTypeCode(
            String transactionTypeCode
    );

    List<TransactionType> findByStatusOrderByDisplayOrderAsc(
            String status
    );

    List<TransactionType> findAllByOrderByDisplayOrderAsc();
}