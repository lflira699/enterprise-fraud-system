package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.ScenarioEvidenceRequest;
import com.efs.modules.detection.dto.ScenarioEvidenceResponse;
import com.efs.modules.detection.entity.ScenarioEvidence;
import org.springframework.stereotype.Component;

@Component
public class ScenarioEvidenceMapper {

    public ScenarioEvidence toEntity(
            ScenarioEvidenceRequest request) {

        ScenarioEvidence evidence =
                new ScenarioEvidence();

        evidence.setScenarioVersionId(
                request.getScenarioVersionId()
        );

        evidence.setEvidenceType(
                request.getEvidenceType()
        );

        evidence.setSourceType(
                request.getSourceType()
        );

        evidence.setSourceReference(
                request.getSourceReference()
        );

        evidence.setEvidenceValue(
                request.getEvidenceValue()
        );

        evidence.setEvidenceSummary(
                request.getEvidenceSummary()
        );

        evidence.setConfidence(
                request.getConfidence()
        );

        evidence.setObservedAt(
                request.getObservedAt()
        );

        return evidence;
    }

    public ScenarioEvidenceResponse toResponse(
            ScenarioEvidence evidence) {

        ScenarioEvidenceResponse response =
                new ScenarioEvidenceResponse();

        response.setEvidenceId(
                evidence.getEvidenceId()
        );

        response.setScenarioVersionId(
                evidence.getScenarioVersionId()
        );

        response.setEvidenceType(
                evidence.getEvidenceType()
        );

        response.setSourceType(
                evidence.getSourceType()
        );

        response.setSourceReference(
                evidence.getSourceReference()
        );

        response.setEvidenceValue(
                evidence.getEvidenceValue()
        );

        response.setEvidenceSummary(
                evidence.getEvidenceSummary()
        );

        response.setConfidence(
                evidence.getConfidence()
        );

        response.setObservedAt(
                evidence.getObservedAt()
        );

        response.setCreatedAt(
                evidence.getCreatedAt()
        );

        return response;
    }
}