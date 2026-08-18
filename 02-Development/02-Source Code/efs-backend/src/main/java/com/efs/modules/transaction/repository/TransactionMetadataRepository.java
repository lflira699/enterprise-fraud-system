package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionMetadataRepository
        extends JpaRepository<TransactionMetadata, UUID> {

    Optional<TransactionMetadata> findByMetadataId(
            UUID metadataId
    );

    List<TransactionMetadata> findByTransactionIdOrderByCreatedAtDesc(
            UUID transactionId
    );

    List<TransactionMetadata> findByMetadataTypeOrderByCreatedAtDesc(
            String metadataType
    );
}