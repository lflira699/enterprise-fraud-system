package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionScoreRequest;
import com.efs.modules.transaction.dto.TransactionScoreResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionScore;
import com.efs.modules.transaction.mapper.TransactionScoreMapper;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.repository.TransactionScoreRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionScoreService
        implements TransactionScoreServiceInterface {

    private final TransactionScoreRepository
            transactionScoreRepository;

    private final TransactionRepository
            transactionRepository;

    private final TransactionScoreMapper
            transactionScoreMapper;

    public TransactionScoreService(
            TransactionScoreRepository transactionScoreRepository,
            TransactionRepository transactionRepository,
            TransactionScoreMapper transactionScoreMapper) {

        this.transactionScoreRepository =
                transactionScoreRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionScoreMapper =
                transactionScoreMapper;
    }

    @Override
    @Transactional
    public TransactionScoreResponse createScore(
            UUID transactionId,
            TransactionScoreRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionScore score =
                transactionScoreMapper.toEntity(request);

        score.setTransactionId(transactionId);

        if (score.getCalculatedAt() == null) {
            score.setCalculatedAt(LocalDateTime.now());
        }

        TransactionScore savedScore =
                transactionScoreRepository.save(score);

        return transactionScoreMapper.toResponse(savedScore);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionScoreResponse getScoreById(
            UUID scoreId) {

        TransactionScore score =
                transactionScoreRepository
                        .findByScoreId(scoreId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction score not found: "
                                                + scoreId
                                )
                        );

        if (transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(
                        score.getTransactionId()
                )
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Transaction score not found: "
                            + scoreId
            );
        }

        return transactionScoreMapper.toResponse(score);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionScoreResponse>
    getScoresByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionScoreRepository
                .findByTransactionIdOrderByCalculatedAtDesc(
                        transactionId
                )
                .stream()
                .map(transactionScoreMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionScoreResponse> getScoresByType(
            String scoreType) {

        return toActiveScoreResponses(
                transactionScoreRepository
                        .findByScoreTypeOrderByCalculatedAtDesc(
                                scoreType
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionScoreResponse>
    getScoresByScoringModel(
            String scoringModel) {

        return toActiveScoreResponses(
                transactionScoreRepository
                        .findByScoringModelOrderByCalculatedAtDesc(
                                scoringModel
                        )
        );
    }

    private List<TransactionScoreResponse>
    toActiveScoreResponses(
            List<TransactionScore> scores) {

        if (scores.isEmpty()) {
            return List.of();
        }

        Set<UUID> transactionIds =
                scores.stream()
                        .map(TransactionScore::getTransactionId)
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

        return scores.stream()
                .filter(score ->
                        activeTransactionIds.contains(
                                score.getTransactionId()
                        )
                )
                .map(transactionScoreMapper::toResponse)
                .toList();
    }
}