package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import com.efs.modules.transaction.dto.TransactionAttachmentResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionAttachmentServiceInterface {

    TransactionAttachmentResponse createAttachment(
            UUID transactionId,
            TransactionAttachmentRequest request
    );

    TransactionAttachmentResponse getAttachmentById(
            UUID attachmentId
    );

    List<TransactionAttachmentResponse> getAttachmentsByTransactionId(
            UUID transactionId
    );

    List<TransactionAttachmentResponse> getAttachmentsByFileType(
            String fileType
    );

    List<TransactionAttachmentResponse> getAttachmentsByUploadedBy(
            UUID uploadedBy
    );
}