package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleExecutionRequest;
import com.efs.modules.rules.dto.RuleExecutionResponse;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.mapper.RuleExecutionMapper;
import com.efs.modules.rules.repository.RuleExecutionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleExecutionService
        implements RuleExecutionServiceInterface {

    private final RuleExecutionRepository ruleExecutionRepository;
    private final RuleExecutionMapper ruleExecutionMapper;

    public RuleExecutionService(
            RuleExecutionRepository ruleExecutionRepository,
            RuleExecutionMapper ruleExecutionMapper) {

        this.ruleExecutionRepository = ruleExecutionRepository;
        this.ruleExecutionMapper = ruleExecutionMapper;
    }

    @Override
    @Transactional
    public RuleExecutionResponse createRuleExecution(
            RuleExecutionRequest request) {

        RuleExecution execution =
                ruleExecutionMapper.toEntity(request);

        execution.setExecutedAt(
                LocalDateTime.now()
        );

        RuleExecution savedExecution =
                ruleExecutionRepository.save(execution);

        return ruleExecutionMapper.toResponse(savedExecution);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleExecutionResponse getRuleExecutionById(
            UUID executionId) {

        RuleExecution execution =
                ruleExecutionRepository
                        .findByExecutionId(executionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule execution not found: "
                                                + executionId
                                )
                        );

        return ruleExecutionMapper.toResponse(execution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByRuleId(
            UUID ruleId) {

        return ruleExecutionRepository
                .findByRuleIdOrderByExecutedAtDesc(ruleId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByRuleVersionId(
            UUID ruleVersionId) {

        return ruleExecutionRepository
                .findByRuleVersionIdOrderByExecutedAtDesc(ruleVersionId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByPolicyId(
            UUID policyId) {

        return ruleExecutionRepository
                .findByPolicyIdOrderByExecutedAtDesc(policyId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByTransactionId(
            UUID transactionId) {

        return ruleExecutionRepository
                .findByTransactionIdOrderByExecutedAtDesc(transactionId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByStatus(
            String executionStatus) {

        return ruleExecutionRepository
                .findByExecutionStatusOrderByExecutedAtDesc(
                        executionStatus
                )
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }
}