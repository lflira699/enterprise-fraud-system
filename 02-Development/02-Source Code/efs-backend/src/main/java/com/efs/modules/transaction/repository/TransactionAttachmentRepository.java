package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionAttachmentRepository
        extends JpaRepository<TransactionAttachment, UUID> {

    Optional<TransactionAttachment> findByAttachmentId(
            UUID attachmentId
    );

    List<TransactionAttachment> findByTransactionIdOrderByUploadedAtDesc(
            UUID transactionId
    );

    List<TransactionAttachment> findByFileTypeOrderByUploadedAtDesc(
            String fileType
    );

    List<TransactionAttachment> findByUploadedByOrderByUploadedAtDesc(
            UUID uploadedBy
    );
}