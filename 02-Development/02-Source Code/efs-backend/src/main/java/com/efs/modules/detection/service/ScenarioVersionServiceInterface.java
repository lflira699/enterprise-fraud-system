package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioVersionRequest;
import com.efs.modules.detection.dto.ScenarioVersionResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioVersionServiceInterface {

    ScenarioVersionResponse createScenarioVersion(
            ScenarioVersionRequest request
    );

    ScenarioVersionResponse getScenarioVersionById(
            UUID scenarioVersionId
    );

    List<ScenarioVersionResponse> getScenarioVersionsByScenario(
            UUID scenarioId
    );

    ScenarioVersionResponse getScenarioVersionByNumber(
            UUID scenarioId,
            Integer versionNumber
    );

    List<ScenarioVersionResponse> getScenarioVersionsByStatus(
            String versionStatus
    );

    List<ScenarioVersionResponse> getScenarioVersionsByActivationMode(
            String activationMode
    );
}