package com.efs.modules.risk.mapper;

import com.efs.modules.risk.dto.EventRiskAssessmentResponse;
import com.efs.modules.risk.entity.EventRiskAssessment;
import org.springframework.stereotype.Component;

@Component
public class EventRiskAssessmentMapper {

    public EventRiskAssessmentResponse toResponse(
            EventRiskAssessment assessment) {

        EventRiskAssessmentResponse response =
                new EventRiskAssessmentResponse();

        response.setEventRiskAssessmentId(
                assessment.getEventRiskAssessmentId()
        );

        response.setFraudEventId(
                assessment.getFraudEventId()
        );

        response.setOrganizationId(
                assessment.getOrganizationId()
        );

        response.setTenantId(
                assessment.getTenantId()
        );

        response.setCorrelationId(
                assessment.getCorrelationId()
        );

        response.setModelId(
                assessment.getModelId()
        );

        response.setModelVersion(
                assessment.getModelVersion()
        );

        response.setOverallRiskScore(
                assessment.getOverallRiskScore()
        );

        response.setRiskLevel(
                assessment.getRiskLevel()
        );

        response.setRiskCategory(
                assessment.getRiskCategory()
        );

        response.setAssessmentResult(
                assessment.getAssessmentResult()
        );

        response.setAssessmentDetails(
                assessment.getAssessmentDetails()
        );

        response.setAssessmentTimestamp(
                assessment.getAssessmentTimestamp()
        );

        response.setProcessingTimeMs(
                assessment.getProcessingTimeMs()
        );

        return response;
    }
}
