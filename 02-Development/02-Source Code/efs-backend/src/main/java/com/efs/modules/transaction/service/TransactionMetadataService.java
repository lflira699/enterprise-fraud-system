package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionMetadataRequest;
import com.efs.modules.transaction.dto.TransactionMetadataResponse;
import com.efs.modules.transaction.entity.TransactionMetadata;
import com.efs.modules.transaction.mapper.TransactionMetadataMapper;
import com.efs.modules.transaction.repository.TransactionMetadataRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionMetadataService
        implements TransactionMetadataServiceInterface {

    private final TransactionMetadataRepository transactionMetadataRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMetadataMapper transactionMetadataMapper;

    public TransactionMetadataService(
            TransactionMetadataRepository transactionMetadataRepository,
            TransactionRepository transactionRepository,
            TransactionMetadataMapper transactionMetadataMapper) {

        this.transactionMetadataRepository = transactionMetadataRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMetadataMapper = transactionMetadataMapper;
    }

    @Override
    @Transactional
    public TransactionMetadataResponse createMetadata(
            UUID transactionId,
            TransactionMetadataRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionMetadata metadata =
                transactionMetadataMapper.toEntity(request);

        metadata.setTransactionId(transactionId);
        metadata.setCreatedAt(LocalDateTime.now());

        TransactionMetadata savedMetadata =
                transactionMetadataRepository.save(metadata);

        return transactionMetadataMapper.toResponse(savedMetadata);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionMetadataResponse getMetadataById(
            UUID metadataId) {

        TransactionMetadata metadata =
                transactionMetadataRepository
                        .findByMetadataId(metadataId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction metadata not found: "
                                                + metadataId
                                )
                        );

        return transactionMetadataMapper.toResponse(metadata);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionMetadataResponse> getMetadataByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionMetadataRepository
                .findByTransactionIdOrderByCreatedAtDesc(transactionId)
                .stream()
                .map(transactionMetadataMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionMetadataResponse> getMetadataByType(
            String metadataType) {

        return transactionMetadataRepository
                .findByMetadataTypeOrderByCreatedAtDesc(metadataType)
                .stream()
                .map(transactionMetadataMapper::toResponse)
                .toList();
    }
}