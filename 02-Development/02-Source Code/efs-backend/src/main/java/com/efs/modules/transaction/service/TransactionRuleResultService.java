package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
import com.efs.modules.transaction.dto.TransactionRuleResultResponse;
import com.efs.modules.transaction.entity.TransactionRuleResult;
import com.efs.modules.transaction.mapper.TransactionRuleResultMapper;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.repository.TransactionRuleResultRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionRuleResultService
        implements TransactionRuleResultServiceInterface {

    private final TransactionRuleResultRepository
            transactionRuleResultRepository;

    private final TransactionRepository
            transactionRepository;

    private final TransactionRuleResultMapper
            transactionRuleResultMapper;

    public TransactionRuleResultService(
            TransactionRuleResultRepository transactionRuleResultRepository,
            TransactionRepository transactionRepository,
            TransactionRuleResultMapper transactionRuleResultMapper) {

        this.transactionRuleResultRepository =
                transactionRuleResultRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionRuleResultMapper =
                transactionRuleResultMapper;
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

        return transactionRuleResultRepository
                .findByRuleIdOrderByExecutedAtDesc(ruleId)
                .stream()
                .map(transactionRuleResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionRuleResultResponse>
    getRuleResultsByEvaluationResult(
            String evaluationResult) {

        return transactionRuleResultRepository
                .findByEvaluationResultOrderByExecutedAtDesc(
                        evaluationResult
                )
                .stream()
                .map(transactionRuleResultMapper::toResponse)
                .toList();
    }
}