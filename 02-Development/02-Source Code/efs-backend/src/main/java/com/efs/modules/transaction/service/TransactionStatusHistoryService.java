package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionStatusHistoryRequest;
import com.efs.modules.transaction.dto.TransactionStatusHistoryResponse;
import com.efs.modules.transaction.entity.TransactionStatusHistory;
import com.efs.modules.transaction.mapper.TransactionStatusHistoryMapper;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.repository.TransactionStatusHistoryRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionStatusHistoryService
        implements TransactionStatusHistoryServiceInterface {

    private final TransactionStatusHistoryRepository transactionStatusHistoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionStatusHistoryMapper transactionStatusHistoryMapper;

    public TransactionStatusHistoryService(
            TransactionStatusHistoryRepository transactionStatusHistoryRepository,
            TransactionRepository transactionRepository,
            TransactionStatusHistoryMapper transactionStatusHistoryMapper) {

        this.transactionStatusHistoryRepository = transactionStatusHistoryRepository;
        this.transactionRepository = transactionRepository;
        this.transactionStatusHistoryMapper = transactionStatusHistoryMapper;
    }

    @Override
    @Transactional
    public TransactionStatusHistoryResponse createStatusHistory(
            UUID transactionId,
            TransactionStatusHistoryRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionStatusHistory history =
                transactionStatusHistoryMapper.toEntity(request);

        history.setTransactionId(transactionId);

        if (history.getChangedAt() == null) {
            history.setChangedAt(LocalDateTime.now());
        }

        TransactionStatusHistory savedHistory =
                transactionStatusHistoryRepository.save(history);

        return transactionStatusHistoryMapper.toResponse(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionStatusHistoryResponse getStatusHistoryById(
            UUID historyId) {

        TransactionStatusHistory history =
                transactionStatusHistoryRepository
                        .findByHistoryId(historyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction status history not found: "
                                                + historyId
                                )
                        );

        return transactionStatusHistoryMapper.toResponse(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionStatusHistoryResponse> getStatusHistoryByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionStatusHistoryRepository
                .findByTransactionIdOrderByChangedAtDesc(transactionId)
                .stream()
                .map(transactionStatusHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionStatusHistoryResponse> getStatusHistoryByCurrentStatus(
            String currentStatus) {

        return transactionStatusHistoryRepository
                .findByCurrentStatusOrderByChangedAtDesc(currentStatus)
                .stream()
                .map(transactionStatusHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionStatusHistoryResponse> getStatusHistoryByChangedBy(
            UUID changedBy) {

        return transactionStatusHistoryRepository
                .findByChangedByOrderByChangedAtDesc(changedBy)
                .stream()
                .map(transactionStatusHistoryMapper::toResponse)
                .toList();
    }
}