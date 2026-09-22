package com.efs.modules.alert.service;

import com.efs.modules.alert.dto.AlertAssignmentRequest;
import com.efs.modules.alert.dto.AlertClosureRequest;
import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.dto.AlertStatusUpdateRequest;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AlertAccessServiceInterface {

    AlertResponse createAlert(
            AlertRequest request,
            SecurityContext securityContext
    );

    AlertResponse getAlertById(
            UUID alertId,
            SecurityContext securityContext
    );

    AlertResponse updateAlertStatus(
            UUID alertId,
            AlertStatusUpdateRequest request,
            SecurityContext securityContext
    );

    AlertResponse assignAlert(
            UUID alertId,
            AlertAssignmentRequest request,
            SecurityContext securityContext
    );

    AlertResponse closeAlert(
            UUID alertId,
            AlertClosureRequest request,
            SecurityContext securityContext
    );

    List<AlertHistoryResponse> getAlertHistory(
            UUID alertId,
            SecurityContext securityContext
    );

    List<AlertResponse> getAlertsByTransactionId(
            UUID transactionId,
            SecurityContext securityContext
    );

    List<AlertResponse> getAlertsByDecisionId(
            UUID decisionId,
            SecurityContext securityContext
    );
    PageResponse<AlertResponse> searchAlerts(
            String status,
            String priority,
            String riskLevel,
            UUID assignedTo,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            UUID customerId,
            String scenarioCode,
            UUID caseId,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext
    );
}