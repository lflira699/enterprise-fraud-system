package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvidenceRequest;
import com.efs.modules.detection.dto.ScenarioEvidenceResponse;

import java.util.List;
import java.util.UUID;

public interface ScenarioEvidenceServiceInterface {

    ScenarioEvidenceResponse createScenarioEvidence(
            ScenarioEvidenceRequest request
    );

    ScenarioEvidenceResponse getScenarioEvidenceById(
            UUID evidenceId
    );

    List<ScenarioEvidenceResponse> getEvidenceByScenarioVersion(
            UUID scenarioVersionId
    );

    List<ScenarioEvidenceResponse> getEvidenceByType(
            String evidenceType
    );

    List<ScenarioEvidenceResponse> getEvidenceBySourceType(
            String sourceType
    );
}