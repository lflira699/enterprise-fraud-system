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
import com.efs.modules.casemanagement.repository.CaseAlertRepository;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionDecision;
import com.efs.modules.transaction.repository.TransactionDecisionRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.AlertConcurrentModificationException;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import com.efs.shared.pagination.PageResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

    private static final int MAX_PAGE_SIZE =
            100;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "generatedAt",
                    "priorityScore",
                    "riskScore",
                    "dueAt"
            );

    private final AlertRepository alertRepository;

    private final AlertHistoryRepository
            alertHistoryRepository;

    private final TransactionDecisionRepository
            transactionDecisionRepository;

    private final TransactionRepository
            transactionRepository;

    private final RiskAssessmentRepository
            riskAssessmentRepository;

    private final DetectionScenarioRepository
            detectionScenarioRepository;

    private final CaseAlertRepository
            caseAlertRepository;

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
            RiskAssessmentRepository riskAssessmentRepository,
            DetectionScenarioRepository detectionScenarioRepository,
            CaseAlertRepository caseAlertRepository,
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

        this.riskAssessmentRepository =
                riskAssessmentRepository;

        this.detectionScenarioRepository =
                detectionScenarioRepository;

        this.caseAlertRepository =
                caseAlertRepository;

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
                saveMutableAlert(
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

        if (CLOSED_STATUS.equals(
                alert.getStatus())) {

            throw new ValidationException(
                    "Alert is not available for assignment"
            );
        }

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
                saveMutableAlert(
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

        if (CLOSED_STATUS.equals(
                alert.getStatus())) {

            throw new ValidationException(
                    "Alert is already closed"
            );
        }

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
                saveMutableAlert(
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AlertResponse> searchAlerts(
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
            String direction) {

        validatePagination(
                page,
                size,
                sort,
                direction
        );

        validateDateRange(
                createdFrom,
                createdTo
        );

        String normalizedStatus =
                null;

        if (status != null
                && !status.isBlank()) {

            if (!alertStatusValidator.isValidStatus(
                    status)) {

                throw new RequestValidationException(
                        "Unsupported alert status: "
                                + status
                );
            }

            normalizedStatus =
                    alertStatusValidator.normalize(
                            status
                    );
        }

        List<UUID> riskAssessmentIds =
                null;

        if (riskLevel != null
                && !riskLevel.isBlank()) {

            riskAssessmentIds =
                    riskAssessmentRepository
                            .findByRiskLevelOrderByAssessmentTimestampDesc(
                                    riskLevel
                            )
                            .stream()
                            .map(assessment ->
                                    assessment.getRiskAssessmentId()
                            )
                            .toList();

            if (riskAssessmentIds.isEmpty()) {
                return emptyPageResponse(
                        page,
                        size
                );
            }
        }

        List<UUID> scenarioIds =
                null;

        if (scenarioCode != null
                && !scenarioCode.isBlank()) {

            scenarioIds =
                    detectionScenarioRepository
                            .findByScenarioCodeOrderByVersionDesc(
                                    scenarioCode
                            )
                            .stream()
                            .map(scenario ->
                                    scenario.getScenarioId()
                            )
                            .toList();

            if (scenarioIds.isEmpty()) {
                return emptyPageResponse(
                        page,
                        size
                );
            }
        }

        List<UUID> caseAlertIds =
                null;

        if (caseId != null) {

            caseAlertIds =
                    caseAlertRepository
                            .findByCaseIdOrderByGeneratedAtDesc(
                                    caseId
                            )
                            .stream()
                            .map(caseAlert ->
                                    caseAlert.getSourceAlertId()
                            )
                            .filter(sourceAlertId ->
                                    sourceAlertId != null
                            )
                            .toList();

            if (caseAlertIds.isEmpty()) {
                return emptyPageResponse(
                        page,
                        size
                );
            }
        }

        Specification<Alert> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (normalizedStatus != null) {

            String filterStatus =
                    normalizedStatus;

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("status"),
                                            filterStatus
                                    )
                    );
        }

        if (priority != null
                && !priority.isBlank()) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("priority"),
                                            priority
                                    )
                    );
        }

        if (assignedTo != null) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("assignedTo"),
                                            assignedTo
                                    )
                    );
        }

        if (createdFrom != null) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder
                                            .greaterThanOrEqualTo(
                                                    root.<LocalDateTime>get(
                                                            "createdAt"
                                                    ),
                                                    createdFrom
                                            )
                    );
        }

        if (createdTo != null) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder
                                            .lessThanOrEqualTo(
                                                    root.<LocalDateTime>get(
                                                            "createdAt"
                                                    ),
                                                    createdTo
                                            )
                    );
        }

        if (customerId != null) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("customerId"),
                                            customerId
                                    )
                    );
        }

        if (riskAssessmentIds != null) {

            List<UUID> filterRiskAssessmentIds =
                    riskAssessmentIds;

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    root.<UUID>get(
                                            "riskAssessmentId"
                                    ).in(
                                            filterRiskAssessmentIds
                                    )
                    );
        }

        if (scenarioIds != null) {

            List<UUID> filterScenarioIds =
                    scenarioIds;

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    root.<UUID>get(
                                            "scenarioId"
                                    ).in(
                                            filterScenarioIds
                                    )
                    );
        }

        if (caseAlertIds != null) {

            List<UUID> filterCaseAlertIds =
                    caseAlertIds;

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    root.<UUID>get(
                                            "alertId"
                                    ).in(
                                            filterCaseAlertIds
                                    )
                    );
        }

        Sort.Direction sortDirection =
                Sort.Direction.fromString(
                        direction
                );

        PageRequest pageRequest =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sort
                        )
                );

        Page<Alert> alertPage =
                alertRepository.findAll(
                        specification,
                        pageRequest
                );

        return toPageResponse(
                alertPage
        );
    }

    private Alert saveMutableAlert(
            Alert alert) {

        try {

            return alertRepository
                    .saveAndFlush(
                            alert
                    );

        } catch (OptimisticLockingFailureException exception) {

            throw new AlertConcurrentModificationException(
                    "Alert was modified by another transaction: "
                            + alert.getAlertId(),
                    exception
            );
        }
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

    private void validatePagination(
            int page,
            int size,
            String sort,
            String direction) {

        if (page < 0) {

            throw new RequestValidationException(
                    "Page must be greater than or equal to zero"
            );
        }

        if (size < 1
                || size > MAX_PAGE_SIZE) {

            throw new RequestValidationException(
                    "Page size must be between 1 and "
                            + MAX_PAGE_SIZE
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(
                sort)) {

            throw new RequestValidationException(
                    "Unsupported alert sort field: "
                            + sort
            );
        }

        try {

            Sort.Direction.fromString(
                    direction
            );

        } catch (IllegalArgumentException exception) {

            throw new RequestValidationException(
                    "Unsupported sort direction: "
                            + direction,
                    exception
            );
        }
    }

    private void validateDateRange(
            LocalDateTime createdFrom,
            LocalDateTime createdTo) {

        if (createdFrom != null
                && createdTo != null
                && createdFrom.isAfter(
                        createdTo
                )) {

            throw new RequestValidationException(
                    "createdFrom must be before or equal to createdTo"
            );
        }
    }

    private PageResponse<AlertResponse> toPageResponse(
            Page<Alert> alertPage) {

        List<AlertResponse> content =
                alertPage.getContent()
                        .stream()
                        .map(alertMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                content,
                alertPage.getNumber(),
                alertPage.getSize(),
                alertPage.getTotalElements(),
                alertPage.getTotalPages(),
                alertPage.hasNext(),
                alertPage.hasPrevious()
        );
    }

    private PageResponse<AlertResponse> emptyPageResponse(
            int page,
            int size) {

        return new PageResponse<>(
                List.of(),
                page,
                size,
                0,
                0,
                false,
                false
        );
    }
}