package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import org.springframework.stereotype.Component;

@Component
public class ScenarioEvaluationMapper {

    public ScenarioEvaluation toEntity(
            ScenarioEvaluationRequest request) {

        ScenarioEvaluation evaluation =
                new ScenarioEvaluation();

        evaluation.setScenarioId(
                request.getScenarioId()
        );

        evaluation.setScenarioVersionId(
                request.getScenarioVersionId()
        );

        evaluation.setTransactionId(
                request.getTransactionId()
        );

        evaluation.setCustomerId(
                request.getCustomerId()
        );

        evaluation.setEvaluationStatus(
                request.getEvaluationStatus()
        );

        evaluation.setMatched(
                request.getMatched()
        );

        evaluation.setRuleCount(
                request.getRuleCount()
        );

        evaluation.setMatchedRuleCount(
                request.getMatchedRuleCount()
        );

        evaluation.setRequiredEvidenceCount(
                request.getRequiredEvidenceCount()
        );

        evaluation.setAvailableEvidenceCount(
                request.getAvailableEvidenceCount()
        );

        evaluation.setConfidence(
                request.getConfidence()
        );

        evaluation.setRiskContribution(
                request.getRiskContribution()
        );

        evaluation.setEvaluationDurationMs(
                request.getEvaluationDurationMs()
        );

        evaluation.setEvaluationContext(
                request.getEvaluationContext()
        );

        return evaluation;
    }

    public ScenarioEvaluationResponse toResponse(
            ScenarioEvaluation evaluation) {

        ScenarioEvaluationResponse response =
                new ScenarioEvaluationResponse();

        response.setEvaluationId(
                evaluation.getEvaluationId()
        );

        response.setScenarioId(
                evaluation.getScenarioId()
        );

        response.setScenarioVersionId(
                evaluation.getScenarioVersionId()
        );

        response.setTransactionId(
                evaluation.getTransactionId()
        );

        response.setCustomerId(
                evaluation.getCustomerId()
        );

        response.setEvaluationStatus(
                evaluation.getEvaluationStatus()
        );

        response.setMatched(
                evaluation.getMatched()
        );

        response.setRuleCount(
                evaluation.getRuleCount()
        );

        response.setMatchedRuleCount(
                evaluation.getMatchedRuleCount()
        );

        response.setRequiredEvidenceCount(
                evaluation.getRequiredEvidenceCount()
        );

        response.setAvailableEvidenceCount(
                evaluation.getAvailableEvidenceCount()
        );

        response.setConfidence(
                evaluation.getConfidence()
        );

        response.setRiskContribution(
                evaluation.getRiskContribution()
        );

        response.setEvaluatedAt(
                evaluation.getEvaluatedAt()
        );

        response.setEvaluationDurationMs(
                evaluation.getEvaluationDurationMs()
        );

        response.setEvaluationContext(
                evaluation.getEvaluationContext()
        );

        response.setCreatedAt(
                evaluation.getCreatedAt()
        );

        return response;
    }
}