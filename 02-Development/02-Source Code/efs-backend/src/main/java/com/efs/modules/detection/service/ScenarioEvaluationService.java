package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.mapper.ScenarioEvaluationMapper;
import com.efs.modules.detection.repository.ScenarioEvaluationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScenarioEvaluationService
        implements ScenarioEvaluationServiceInterface {

    private final ScenarioEvaluationRepository scenarioEvaluationRepository;
    private final ScenarioEvaluationMapper scenarioEvaluationMapper;

    public ScenarioEvaluationService(
            ScenarioEvaluationRepository scenarioEvaluationRepository,
            ScenarioEvaluationMapper scenarioEvaluationMapper) {

        this.scenarioEvaluationRepository = scenarioEvaluationRepository;
        this.scenarioEvaluationMapper = scenarioEvaluationMapper;
    }

    @Override
    @Transactional
    public ScenarioEvaluationResponse createScenarioEvaluation(
            ScenarioEvaluationRequest request) {

        ScenarioEvaluation evaluation =
                scenarioEvaluationMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        evaluation.setEvaluatedAt(now);
        evaluation.setCreatedAt(now);

        ScenarioEvaluation savedEvaluation =
                scenarioEvaluationRepository.save(evaluation);

        return scenarioEvaluationMapper.toResponse(
                savedEvaluation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioEvaluationResponse getScenarioEvaluationById(
            UUID evaluationId) {

        ScenarioEvaluation evaluation =
                scenarioEvaluationRepository
                        .findByEvaluationId(evaluationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario evaluation not found: "
                                                + evaluationId
                                )
                        );

        return scenarioEvaluationMapper.toResponse(
                evaluation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByScenario(UUID scenarioId) {

        return scenarioEvaluationRepository
                .findByScenarioIdOrderByEvaluatedAtDesc(scenarioId)
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByScenarioVersion(UUID scenarioVersionId) {

        return scenarioEvaluationRepository
                .findByScenarioVersionIdOrderByEvaluatedAtDesc(
                        scenarioVersionId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByTransaction(UUID transactionId) {

        return scenarioEvaluationRepository
                .findByTransactionIdOrderByEvaluatedAtDesc(
                        transactionId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByCustomer(UUID customerId) {

        return scenarioEvaluationRepository
                .findByCustomerIdOrderByEvaluatedAtDesc(
                        customerId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByStatus(String evaluationStatus) {

        return scenarioEvaluationRepository
                .findByEvaluationStatusOrderByEvaluatedAtDesc(
                        evaluationStatus
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByMatched(Boolean matched) {

        return scenarioEvaluationRepository
                .findByMatchedOrderByEvaluatedAtDesc(matched)
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }
}