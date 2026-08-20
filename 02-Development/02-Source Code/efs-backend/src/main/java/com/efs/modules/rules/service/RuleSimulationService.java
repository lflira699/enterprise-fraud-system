package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleSimulationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.entity.RuleSimulation;
import com.efs.modules.rules.mapper.RuleSimulationMapper;
import com.efs.modules.rules.repository.RuleSimulationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RuleSimulationService
        implements RuleSimulationServiceInterface {

    private final RuleSimulationRepository ruleSimulationRepository;
    private final RuleSimulationMapper ruleSimulationMapper;

    public RuleSimulationService(
            RuleSimulationRepository ruleSimulationRepository,
            RuleSimulationMapper ruleSimulationMapper) {

        this.ruleSimulationRepository = ruleSimulationRepository;
        this.ruleSimulationMapper = ruleSimulationMapper;
    }

    @Override
    @Transactional
    public RuleSimulationResponse createRuleSimulation(
            RuleSimulationRequest request) {

        RuleSimulation simulation =
                ruleSimulationMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        simulation.setStartedAt(now);
        simulation.setCreatedAt(now);

        RuleSimulation savedSimulation =
                ruleSimulationRepository.save(simulation);

        return ruleSimulationMapper.toResponse(savedSimulation);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleSimulationResponse getRuleSimulationById(
            UUID simulationId) {

        RuleSimulation simulation =
                ruleSimulationRepository
                        .findBySimulationId(simulationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule simulation not found: "
                                                + simulationId
                                )
                        );

        return ruleSimulationMapper.toResponse(simulation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleSimulationResponse> getRuleSimulationsByEntity(
            String entityType,
            UUID entityId) {

        return ruleSimulationRepository
                .findByEntityTypeAndEntityIdOrderByStartedAtDesc(
                        entityType,
                        entityId
                )
                .stream()
                .map(ruleSimulationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleSimulationResponse> getRuleSimulationsByStatus(
            String simulationStatus) {

        return ruleSimulationRepository
                .findBySimulationStatusOrderByStartedAtDesc(
                        simulationStatus
                )
                .stream()
                .map(ruleSimulationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleSimulationResponse> getRuleSimulationsByExecutedBy(
            UUID executedBy) {

        return ruleSimulationRepository
                .findByExecutedByOrderByStartedAtDesc(executedBy)
                .stream()
                .map(ruleSimulationMapper::toResponse)
                .toList();
    }
}