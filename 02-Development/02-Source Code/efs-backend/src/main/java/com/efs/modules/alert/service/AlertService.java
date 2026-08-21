package com.efs.modules.alert.service;

import com.efs.modules.alert.dto.AlertAssignmentRequest;
import com.efs.modules.alert.dto.AlertClosureRequest;
import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.dto.AlertStatusUpdateRequest;
import com.efs.modules.alert.entity.Alert;
import com.efs.modules.alert.entity.AlertHistory;
import com.efs.modules.alert.mapper.AlertHistoryMapper;
import com.efs.modules.alert.mapper.AlertMapper;
import com.efs.modules.alert.repository.AlertHistoryRepository;
import com.efs.modules.alert.repository.AlertRepository;
import com.efs.modules.alert.validator.AlertStatusValidator;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionDecision;
import com.efs.modules.transaction.repository.TransactionDecisionRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService
        implements AlertServiceInterface {

    private static final String INITIAL_STATUS =
            "NEW";

    private static final String CLOSED_STATUS =
            "CLOSED";

    private static final String STATUS_CHANGE_ACTION =
            "STATUS_CHANGE";

    private static final String ASSIGNMENT_ACTION =
            "ASSIGNMENT";

    private static final String CLOSURE_ACTION =
            "CLOSURE";

    private final AlertRepository alertRepository;

    private final AlertHistoryRepository
            alertHistoryRepository;

    private final TransactionDecisionRepository
            transactionDecisionRepository;

    private final TransactionRepository
            transactionRepository;

    private final AlertMapper alertMapper;

    private final AlertHistoryMapper
            alertHistoryMapper;

    private final AlertStatusValidator
            alertStatusValidator;

    public AlertService(
            AlertRepository alertRepository,
            AlertHistoryRepository alertHistoryRepository,
            TransactionDecisionRepository transactionDecisionRepository,
            TransactionRepository transactionRepository,
            AlertMapper alertMapper,
            AlertHistoryMapper alertHistoryMapper,
            AlertStatusValidator alertStatusValidator) {

        this.alertRepository =
                alertRepository;

        this.alertHistoryRepository =
                alertHistoryRepository;

        this.transactionDecisionRepository =
                transactionDecisionRepository;

        this.transactionRepository =
                transactionRepository;

        this.alertMapper =
                alertMapper;

        this.alertHistoryMapper =
                alertHistoryMapper;

        this.alertStatusValidator =
                alertStatusValidator;
    }

    @Override
    @Transactional
    public AlertResponse createAlert(
            AlertRequest request) {

        TransactionDecision decision =
                transactionDecisionRepository
                        .findByDecisionId(
                                request.getDecisionId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction decision not found: "
                                                + request.getDecisionId()
                                )
                        );

        validateDecisionContext(
                request,
                decision
        );

        Transaction transaction =
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                decision.getTransactionId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found: "
                                                + decision.getTransactionId()
                                )
                        );

        Alert alert =
                alertMapper.toEntity(
                        request
                );

        alert.setTransactionId(
                decision.getTransactionId()
        );

        alert.setRiskAssessmentId(
                decision.getRiskAssessmentId()
        );

        if (alert.getCustomerId() == null) {
            alert.setCustomerId(
                    transaction.getCustomerId()
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        alert.setStatus(
                INITIAL_STATUS
        );

        alert.setGeneratedAt(
                now
        );

        alert.setCreatedAt(
                now
        );

        alert.setUpdatedAt(
                now
        );

        Alert savedAlert =
                alertRepository.save(
                        alert
                );

        return alertMapper.toResponse(
                savedAlert
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AlertResponse getAlertById(
            UUID alertId) {

        Alert alert =
                getExistingAlert(
                        alertId
                );

        return alertMapper.toResponse(
                alert
        );
    }

    @Override
    @Transactional
    public AlertResponse updateAlertStatus(
            UUID alertId,
            AlertStatusUpdateRequest request) {

        Alert alert =
                getExistingAlert(
                        alertId
                );

        ensureAlertIsOpen(
                alert
        );

        if (!alertStatusValidator.isValidStatus(
                request.getStatus())) {

            throw new IllegalArgumentException(
                    "Unsupported alert status: "
                            + request.getStatus()
            );
        }

        String newStatus =
                alertStatusValidator.normalize(
                        request.getStatus()
                );

        if (CLOSED_STATUS.equals(
                newStatus)) {

            throw new IllegalArgumentException(
                    "Alert closure must use the formal closure operation"
            );
        }

        String previousStatus =
                alert.getStatus();

        LocalDateTime now =
                LocalDateTime.now();

        alert.setStatus(
                newStatus
        );

        alert.setUpdatedAt(
                now
        );

        Alert savedAlert =
                alertRepository.save(
                        alert
                );

        AlertHistory history =
                new AlertHistory();

        history.setAlertId(
                alertId
        );

        history.setActionType(
                STATUS_CHANGE_ACTION
        );

        history.setPreviousStatus(
                previousStatus
        );

        history.setNewStatus(
                newStatus
        );

        history.setChangedBy(
                request.getChangedBy()
        );

        history.setChangeReason(
                request.getChangeReason()
        );

        history.setChangedAt(
                now
        );

        alertHistoryRepository.save(
                history
        );

        return alertMapper.toResponse(
                savedAlert
        );
    }

    @Override
    @Transactional
    public AlertResponse assignAlert(
            UUID alertId,
            AlertAssignmentRequest request) {

        Alert alert =
                getExistingAlert(
                        alertId
                );

        ensureAlertIsOpen(
                alert
        );

        LocalDateTime now =
                LocalDateTime.now();

        alert.setAssignedTo(
                request.getAssignedTo()
        );

        alert.setAssignedTeam(
                request.getAssignedTeam()
        );

        alert.setUpdatedAt(
                now
        );

        Alert savedAlert =
                alertRepository.save(
                        alert
                );

        AlertHistory history =
                new AlertHistory();

        history.setAlertId(
                alertId
        );

        history.setActionType(
                ASSIGNMENT_ACTION
        );

        history.setPreviousStatus(
                alert.getStatus()
        );

        history.setNewStatus(
                alert.getStatus()
        );

        history.setChangedBy(
                request.getChangedBy()
        );

        history.setChangeReason(
                request.getChangeReason()
        );

        history.setChangedAt(
                now
        );

        alertHistoryRepository.save(
                history
        );

        return alertMapper.toResponse(
                savedAlert
        );
    }

    @Override
    @Transactional
    public AlertResponse closeAlert(
            UUID alertId,
            AlertClosureRequest request) {

        Alert alert =
                getExistingAlert(
                        alertId
                );

        ensureAlertIsOpen(
                alert
        );

        String previousStatus =
                alert.getStatus();

        LocalDateTime now =
                LocalDateTime.now();

        alert.setStatus(
                CLOSED_STATUS
        );

        alert.setClosedAt(
                now
        );

        alert.setClosureReason(
                request.getClosureReason()
        );

        alert.setUpdatedAt(
                now
        );

        Alert savedAlert =
                alertRepository.save(
                        alert
                );

        AlertHistory history =
                new AlertHistory();

        history.setAlertId(
                alertId
        );

        history.setActionType(
                CLOSURE_ACTION
        );

        history.setPreviousStatus(
                previousStatus
        );

        history.setNewStatus(
                CLOSED_STATUS
        );

        history.setChangedBy(
                request.getClosedBy()
        );

        history.setChangeReason(
                request.getInvestigationResult()
                        + " | "
                        + request.getClosureReason()
        );

        history.setChangedAt(
                now
        );

        alertHistoryRepository.save(
                history
        );

        return alertMapper.toResponse(
                savedAlert
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertHistoryResponse> getAlertHistory(
            UUID alertId) {

        getExistingAlert(
                alertId
        );

        return alertHistoryRepository
                .findByAlertIdOrderByChangedAtDesc(
                        alertId
                )
                .stream()
                .map(alertHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByTransactionId(
            UUID transactionId) {

        return alertRepository
                .findByTransactionIdOrderByGeneratedAtDesc(
                        transactionId
                )
                .stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByDecisionId(
            UUID decisionId) {

        return alertRepository
                .findByDecisionIdOrderByGeneratedAtDesc(
                        decisionId
                )
                .stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByStatus(
            String status) {

        String normalizedStatus =
                alertStatusValidator.normalize(
                        status
                );

        return alertRepository
                .findByStatusOrderByGeneratedAtDesc(
                        normalizedStatus
                )
                .stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByPriority(
            String priority) {

        return alertRepository
                .findByPriorityOrderByGeneratedAtDesc(
                        priority
                )
                .stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByType(
            String alertType) {

        return alertRepository
                .findByAlertTypeOrderByGeneratedAtDesc(
                        alertType
                )
                .stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    private Alert getExistingAlert(
            UUID alertId) {

        return alertRepository
                .findByAlertId(
                        alertId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Alert not found: "
                                        + alertId
                        )
                );
    }

    private void ensureAlertIsOpen(
            Alert alert) {

        if (CLOSED_STATUS.equals(
                alert.getStatus())) {

            throw new IllegalStateException(
                    "Closed alert cannot be modified"
            );
        }
    }

    private void validateDecisionContext(
            AlertRequest request,
            TransactionDecision decision) {

        if (request.getTransactionId() != null
                && !request.getTransactionId().equals(
                        decision.getTransactionId()
                )) {

            throw new IllegalArgumentException(
                    "Alert transaction does not match "
                            + "the transaction decision"
            );
        }

        if (request.getRiskAssessmentId() != null
                && !request.getRiskAssessmentId().equals(
                        decision.getRiskAssessmentId()
                )) {

            throw new IllegalArgumentException(
                    "Alert risk assessment does not match "
                            + "the transaction decision"
            );
        }
    }
}