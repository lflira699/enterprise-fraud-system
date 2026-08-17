package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionPaymentMethodRequest;
import com.efs.modules.transaction.dto.TransactionPaymentMethodResponse;
import com.efs.modules.transaction.entity.TransactionPaymentMethod;
import com.efs.modules.transaction.mapper.TransactionPaymentMethodMapper;
import com.efs.modules.transaction.repository.TransactionPaymentMethodRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionPaymentMethodService
        implements TransactionPaymentMethodServiceInterface {

    private final TransactionPaymentMethodRepository
            transactionPaymentMethodRepository;

    private final TransactionRepository transactionRepository;

    private final TransactionPaymentMethodMapper
            transactionPaymentMethodMapper;

    public TransactionPaymentMethodService(
            TransactionPaymentMethodRepository
                    transactionPaymentMethodRepository,
            TransactionRepository transactionRepository,
            TransactionPaymentMethodMapper
                    transactionPaymentMethodMapper) {

        this.transactionPaymentMethodRepository =
                transactionPaymentMethodRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionPaymentMethodMapper =
                transactionPaymentMethodMapper;
    }

    @Override
    @Transactional
    public TransactionPaymentMethodResponse createPaymentMethod(
            UUID transactionId,
            TransactionPaymentMethodRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionPaymentMethod paymentMethod =
                transactionPaymentMethodMapper.toEntity(request);

        paymentMethod.setTransactionId(transactionId);
        paymentMethod.setCreatedAt(LocalDateTime.now());

        TransactionPaymentMethod savedPaymentMethod =
                transactionPaymentMethodRepository.save(paymentMethod);

        return transactionPaymentMethodMapper.toResponse(
                savedPaymentMethod
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionPaymentMethodResponse getPaymentMethodById(
            UUID paymentMethodId) {

        TransactionPaymentMethod paymentMethod =
                transactionPaymentMethodRepository
                        .findByPaymentMethodId(paymentMethodId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction payment method not found: "
                                                + paymentMethodId
                                )
                        );

        return transactionPaymentMethodMapper.toResponse(
                paymentMethod
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionPaymentMethodResponse>
    getPaymentMethodsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionPaymentMethodRepository
                .findByTransactionId(transactionId)
                .stream()
                .map(transactionPaymentMethodMapper::toResponse)
                .toList();
    }
}