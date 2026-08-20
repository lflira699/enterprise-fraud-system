package com.efs.modules.risk.mapper;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.modules.risk.entity.RiskAssessment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RiskAssessmentMapper {

    public RiskAssessment toEntity(
            RiskAssessmentRequest request) {

        RiskAssessment assessment =
                new RiskAssessment();

        assessment.setTransactionId(
                request.getTransactionId()
        );

        assessment.setAssessmentType(
                request.getAssessmentType()
        );

        assessment.setAssessmentStage(
                request.getAssessmentStage()
        );

        assessment.setOverallRiskScore(
                request.getOverallRiskScore()
        );

        assessment.setRiskLevel(
                request.getRiskLevel()
        );

        assessment.setRiskCategory(
                request.getRiskCategory()
        );

        assessment.setAssessmentResult(
                request.getAssessmentResult()
        );

        assessment.setRulesScore(
                request.getRulesScore()
        );

        assessment.setMachineLearningScore(
                request.getMachineLearningScore()
        );

        assessment.setBehavioralScore(
                request.getBehavioralScore()
        );

        assessment.setCustomerScore(
                request.getCustomerScore()
        );

        assessment.setGeographicScore(
                request.getGeographicScore()
        );

        assessment.setDeviceScore(
                request.getDeviceScore()
        );

        assessment.setConfidenceScore(
                request.getConfidenceScore()
        );

        assessment.setModelName(
                request.getModelName()
        );

        assessment.setModelVersion(
                request.getModelVersion()
        );

        assessment.setProcessingTimeMs(
                request.getProcessingTimeMs()
        );

        assessment.setAssessmentDetails(
                request.getAssessmentDetails()
        );

        assessment.setCreatedBy(
                request.getCreatedBy()
        );

        assessment.setUpdatedBy(
                request.getUpdatedBy()
        );

        LocalDateTime now =
                LocalDateTime.now();

        assessment.setAssessmentTimestamp(now);
        assessment.setCreatedAt(now);
        assessment.setUpdatedAt(now);

        return assessment;
    }

    public RiskAssessmentResponse toResponse(
            RiskAssessment assessment) {

        RiskAssessmentResponse response =
                new RiskAssessmentResponse();

        response.setRiskAssessmentId(
                assessment.getRiskAssessmentId()
        );

        response.setTransactionId(
                assessment.getTransactionId()
        );

        response.setAssessmentType(
                assessment.getAssessmentType()
        );

        response.setAssessmentStage(
                assessment.getAssessmentStage()
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

        response.setRulesScore(
                assessment.getRulesScore()
        );

        response.setMachineLearningScore(
                assessment.getMachineLearningScore()
        );

        response.setBehavioralScore(
                assessment.getBehavioralScore()
        );

        response.setCustomerScore(
                assessment.getCustomerScore()
        );

        response.setGeographicScore(
                assessment.getGeographicScore()
        );

        response.setDeviceScore(
                assessment.getDeviceScore()
        );

        response.setConfidenceScore(
                assessment.getConfidenceScore()
        );

        response.setModelName(
                assessment.getModelName()
        );

        response.setModelVersion(
                assessment.getModelVersion()
        );

        response.setAssessmentTimestamp(
                assessment.getAssessmentTimestamp()
        );

        response.setProcessingTimeMs(
                assessment.getProcessingTimeMs()
        );

        response.setAssessmentDetails(
                assessment.getAssessmentDetails()
        );

        response.setCreatedAt(
                assessment.getCreatedAt()
        );

        response.setCreatedBy(
                assessment.getCreatedBy()
        );

        response.setUpdatedAt(
                assessment.getUpdatedAt()
        );

        response.setUpdatedBy(
                assessment.getUpdatedBy()
        );

        response.setDeletedAt(
                assessment.getDeletedAt()
        );

        response.setDeletedBy(
                assessment.getDeletedBy()
        );

        response.setRecordVersion(
                assessment.getRecordVersion()
        );

        return response;
    }
}