package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import org.springframework.stereotype.Component;

@Component
public class DetectionScenarioMapper {

    public DetectionScenario toEntity(
            DetectionScenarioRequest request) {

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                request.getScenarioCode()
        );

        scenario.setScenarioName(
                request.getScenarioName()
        );

        scenario.setObjective(
                request.getObjective()
        );

        scenario.setDescription(
                request.getDescription()
        );

        scenario.setCategory(
                request.getCategory()
        );

        scenario.setCriticality(
                request.getCriticality()
        );

        scenario.setStatus(
                request.getStatus()
        );

        scenario.setOwner(
                request.getOwner()
        );

        scenario.setVersion(
                request.getVersion()
        );

        scenario.setCorrelationWindowMinutes(
                request.getCorrelationWindowMinutes()
        );

        scenario.setMaximumExecutionTimeSeconds(
                request.getMaximumExecutionTimeSeconds()
        );

        scenario.setMinimumEvents(
                request.getMinimumEvents()
        );

        scenario.setMinimumConfidence(
                request.getMinimumConfidence()
        );

        scenario.setMinimumEvidence(
                request.getMinimumEvidence()
        );

        scenario.setRequiredRules(
                request.getRequiredRules()
        );

        scenario.setRequiredVariables(
                request.getRequiredVariables()
        );

        scenario.setEvidenceRequirements(
                request.getEvidenceRequirements()
        );

        scenario.setExclusions(
                request.getExclusions()
        );

        scenario.setExceptions(
                request.getExceptions()
        );

        scenario.setSuggestedActions(
                request.getSuggestedActions()
        );

        scenario.setRelatedScenarios(
                request.getRelatedScenarios()
        );

        scenario.setConfigurationContext(
                request.getConfigurationContext()
        );

        return scenario;
    }

    public DetectionScenarioResponse toResponse(
            DetectionScenario scenario) {

        DetectionScenarioResponse response =
                new DetectionScenarioResponse();

        response.setScenarioId(
                scenario.getScenarioId()
        );

        response.setScenarioCode(
                scenario.getScenarioCode()
        );

        response.setScenarioName(
                scenario.getScenarioName()
        );

        response.setObjective(
                scenario.getObjective()
        );

        response.setDescription(
                scenario.getDescription()
        );

        response.setCategory(
                scenario.getCategory()
        );

        response.setCriticality(
                scenario.getCriticality()
        );

        response.setStatus(
                scenario.getStatus()
        );

        response.setOwner(
                scenario.getOwner()
        );

        response.setVersion(
                scenario.getVersion()
        );

        response.setCorrelationWindowMinutes(
                scenario.getCorrelationWindowMinutes()
        );

        response.setMaximumExecutionTimeSeconds(
                scenario.getMaximumExecutionTimeSeconds()
        );

        response.setMinimumEvents(
                scenario.getMinimumEvents()
        );

        response.setMinimumConfidence(
                scenario.getMinimumConfidence()
        );

        response.setMinimumEvidence(
                scenario.getMinimumEvidence()
        );

        response.setRequiredRules(
                scenario.getRequiredRules()
        );

        response.setRequiredVariables(
                scenario.getRequiredVariables()
        );

        response.setEvidenceRequirements(
                scenario.getEvidenceRequirements()
        );

        response.setExclusions(
                scenario.getExclusions()
        );

        response.setExceptions(
                scenario.getExceptions()
        );

        response.setSuggestedActions(
                scenario.getSuggestedActions()
        );

        response.setRelatedScenarios(
                scenario.getRelatedScenarios()
        );

        response.setConfigurationContext(
                scenario.getConfigurationContext()
        );

        response.setCreatedAt(
                scenario.getCreatedAt()
        );

        response.setUpdatedAt(
                scenario.getUpdatedAt()
        );

        return response;
    }
}