package com.efs.modules.casemanagement.service;

import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.dto.CaseCommentRequest;
import com.efs.modules.casemanagement.dto.CaseCommentResponse;
import com.efs.modules.casemanagement.dto.CaseEscalationRequest;
import com.efs.modules.casemanagement.dto.CaseEscalationResponse;
import com.efs.modules.casemanagement.dto.CaseEvidenceRequest;
import com.efs.modules.casemanagement.dto.CaseEvidenceResponse;
import com.efs.modules.casemanagement.dto.CaseFromAlertRequest;
import com.efs.modules.casemanagement.dto.CaseHistoryRequest;
import com.efs.modules.casemanagement.dto.CaseHistoryResponse;
import com.efs.modules.casemanagement.dto.CaseNotificationRequest;
import com.efs.modules.casemanagement.dto.CaseNotificationResponse;
import com.efs.modules.casemanagement.dto.CaseRequest;
import com.efs.modules.casemanagement.dto.CaseResolutionRequest;
import com.efs.modules.casemanagement.dto.CaseResolutionResponse;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.dto.CaseSlaRequest;
import com.efs.modules.casemanagement.dto.CaseSlaResponse;
import com.efs.modules.casemanagement.dto.CaseStatusHistoryResponse;
import com.efs.modules.casemanagement.dto.CaseStatusUpdateRequest;
import com.efs.modules.casemanagement.dto.CaseTaskRequest;
import com.efs.modules.casemanagement.dto.CaseTaskResponse;

import java.util.List;
import java.util.UUID;

public interface CaseServiceInterface {

    CaseResponse createCase(
            CaseRequest request
    );

    CaseResponse createCaseFromAlert(
            CaseFromAlertRequest request
    );

    CaseAssignmentResponse assignCase(
            UUID caseId,
            CaseAssignmentRequest request
    );

    List<CaseAssignmentResponse> getCaseAssignments(
            UUID caseId
    );

    CaseTaskResponse createCaseTask(
            UUID caseId,
            CaseTaskRequest request
    );

    CaseTaskResponse getCaseTaskById(
            UUID caseId,
            UUID taskId
    );

    List<CaseTaskResponse> getCaseTasks(
            UUID caseId
    );

    CaseCommentResponse createCaseComment(
            UUID caseId,
            CaseCommentRequest request
    );

    CaseCommentResponse getCaseCommentById(
            UUID caseId,
            UUID commentId
    );

    List<CaseCommentResponse> getCaseComments(
            UUID caseId
    );

    CaseEvidenceResponse createCaseEvidence(
            UUID caseId,
            CaseEvidenceRequest request
    );

    CaseEvidenceResponse getCaseEvidenceById(
            UUID caseId,
            UUID evidenceId
    );

    List<CaseEvidenceResponse> getCaseEvidence(
            UUID caseId
    );

    CaseResponse updateCaseStatus(
            UUID caseId,
            CaseStatusUpdateRequest request
    );

    List<CaseStatusHistoryResponse> getCaseStatusHistory(
            UUID caseId
    );

    CaseResolutionResponse createCaseResolution(
            UUID caseId,
            CaseResolutionRequest request
    );

    CaseResolutionResponse getCaseResolutionById(
            UUID caseId,
            UUID resolutionId
    );

    List<CaseResolutionResponse> getCaseResolutions(
            UUID caseId
    );

    CaseEscalationResponse createCaseEscalation(
            UUID caseId,
            CaseEscalationRequest request
    );

    CaseEscalationResponse getCaseEscalationById(
            UUID caseId,
            UUID escalationId
    );

    List<CaseEscalationResponse> getCaseEscalations(
            UUID caseId
    );

    CaseSlaResponse createCaseSla(
            UUID caseId,
            CaseSlaRequest request
    );

    CaseSlaResponse getCaseSlaById(
            UUID caseId,
            UUID slaId
    );

    List<CaseSlaResponse> getCaseSlas(
            UUID caseId
    );

    CaseNotificationResponse createCaseNotification(
            UUID caseId,
            CaseNotificationRequest request
    );

    CaseNotificationResponse getCaseNotificationById(
            UUID caseId,
            UUID caseNotificationId
    );

    List<CaseNotificationResponse> getCaseNotifications(
            UUID caseId
    );

    CaseHistoryResponse createCaseHistory(
            UUID caseId,
            CaseHistoryRequest request
    );

    CaseHistoryResponse getCaseHistoryById(
            UUID caseId,
            UUID historyId
    );

    List<CaseHistoryResponse> getCaseHistory(
            UUID caseId
    );

    CaseResponse getCaseById(
            UUID caseId
    );

    CaseResponse getCaseByNumber(
            String caseNumber
    );

    List<CaseResponse> getCasesByCustomerId(
            UUID customerId
    );

    List<CaseResponse> getCasesByTransactionId(
            UUID transactionId
    );

    List<CaseResponse> getCasesByStatus(
            String currentStatus
    );

    List<CaseResponse> getCasesByPriority(
            String priority
    );

    List<CaseResponse> getCasesByAssignedUser(
            UUID assignedUser
    );

    List<CaseResponse> getCasesByAssignedTeam(
            String assignedTeam
    );
}