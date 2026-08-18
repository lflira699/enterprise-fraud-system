package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import com.efs.modules.transaction.dto.TransactionAttachmentResponse;
import com.efs.modules.transaction.entity.TransactionAttachment;
import org.springframework.stereotype.Component;

@Component
public class TransactionAttachmentMapper {

    public TransactionAttachment toEntity(
            TransactionAttachmentRequest request) {

        TransactionAttachment attachment =
                new TransactionAttachment();

        attachment.setFileName(
                request.getFileName()
        );

        attachment.setFileType(
                request.getFileType()
        );

        attachment.setMimeType(
                request.getMimeType()
        );

        attachment.setFileSize(
                request.getFileSize()
        );

        attachment.setStorageUri(
                request.getStorageUri()
        );

        attachment.setChecksumSha256(
                request.getChecksumSha256()
        );

        attachment.setUploadedBy(
                request.getUploadedBy()
        );

        return attachment;
    }

    public TransactionAttachmentResponse toResponse(
            TransactionAttachment attachment) {

        TransactionAttachmentResponse response =
                new TransactionAttachmentResponse();

        response.setAttachmentId(
                attachment.getAttachmentId()
        );

        response.setTransactionId(
                attachment.getTransactionId()
        );

        response.setFileName(
                attachment.getFileName()
        );

        response.setFileType(
                attachment.getFileType()
        );

        response.setMimeType(
                attachment.getMimeType()
        );

        response.setFileSize(
                attachment.getFileSize()
        );

        response.setStorageUri(
                attachment.getStorageUri()
        );

        response.setChecksumSha256(
                attachment.getChecksumSha256()
        );

        response.setUploadedBy(
                attachment.getUploadedBy()
        );

        response.setUploadedAt(
                attachment.getUploadedAt()
        );

        return response;
    }
}