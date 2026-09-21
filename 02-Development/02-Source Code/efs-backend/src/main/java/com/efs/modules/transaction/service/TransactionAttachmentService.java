package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import com.efs.modules.transaction.dto.TransactionAttachmentResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionAttachment;
import com.efs.modules.transaction.mapper.TransactionAttachmentMapper;
import com.efs.modules.transaction.repository.TransactionAttachmentRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.validator.TransactionAttachmentValueValidator;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionAttachmentService
        implements TransactionAttachmentServiceInterface {

    private final TransactionAttachmentRepository transactionAttachmentRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionAttachmentMapper transactionAttachmentMapper;
    private final TransactionAttachmentValueValidator
            transactionAttachmentValueValidator;

    public TransactionAttachmentService(
            TransactionAttachmentRepository transactionAttachmentRepository,
            TransactionRepository transactionRepository,
            TransactionAttachmentMapper transactionAttachmentMapper,
            TransactionAttachmentValueValidator
                    transactionAttachmentValueValidator) {

        this.transactionAttachmentRepository = transactionAttachmentRepository;
        this.transactionRepository = transactionRepository;
        this.transactionAttachmentMapper = transactionAttachmentMapper;
        this.transactionAttachmentValueValidator =
                transactionAttachmentValueValidator;
    }

    @Override
    @Transactional
    public TransactionAttachmentResponse createAttachment(
            UUID transactionId,
            TransactionAttachmentRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        transactionAttachmentValueValidator
                .validate(request);

        TransactionAttachment attachment =
                transactionAttachmentMapper.toEntity(request);

        attachment.setTransactionId(transactionId);
        attachment.setUploadedAt(LocalDateTime.now());

        TransactionAttachment savedAttachment =
                transactionAttachmentRepository.save(attachment);

        return transactionAttachmentMapper.toResponse(savedAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionAttachmentResponse getAttachmentById(
            UUID attachmentId) {

        TransactionAttachment attachment =
                transactionAttachmentRepository
                        .findByAttachmentId(attachmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction attachment not found: "
                                                + attachmentId
                                )
                        );

        if (transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(
                        attachment.getTransactionId()
                )
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Transaction attachment not found: "
                            + attachmentId
            );
        }

        return transactionAttachmentMapper.toResponse(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionAttachmentResponse> getAttachmentsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionAttachmentRepository
                .findByTransactionIdOrderByUploadedAtDesc(transactionId)
                .stream()
                .map(transactionAttachmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionAttachmentResponse> getAttachmentsByFileType(
            String fileType) {

        return toActiveAttachmentResponses(
                transactionAttachmentRepository
                        .findByFileTypeOrderByUploadedAtDesc(
                                fileType
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionAttachmentResponse> getAttachmentsByUploadedBy(
            UUID uploadedBy) {

        return toActiveAttachmentResponses(
                transactionAttachmentRepository
                        .findByUploadedByOrderByUploadedAtDesc(
                                uploadedBy
                        )
        );
    }

    private List<TransactionAttachmentResponse>
    toActiveAttachmentResponses(
            List<TransactionAttachment> attachments) {

        if (attachments.isEmpty()) {
            return List.of();
        }

        Set<UUID> transactionIds =
                attachments.stream()
                        .map(TransactionAttachment::getTransactionId)
                        .collect(Collectors.toSet());

        Set<UUID> activeTransactionIds =
                transactionRepository
                        .findAllById(transactionIds)
                        .stream()
                        .filter(transaction ->
                                transaction.getDeletedAt() == null
                        )
                        .map(Transaction::getTransactionId)
                        .collect(Collectors.toSet());

        return attachments.stream()
                .filter(attachment ->
                        activeTransactionIds.contains(
                                attachment.getTransactionId()
                        )
                )
                .map(transactionAttachmentMapper::toResponse)
                .toList();
    }
}