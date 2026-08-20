package com.efs.modules.risk.service;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;

import java.util.List;
import java.util.UUID;

public interface RiskAssessmentServiceInterface {

    RiskAssessmentResponse createRiskAssessment(
            RiskAssessmentRequest request
    );

    RiskAssessmentResponse getRiskAssessmentById(
            UUID riskAssessmentId
    );

    List<RiskAssessmentResponse> getAssessmentsByTransaction(
            UUID transactionId
    );

    RiskAssessmentResponse getLatestAssessmentByTransaction(
            UUID transactionId
    );

    List<RiskAssessmentResponse> getAssessmentsByTransactionAndType(
            UUID transactionId,
            String assessmentType
    );

    List<RiskAssessmentResponse> getAssessmentsByRiskLevel(
            String riskLevel
    );

    List<RiskAssessmentResponse> getAssessmentsByResult(
            String assessmentResult
    );
}