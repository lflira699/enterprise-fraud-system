package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleHistoryRequest;
import com.efs.modules.rules.dto.RuleHistoryResponse;
import com.efs.modules.rules.entity.RuleHistory;
import com.efs.modules.rules.mapper.RuleHistoryMapper;
import com.efs.modules.rules.repository.RuleHistoryRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleHistoryService
        implements RuleHistoryServiceInterface {

    private final RuleHistoryRepository ruleHistoryRepository;
    private final RuleHistoryMapper ruleHistoryMapper;

    public RuleHistoryService(
            RuleHistoryRepository ruleHistoryRepository,
            RuleHistoryMapper ruleHistoryMapper) {

        this.ruleHistoryRepository = ruleHistoryRepository;
        this.ruleHistoryMapper = ruleHistoryMapper;
    }

    @Override
    @Transactional
    public RuleHistoryResponse createRuleHistory(
            RuleHistoryRequest request) {

        RuleHistory history =
                ruleHistoryMapper.toEntity(request);

        history.setChangedAt(
                LocalDateTime.now()
        );

        RuleHistory savedHistory =
                ruleHistoryRepository.save(history);

        return ruleHistoryMapper.toResponse(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleHistoryResponse getRuleHistoryById(
            UUID historyId) {

        RuleHistory history =
                ruleHistoryRepository
                        .findByHistoryId(historyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule history not found: "
                                                + historyId
                                )
                        );

        return ruleHistoryMapper.toResponse(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleHistoryResponse> getRuleHistoriesByEntity(
            String entityType,
            UUID entityId) {

        return ruleHistoryRepository
                .findByEntityTypeAndEntityIdOrderByChangedAtDesc(
                        entityType,
                        entityId
                )
                .stream()
                .map(ruleHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleHistoryResponse> getRuleHistoriesByChangedBy(
            UUID changedBy) {

        return ruleHistoryRepository
                .findByChangedByOrderByChangedAtDesc(changedBy)
                .stream()
                .map(ruleHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleHistoryResponse> getRuleHistoriesByOperationType(
            String operationType) {

        return ruleHistoryRepository
                .findByOperationTypeOrderByChangedAtDesc(operationType)
                .stream()
                .map(ruleHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleHistoryResponse> getRuleHistoriesByCorrelationId(
            UUID correlationId) {

        return ruleHistoryRepository
                .findByCorrelationIdOrderByChangedAtDesc(correlationId)
                .stream()
                .map(ruleHistoryMapper::toResponse)
                .toList();
    }
}