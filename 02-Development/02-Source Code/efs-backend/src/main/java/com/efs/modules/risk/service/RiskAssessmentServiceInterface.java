package com.efs.modules.risk.service;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface RiskAssessmentServiceInterface {

    String RISK_ASSESSMENT_VIEW_PERMISSION =
            "risk.assessment.view";

    RiskAssessmentResponse createRiskAssessment(
            RiskAssessmentRequest request
    );

    RiskAssessmentResponse getRiskAssessmentById(
            UUID riskAssessmentId
    );

    RiskAssessmentResponse getRiskAssessmentById(
            UUID riskAssessmentId,
            SecurityContext securityContext
    );

    List<RiskAssessmentResponse> getAssessmentsByTransaction(
            UUID transactionId
    );

    List<RiskAssessmentResponse> getAssessmentsByTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );

    RiskAssessmentResponse getLatestAssessmentByTransaction(
            UUID transactionId
    );

    RiskAssessmentResponse getLatestAssessmentByTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );

    List<RiskAssessmentResponse> getAssessmentsByTransactionAndType(
            UUID transactionId,
            String assessmentType
    );

    List<RiskAssessmentResponse> getAssessmentsByTransactionAndType(
            UUID transactionId,
            String assessmentType,
            SecurityContext securityContext
    );

    List<RiskAssessmentResponse> getAssessmentsByRiskLevel(
            String riskLevel
    );

    List<RiskAssessmentResponse> getAssessmentsByResult(
            String assessmentResult
    );

    PageResponse<RiskAssessmentResponse> searchAssessments(
            String riskLevel,
            String assessmentResult,
            int page,
            int size,
            String sort,
            String direction
    );

    PageResponse<RiskAssessmentResponse> searchAssessments(
            String riskLevel,
            String assessmentResult,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext
    );
}