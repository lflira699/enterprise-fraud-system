package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.entity.ScenarioEvaluationRuleExecution;
import com.efs.modules.detection.mapper.ScenarioEvaluationRuleExecutionMapper;
import com.efs.modules.detection.repository.ScenarioEvaluationRuleExecutionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScenarioEvaluationRuleExecutionService
        implements ScenarioEvaluationRuleExecutionServiceInterface {

    private final ScenarioEvaluationRuleExecutionRepository repository;
    private final ScenarioEvaluationRuleExecutionMapper mapper;

    public ScenarioEvaluationRuleExecutionService(
            ScenarioEvaluationRuleExecutionRepository repository,
            ScenarioEvaluationRuleExecutionMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ScenarioEvaluationRuleExecutionResponse
    createScenarioEvaluationRuleExecution(
            ScenarioEvaluationRuleExecutionRequest request) {

        ScenarioEvaluationRuleExecution relation =
                mapper.toEntity(request);

        relation.setCreatedAt(LocalDateTime.now());

        ScenarioEvaluationRuleExecution savedRelation =
                repository.save(relation);

        return mapper.toResponse(savedRelation);
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioEvaluationRuleExecutionResponse
    getScenarioEvaluationRuleExecutionById(
            UUID evaluationRuleExecutionId) {

        ScenarioEvaluationRuleExecution relation =
                repository
                        .findByEvaluationRuleExecutionId(
                                evaluationRuleExecutionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario evaluation rule execution not found: "
                                                + evaluationRuleExecutionId
                                )
                        );

        return mapper.toResponse(relation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationRuleExecutionResponse>
    getRuleExecutionsByEvaluation(UUID evaluationId) {

        return repository
                .findByEvaluationIdOrderByCreatedAtAsc(evaluationId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationRuleExecutionResponse>
    getEvaluationsByRuleExecution(UUID executionId) {

        return repository
                .findByExecutionIdOrderByCreatedAtAsc(executionId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}