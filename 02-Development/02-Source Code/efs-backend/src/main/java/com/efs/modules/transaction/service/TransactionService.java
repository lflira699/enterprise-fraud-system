package com.efs.modules.transaction.service;

import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.mapper.TransactionMapper;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService
        implements TransactionServiceInterface {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            CustomerRepository customerRepository,
            TransactionMapper transactionMapper) {

        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(
            TransactionRequest request) {

        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + request.getCustomerId()
            );
        }

        if (transactionRepository
                .existsByTransactionReferenceAndDeletedAtIsNull(
                        request.getTransactionReference()
                )) {

            throw new DuplicateRecordException(
                    "Transaction reference already exists: "
                            + request.getTransactionReference()
            );
        }

        Transaction transaction =
                transactionMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        if (transaction.getTransactionDatetime() == null) {
            transaction.setTransactionDatetime(now);
        }

        if (transaction.getTransactionStatus() == null
                || transaction.getTransactionStatus().isBlank()) {
            transaction.setTransactionStatus("RECEIVED");
        }

        if (transaction.getFinalDecision() == null
                || transaction.getFinalDecision().isBlank()) {
            transaction.setFinalDecision("PENDING");
        }

        if (transaction.getFraudScore() == null) {
            transaction.setFraudScore(BigDecimal.ZERO);
        }

        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(
            UUID transactionId) {

        Transaction transaction =
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                transactionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found: "
                                                + transactionId
                                )
                        );

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByReference(
            String transactionReference) {

        Transaction transaction =
                transactionRepository
                        .findByTransactionReferenceAndDeletedAtIsNull(
                                transactionReference
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found: "
                                                + transactionReference
                                )
                        );

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByCustomerId(
            UUID customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        return transactionRepository
                .findByCustomerIdAndDeletedAtIsNullOrderByTransactionDatetimeDesc(
                        customerId
                )
                .stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(
            UUID transactionId,
            TransactionRequest request) {

        Transaction transaction =
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                transactionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found: "
                                                + transactionId
                                )
                        );

        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + request.getCustomerId()
            );
        }

        transactionRepository
                .findByTransactionReferenceAndDeletedAtIsNull(
                        request.getTransactionReference()
                )
                .filter(existing ->
                        !existing.getTransactionId().equals(transactionId)
                )
                .ifPresent(existing -> {
                    throw new DuplicateRecordException(
                            "Transaction reference already exists: "
                                    + request.getTransactionReference()
                    );
                });

        transactionMapper.updateEntity(
                request,
                transaction
        );

        if (transaction.getTransactionDatetime() == null) {
            transaction.setTransactionDatetime(
                    LocalDateTime.now()
            );
        }

        if (transaction.getTransactionStatus() == null
                || transaction.getTransactionStatus().isBlank()) {
            transaction.setTransactionStatus("RECEIVED");
        }

        if (transaction.getFinalDecision() == null
                || transaction.getFinalDecision().isBlank()) {
            transaction.setFinalDecision("PENDING");
        }

        if (transaction.getFraudScore() == null) {
            transaction.setFraudScore(BigDecimal.ZERO);
        }

        transaction.setUpdatedAt(LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(
            UUID transactionId) {

        Transaction transaction =
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                transactionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found: "
                                                + transactionId
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        transaction.setDeletedAt(now);
        transaction.setUpdatedAt(now);

        transactionRepository.save(transaction);
    }
}