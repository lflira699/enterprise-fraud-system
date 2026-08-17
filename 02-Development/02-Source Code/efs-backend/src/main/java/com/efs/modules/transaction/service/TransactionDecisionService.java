package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.modules.transaction.entity.TransactionDecision;
import com.efs.modules.transaction.mapper.TransactionDecisionMapper;
import com.efs.modules.transaction.repository.TransactionDecisionRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionDecisionService
        implements TransactionDecisionServiceInterface {

    private final TransactionDecisionRepository
            transactionDecisionRepository;

    private final TransactionRepository
            transactionRepository;

    private final TransactionDecisionMapper
            transactionDecisionMapper;

    public TransactionDecisionService(
            TransactionDecisionRepository transactionDecisionRepository,
            TransactionRepository transactionRepository,
            TransactionDecisionMapper transactionDecisionMapper) {

        this.transactionDecisionRepository =
                transactionDecisionRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionDecisionMapper =
                transactionDecisionMapper;
    }

    @Override
    @Transactional
    public TransactionDecisionResponse createDecision(
            UUID transactionId,
            TransactionDecisionRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionDecision decision =
                transactionDecisionMapper.toEntity(request);

        decision.setTransactionId(transactionId);

        TransactionDecision savedDecision =
                transactionDecisionRepository.save(decision);

        return transactionDecisionMapper.toResponse(
                savedDecision
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDecisionResponse getDecisionById(
            UUID decisionId) {

        TransactionDecision decision =
                transactionDecisionRepository
                        .findByDecisionId(decisionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction decision not found: "
                                                + decisionId
                                )
                        );

        return transactionDecisionMapper.toResponse(
                decision
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDecisionResponse>
    getDecisionsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionDecisionRepository
                .findByTransactionIdOrderByDecisionTimestampDesc(
                        transactionId
                )
                .stream()
                .map(transactionDecisionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDecisionResponse>
    getDecisionsByType(
            String decisionType) {

        return transactionDecisionRepository
                .findByDecisionTypeOrderByDecisionTimestampDesc(
                        decisionType
                )
                .stream()
                .map(transactionDecisionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDecisionResponse>
    getDecisionsBySource(
            String decisionSource) {

        return transactionDecisionRepository
                .findByDecisionSourceOrderByDecisionTimestampDesc(
                        decisionSource
                )
                .stream()
                .map(transactionDecisionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDecisionResponse>
    getDecisionsByFinalStatus(
            Boolean finalDecision) {

        return transactionDecisionRepository
                .findByFinalDecisionOrderByDecisionTimestampDesc(
                        finalDecision
                )
                .stream()
                .map(transactionDecisionMapper::toResponse)
                .toList();
    }
}