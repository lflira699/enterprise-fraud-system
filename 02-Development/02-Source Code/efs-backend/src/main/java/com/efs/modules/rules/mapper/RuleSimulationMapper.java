package com.efs.modules.rules.mapper;

import com.efs.modules.rules.dto.RuleSimulationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.entity.RuleSimulation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleSimulationMapper {

    public RuleSimulation toEntity(
            RuleSimulationRequest request) {

        RuleSimulation simulation =
                new RuleSimulation();

        simulation.setSimulationName(
                request.getSimulationName()
        );

        simulation.setEntityType(
                request.getEntityType()
        );

        simulation.setEntityId(
                request.getEntityId()
        );

        simulation.setDatasetReference(
                request.getDatasetReference()
        );

        simulation.setSampleSize(
                request.getSampleSize()
        );

        simulation.setSimulationStatus(
                request.getSimulationStatus()
        );

        simulation.setMatchCount(
                request.getMatchCount()
        );

        simulation.setApproveCount(
                request.getApproveCount()
        );

        simulation.setRejectCount(
                request.getRejectCount()
        );

        simulation.setReviewCount(
                request.getReviewCount()
        );

        if (request.getResultSummary() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> resultSummary =
                    (Map<String, Object>) request.getResultSummary();

            simulation.setResultSummary(resultSummary);
        }

        simulation.setExecutedBy(
                request.getExecutedBy()
        );

        return simulation;
    }

    public RuleSimulationResponse toResponse(
            RuleSimulation simulation) {

        RuleSimulationResponse response =
                new RuleSimulationResponse();

        response.setSimulationId(
                simulation.getSimulationId()
        );

        response.setSimulationName(
                simulation.getSimulationName()
        );

        response.setEntityType(
                simulation.getEntityType()
        );

        response.setEntityId(
                simulation.getEntityId()
        );

        response.setDatasetReference(
                simulation.getDatasetReference()
        );

        response.setSampleSize(
                simulation.getSampleSize()
        );

        response.setStartedAt(
                simulation.getStartedAt()
        );

        response.setCompletedAt(
                simulation.getCompletedAt()
        );

        response.setSimulationStatus(
                simulation.getSimulationStatus()
        );

        response.setMatchCount(
                simulation.getMatchCount()
        );

        response.setApproveCount(
                simulation.getApproveCount()
        );

        response.setRejectCount(
                simulation.getRejectCount()
        );

        response.setReviewCount(
                simulation.getReviewCount()
        );

        response.setResultSummary(
                simulation.getResultSummary()
        );

        response.setExecutedBy(
                simulation.getExecutedBy()
        );

        response.setCreatedAt(
                simulation.getCreatedAt()
        );

        return response;
    }
}