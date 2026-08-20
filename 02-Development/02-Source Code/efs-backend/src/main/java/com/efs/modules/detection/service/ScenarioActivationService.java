package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.modules.detection.entity.ScenarioActivation;
import com.efs.modules.detection.mapper.ScenarioActivationMapper;
import com.efs.modules.detection.repository.ScenarioActivationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScenarioActivationService
        implements ScenarioActivationServiceInterface {

    private final ScenarioActivationRepository scenarioActivationRepository;
    private final ScenarioActivationMapper scenarioActivationMapper;

    public ScenarioActivationService(
            ScenarioActivationRepository scenarioActivationRepository,
            ScenarioActivationMapper scenarioActivationMapper) {

        this.scenarioActivationRepository = scenarioActivationRepository;
        this.scenarioActivationMapper = scenarioActivationMapper;
    }

    @Override
    @Transactional
    public ScenarioActivationResponse createScenarioActivation(
            ScenarioActivationRequest request) {

        ScenarioActivation activation =
                scenarioActivationMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        activation.setTriggeredAt(now);
        activation.setCreatedAt(now);

        ScenarioActivation savedActivation =
                scenarioActivationRepository.save(activation);

        return scenarioActivationMapper.toResponse(
                savedActivation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioActivationResponse getScenarioActivationById(
            UUID activationId) {

        ScenarioActivation activation =
                scenarioActivationRepository
                        .findByActivationId(activationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario activation not found: "
                                                + activationId
                                )
                        );

        return scenarioActivationMapper.toResponse(
                activation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByScenario(UUID scenarioId) {

        return scenarioActivationRepository
                .findByScenarioIdOrderByTriggeredAtDesc(scenarioId)
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByScenarioVersion(UUID scenarioVersionId) {

        return scenarioActivationRepository
                .findByScenarioVersionIdOrderByTriggeredAtDesc(
                        scenarioVersionId
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByTransaction(UUID transactionId) {

        return scenarioActivationRepository
                .findByTransactionIdOrderByTriggeredAtDesc(
                        transactionId
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByCustomer(UUID customerId) {

        return scenarioActivationRepository
                .findByCustomerIdOrderByTriggeredAtDesc(
                        customerId
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByStatus(String activationStatus) {

        return scenarioActivationRepository
                .findByActivationStatusOrderByTriggeredAtDesc(
                        activationStatus
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsBySeverity(String severity) {

        return scenarioActivationRepository
                .findBySeverityOrderByTriggeredAtDesc(severity)
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }
}