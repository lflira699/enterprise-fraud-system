package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionHistoryRequest;
import com.efs.modules.transaction.dto.TransactionHistoryResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionHistory;
import com.efs.modules.transaction.mapper.TransactionHistoryMapper;
import com.efs.modules.transaction.repository.TransactionHistoryRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionHistoryService
        implements TransactionHistoryServiceInterface {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;

    public TransactionHistoryService(
            TransactionHistoryRepository transactionHistoryRepository,
            TransactionRepository transactionRepository,
            TransactionHistoryMapper transactionHistoryMapper) {

        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionRepository = transactionRepository;
        this.transactionHistoryMapper = transactionHistoryMapper;
    }

    @Override
    @Transactional
    public TransactionHistoryResponse createHistory(
            UUID transactionId,
            TransactionHistoryRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionHistory history =
                transactionHistoryMapper.toEntity(request);

        history.setTransactionId(transactionId);
        history.setChangedAt(LocalDateTime.now());

        TransactionHistory savedHistory =
                transactionHistoryRepository.save(history);

        return transactionHistoryMapper.toResponse(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionHistoryResponse getHistoryById(
            UUID historyId) {

        TransactionHistory history =
                transactionHistoryRepository
                        .findByHistoryId(historyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction history not found: "
                                                + historyId
                                )
                        );

        if (
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                history.getTransactionId()
                        )
                        .isEmpty()
        ) {
            throw new ResourceNotFoundException(
                    "Transaction history not found: "
                            + historyId
            );
        }

        return transactionHistoryMapper.toResponse(history);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionHistoryResponse getHistoryByTransactionIdAndVersionNumber(
            UUID transactionId,
            Integer versionNumber) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionHistory history =
                transactionHistoryRepository
                        .findByTransactionIdAndVersionNumber(
                                transactionId,
                                versionNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction history not found for transaction "
                                                + transactionId
                                                + " and version "
                                                + versionNumber
                                )
                        );

        return transactionHistoryMapper.toResponse(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getHistoryByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionHistoryRepository
                .findByTransactionIdOrderByVersionNumberDesc(transactionId)
                .stream()
                .map(transactionHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getHistoryByChangedBy(
            UUID changedBy) {

        return toActiveHistoryResponses(
                transactionHistoryRepository
                        .findByChangedByOrderByChangedAtDesc(changedBy)
        );
    }

    private List<TransactionHistoryResponse>
    toActiveHistoryResponses(
            List<TransactionHistory> histories) {

        if (histories.isEmpty()) {
            return List.of();
        }

        Set<UUID> transactionIds =
                histories.stream()
                        .map(TransactionHistory::getTransactionId)
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

        return histories.stream()
                .filter(history ->
                        activeTransactionIds.contains(
                                history.getTransactionId()
                        )
                )
                .map(transactionHistoryMapper::toResponse)
                .toList();
    }
}