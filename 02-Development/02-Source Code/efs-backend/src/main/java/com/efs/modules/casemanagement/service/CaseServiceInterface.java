package com.efs.modules.casemanagement.service;

import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.dto.CaseFromAlertRequest;
import com.efs.modules.casemanagement.dto.CaseRequest;
import com.efs.modules.casemanagement.dto.CaseResponse;

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