package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleSimulationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;

import java.util.List;
import java.util.UUID;

public interface RuleSimulationServiceInterface {

    RuleSimulationResponse createRuleSimulation(
            RuleSimulationRequest request
    );

    RuleSimulationResponse getRuleSimulationById(
            UUID simulationId
    );

    List<RuleSimulationResponse> getRuleSimulationsByEntity(
            String entityType,
            UUID entityId
    );

    List<RuleSimulationResponse> getRuleSimulationsByStatus(
            String simulationStatus
    );

    List<RuleSimulationResponse> getRuleSimulationsByExecutedBy(
            UUID executedBy
    );
}