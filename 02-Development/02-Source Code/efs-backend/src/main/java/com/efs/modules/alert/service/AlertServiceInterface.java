package com.efs.modules.alert.service;

import com.efs.modules.alert.dto.AlertAssignmentRequest;
import com.efs.modules.alert.dto.AlertClosureRequest;
import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.dto.AlertStatusUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AlertServiceInterface {

    AlertResponse createAlert(
            AlertRequest request
    );

    AlertResponse getAlertById(
            UUID alertId
    );

    AlertResponse updateAlertStatus(
            UUID alertId,
            AlertStatusUpdateRequest request
    );

    AlertResponse assignAlert(
            UUID alertId,
            AlertAssignmentRequest request
    );

    AlertResponse closeAlert(
            UUID alertId,
            AlertClosureRequest request
    );

    List<AlertHistoryResponse> getAlertHistory(
            UUID alertId
    );

    List<AlertResponse> getAlertsByTransactionId(
            UUID transactionId
    );

    List<AlertResponse> getAlertsByDecisionId(
            UUID decisionId
    );

    List<AlertResponse> getAlertsByStatus(
            String status
    );

    List<AlertResponse> getAlertsByPriority(
            String priority
    );

    List<AlertResponse> getAlertsByType(
            String alertType
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
            String direction
    );
}
