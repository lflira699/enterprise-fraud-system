package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;

import java.util.List;
import java.util.UUID;

public interface DetectionScenarioServiceInterface {

    DetectionScenarioResponse createScenario(
            DetectionScenarioRequest request
    );

    DetectionScenarioResponse getScenarioById(
            UUID scenarioId
    );

    DetectionScenarioResponse getScenarioByCodeAndVersion(
            String scenarioCode,
            Integer version
    );

    List<DetectionScenarioResponse> getScenariosByCode(
            String scenarioCode
    );

    List<DetectionScenarioResponse> getScenariosByCategory(
            String category
    );

    List<DetectionScenarioResponse> getScenariosByStatus(
            String status
    );

    List<DetectionScenarioResponse> getScenariosByCriticality(
            String criticality
    );

    List<DetectionScenarioResponse> getScenariosByOwner(
            String owner
    );
}