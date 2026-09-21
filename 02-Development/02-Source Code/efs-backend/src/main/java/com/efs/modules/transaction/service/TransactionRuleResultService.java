package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
import com.efs.modules.transaction.dto.TransactionRuleResultResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionRuleResult;
import com.efs.modules.transaction.mapper.TransactionRuleResultMapper;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.repository.TransactionRuleResultRepository;
import com.efs.modules.transaction.validator.TransactionRuleResultValueValidator;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionRuleResultService
        implements TransactionRuleResultServiceInterface {

    private final TransactionRuleResultRepository
            transactionRuleResultRepository;

    private final TransactionRepository
            transactionRepository;

    private final TransactionRuleResultMapper
            transactionRuleResultMapper;

    private final TransactionRuleResultValueValidator
            transactionRuleResultValueValidator;

    public TransactionRuleResultService(
            TransactionRuleResultRepository transactionRuleResultRepository,
            TransactionRepository transactionRepository,
            TransactionRuleResultMapper transactionRuleResultMapper,
            TransactionRuleResultValueValidator
                    transactionRuleResultValueValidator) {

        this.transactionRuleResultRepository =
                transactionRuleResultRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionRuleResultMapper =
                transactionRuleResultMapper;

        this.transactionRuleResultValueValidator =
                transactionRuleResultValueValidator;
    }

    @Override
    @Transactional
    public TransactionRuleResultResponse createRuleResult(
            UUID transactionId,
            TransactionRuleResultRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        transactionRuleResultValueValidator
                .validate(request);

        TransactionRuleResult ruleResult =
                transactionRuleResultMapper.toEntity(request);

        ruleResult.setTransactionId(transactionId);

        if (ruleResult.getExecutedAt() == null) {
            ruleResult.setExecutedAt(LocalDateTime.now());
        }

        TransactionRuleResult savedRuleResult =
                transactionRuleResultRepository.save(ruleResult);

        return transactionRuleResultMapper.toResponse(
                savedRuleResult
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionRuleResultResponse getRuleResultById(
            UUID ruleResultId) {

        TransactionRuleResult ruleResult =
                transactionRuleResultRepository
                        .findByRuleResultId(ruleResultId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction rule result not found: "
                                                + ruleResultId
                                )
                        );

        if (transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(
                        ruleResult.getTransactionId()
                )
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Transaction rule result not found: "
                            + ruleResultId
            );
        }

        return transactionRuleResultMapper.toResponse(
                ruleResult
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionRuleResultResponse>
    getRuleResultsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionRuleResultRepository
                .findByTransactionIdOrderByExecutedAtAsc(transactionId)
                .stream()
                .map(transactionRuleResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionRuleResultResponse>
    getRuleResultsByRuleId(
            UUID ruleId) {

        return toActiveRuleResultResponses(
                transactionRuleResultRepository
                        .findByRuleIdOrderByExecutedAtDesc(
                                ruleId
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionRuleResultResponse>
    getRuleResultsByEvaluationResult(
            String evaluationResult) {

        return toActiveRuleResultResponses(
                transactionRuleResultRepository
                        .findByEvaluationResultOrderByExecutedAtDesc(
                                evaluationResult
                        )
        );
    }

    private List<TransactionRuleResultResponse>
    toActiveRuleResultResponses(
            List<TransactionRuleResult> ruleResults) {

        if (ruleResults.isEmpty()) {
            return List.of();
        }

        Set<UUID> transactionIds =
                ruleResults.stream()
                        .map(TransactionRuleResult::getTransactionId)
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

        return ruleResults.stream()
                .filter(ruleResult ->
                        activeTransactionIds.contains(
                                ruleResult.getTransactionId()
                        )
                )
                .map(transactionRuleResultMapper::toResponse)
                .toList();
    }
}