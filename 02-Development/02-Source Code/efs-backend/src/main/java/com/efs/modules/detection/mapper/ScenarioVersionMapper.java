package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.ScenarioVersionRequest;
import com.efs.modules.detection.dto.ScenarioVersionResponse;
import com.efs.modules.detection.entity.ScenarioVersion;
import org.springframework.stereotype.Component;

@Component
public class ScenarioVersionMapper {

    public ScenarioVersion toEntity(
            ScenarioVersionRequest request) {

        ScenarioVersion version =
                new ScenarioVersion();

        version.setScenarioId(
                request.getScenarioId()
        );

        version.setVersionNumber(
                request.getVersionNumber()
        );

        version.setVersionStatus(
                request.getVersionStatus()
        );

        version.setCorrelationWindowSeconds(
                request.getCorrelationWindowSeconds()
        );

        version.setMaximumProcessingTimeMs(
                request.getMaximumProcessingTimeMs()
        );

        version.setMinimumEvents(
                request.getMinimumEvents()
        );

        version.setMinimumConfidence(
                request.getMinimumConfidence()
        );

        version.setActivationMode(
                request.getActivationMode()
        );

        version.setConfiguration(
                request.getConfiguration()
        );

        version.setEffectiveFrom(
                request.getEffectiveFrom()
        );

        version.setEffectiveTo(
                request.getEffectiveTo()
        );

        return version;
    }

    public ScenarioVersionResponse toResponse(
            ScenarioVersion version) {

        ScenarioVersionResponse response =
                new ScenarioVersionResponse();

        response.setScenarioVersionId(
                version.getScenarioVersionId()
        );

        response.setScenarioId(
                version.getScenarioId()
        );

        response.setVersionNumber(
                version.getVersionNumber()
        );

        response.setVersionStatus(
                version.getVersionStatus()
        );

        response.setCorrelationWindowSeconds(
                version.getCorrelationWindowSeconds()
        );

        response.setMaximumProcessingTimeMs(
                version.getMaximumProcessingTimeMs()
        );

        response.setMinimumEvents(
                version.getMinimumEvents()
        );

        response.setMinimumConfidence(
                version.getMinimumConfidence()
        );

        response.setActivationMode(
                version.getActivationMode()
        );

        response.setConfiguration(
                version.getConfiguration()
        );

        response.setEffectiveFrom(
                version.getEffectiveFrom()
        );

        response.setEffectiveTo(
                version.getEffectiveTo()
        );

        response.setCreatedAt(
                version.getCreatedAt()
        );

        response.setUpdatedAt(
                version.getUpdatedAt()
        );

        return response;
    }
}