package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.entity.ScenarioEvaluationRuleExecution;
import com.efs.modules.detection.mapper.ScenarioEvaluationRuleExecutionMapper;
import com.efs.modules.detection.repository.ScenarioEvaluationRepository;
import com.efs.modules.detection.repository.ScenarioEvaluationRuleExecutionRepository;
import com.efs.modules.rules.dto.RuleExecutionResponse;
import com.efs.modules.rules.service.RuleExecutionServiceInterface;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ScenarioEvaluationRuleExecutionService
        implements ScenarioEvaluationRuleExecutionServiceInterface {

    private final ScenarioEvaluationRuleExecutionRepository
            repository;

    private final ScenarioEvaluationRuleExecutionMapper
            mapper;

    private final ScenarioEvaluationRepository
            scenarioEvaluationRepository;

    private final RuleExecutionServiceInterface
            ruleExecutionService;

    private final TransactionServiceInterface
            transactionService;

    public ScenarioEvaluationRuleExecutionService(
            ScenarioEvaluationRuleExecutionRepository repository,
            ScenarioEvaluationRuleExecutionMapper mapper,
            ScenarioEvaluationRepository scenarioEvaluationRepository,
            RuleExecutionServiceInterface ruleExecutionService,
            TransactionServiceInterface transactionService) {

        this.repository =
                repository;

        this.mapper =
                mapper;

        this.scenarioEvaluationRepository =
                scenarioEvaluationRepository;

        this.ruleExecutionService =
                ruleExecutionService;

        this.transactionService =
                transactionService;
    }

    @Override
    @Transactional
    public ScenarioEvaluationRuleExecutionResponse
    createScenarioEvaluationRuleExecution(
            ScenarioEvaluationRuleExecutionRequest request,
            UUID organizationId,
            UUID tenantId) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        Objects.requireNonNull(
                organizationId,
                "organizationId is required"
        );

        ScenarioEvaluation parent =
                scenarioEvaluationRepository
                        .findScopedByEvaluationId(
                                request.getEvaluationId(),
                                organizationId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario evaluation not found: "
                                                + request.getEvaluationId()
                                )
                        );

        RuleExecutionResponse ruleExecution =
                ruleExecutionService
                        .getRuleExecutionById(
                                request.getExecutionId()
                        );

        UUID executionTransactionId =
                ruleExecution.getTransactionId();

        if (executionTransactionId == null) {

            throw new IllegalStateException(
                    "RuleExecution transactionId is required "
                            + "for ScenarioEvaluation scope validation"
            );
        }

        TransactionResponse executionTransaction =
                transactionService
                        .getTransactionById(
                                executionTransactionId
                        );

        if (executionTransaction.getOrganizationId() == null) {

            throw new IllegalStateException(
                    "RuleExecution transaction organizationId "
                            + "is required for ScenarioEvaluation "
                            + "scope validation"
            );
        }

        if (
            !Objects.equals(
                    parent.getOrganizationId(),
                    executionTransaction.getOrganizationId()
            )
                    ||
            !Objects.equals(
                    parent.getTenantId(),
                    executionTransaction.getTenantId()
            )
        ) {

            throw new ResourceNotFoundException(
                    "Rule execution not found "
                            + "in ScenarioEvaluation scope"
            );
        }

        ScenarioEvaluationRuleExecution relation =
                mapper.toEntity(request);

        relation.setCreatedAt(
                LocalDateTime.now()
        );

        ScenarioEvaluationRuleExecution savedRelation =
                repository.save(
                        relation
                );

        return mapper.toResponse(
                savedRelation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioEvaluationRuleExecutionResponse
    getScenarioEvaluationRuleExecutionById(
            UUID evaluationRuleExecutionId,
            UUID organizationId,
            UUID tenantId) {

        ScenarioEvaluationRuleExecution relation =
                repository
                        .findScopedByEvaluationRuleExecutionId(
                                evaluationRuleExecutionId,
                                organizationId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario evaluation rule execution not found: "
                                                + evaluationRuleExecutionId
                                )
                        );

        return mapper.toResponse(
                relation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationRuleExecutionResponse>
    getRuleExecutionsByEvaluation(
            UUID evaluationId,
            UUID organizationId,
            UUID tenantId) {

        return repository
                .findScopedByEvaluationId(
                        evaluationId,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationRuleExecutionResponse>
    getEvaluationsByRuleExecution(
            UUID executionId,
            UUID organizationId,
            UUID tenantId) {

        return repository
                .findScopedByExecutionId(
                        executionId,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}