package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.modules.detection.entity.ScenarioActivation;
import org.springframework.stereotype.Component;

@Component
public class ScenarioActivationMapper {

    public ScenarioActivation toEntity(
            ScenarioActivationRequest request) {

        ScenarioActivation activation =
                new ScenarioActivation();

        activation.setScenarioId(
                request.getScenarioId()
        );

        activation.setScenarioVersionId(
                request.getScenarioVersionId()
        );

        activation.setTransactionId(
                request.getTransactionId()
        );

        activation.setCustomerId(
                request.getCustomerId()
        );

        activation.setActivationStatus(
                request.getActivationStatus()
        );

        activation.setSeverity(
                request.getSeverity()
        );

        activation.setConfidence(
                request.getConfidence()
        );

        activation.setRiskScore(
                request.getRiskScore()
        );

        activation.setActivationReason(
                request.getActivationReason()
        );

        activation.setDecisionContext(
                request.getDecisionContext()
        );

        return activation;
    }

    public ScenarioActivationResponse toResponse(
            ScenarioActivation activation) {

        ScenarioActivationResponse response =
                new ScenarioActivationResponse();

        response.setActivationId(
                activation.getActivationId()
        );

        response.setScenarioId(
                activation.getScenarioId()
        );

        response.setScenarioVersionId(
                activation.getScenarioVersionId()
        );

        response.setTransactionId(
                activation.getTransactionId()
        );

        response.setCustomerId(
                activation.getCustomerId()
        );

        response.setActivationStatus(
                activation.getActivationStatus()
        );

        response.setSeverity(
                activation.getSeverity()
        );

        response.setConfidence(
                activation.getConfidence()
        );

        response.setRiskScore(
                activation.getRiskScore()
        );

        response.setTriggeredAt(
                activation.getTriggeredAt()
        );

        response.setResolvedAt(
                activation.getResolvedAt()
        );

        response.setActivationReason(
                activation.getActivationReason()
        );

        response.setDecisionContext(
                activation.getDecisionContext()
        );

        response.setCreatedAt(
                activation.getCreatedAt()
        );

        return response;
    }
}