package com.efs.modules.casemanagement.service;

import com.efs.modules.alert.entity.Alert;
import com.efs.modules.alert.repository.AlertRepository;
import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.dto.CaseCommentRequest;
import com.efs.modules.casemanagement.dto.CaseCommentResponse;
import com.efs.modules.casemanagement.dto.CaseEscalationRequest;
import com.efs.modules.casemanagement.dto.CaseEscalationResponse;
import com.efs.modules.casemanagement.dto.CaseEvidenceRequest;
import com.efs.modules.casemanagement.dto.CaseEvidenceResponse;
import com.efs.modules.casemanagement.dto.CaseEvidenceUpdateRequest;
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
import com.efs.modules.casemanagement.dto.CaseUpdateRequest;
import com.efs.modules.casemanagement.dto.CaseTaskRequest;
import com.efs.modules.casemanagement.dto.CaseTaskResponse;
import com.efs.modules.casemanagement.entity.Case;
import com.efs.modules.casemanagement.entity.CaseAlert;
import com.efs.modules.casemanagement.entity.CaseAssignment;
import com.efs.modules.casemanagement.entity.CaseComment;
import com.efs.modules.casemanagement.entity.CaseEscalation;
import com.efs.modules.casemanagement.entity.CaseEvidence;
import com.efs.modules.casemanagement.entity.CaseHistory;
import com.efs.modules.casemanagement.entity.CaseNotification;
import com.efs.modules.casemanagement.entity.CaseResolution;
import com.efs.modules.casemanagement.entity.CaseSla;
import com.efs.modules.casemanagement.entity.CaseStatusHistory;
import com.efs.modules.casemanagement.entity.CaseTask;
import com.efs.modules.casemanagement.mapper.CaseAssignmentMapper;
import com.efs.modules.casemanagement.mapper.CaseCommentMapper;
import com.efs.modules.casemanagement.mapper.CaseEscalationMapper;
import com.efs.modules.casemanagement.mapper.CaseEvidenceMapper;
import com.efs.modules.casemanagement.mapper.CaseHistoryMapper;
import com.efs.modules.casemanagement.mapper.CaseMapper;
import com.efs.modules.casemanagement.mapper.CaseNotificationMapper;
import com.efs.modules.casemanagement.mapper.CaseResolutionMapper;
import com.efs.modules.casemanagement.mapper.CaseSlaMapper;
import com.efs.modules.casemanagement.mapper.CaseStatusHistoryMapper;
import com.efs.modules.casemanagement.mapper.CaseTaskMapper;
import com.efs.modules.casemanagement.repository.CaseAlertRepository;
import com.efs.modules.casemanagement.repository.CaseAssignmentRepository;
import com.efs.modules.casemanagement.repository.CaseCommentRepository;
import com.efs.modules.casemanagement.repository.CaseEscalationRepository;
import com.efs.modules.casemanagement.repository.CaseEvidenceRepository;
import com.efs.modules.casemanagement.repository.CaseHistoryRepository;
import com.efs.modules.casemanagement.repository.CaseNotificationRepository;
import com.efs.modules.casemanagement.repository.CaseRepository;
import com.efs.modules.casemanagement.repository.CaseResolutionRepository;
import com.efs.modules.casemanagement.repository.CaseSlaRepository;
import com.efs.modules.casemanagement.repository.CaseStatusHistoryRepository;
import com.efs.modules.casemanagement.repository.CaseTaskRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class CaseService
        implements CaseServiceInterface {

    private static final String DEFAULT_SEVERITY =
            "MEDIUM";

    private static final String DEFAULT_PRIORITY =
            "NORMAL";

    private static final String INITIAL_STATUS =
            "OPEN";

    private static final String ALERT_SOURCE =
            "ALERT_MANAGEMENT";

    private static final int MAX_PAGE_SIZE =
            100;

    private static final String DEFAULT_CASE_SORT =
            "createdAt";

    private static final String SORT_DIRECTION_ASC =
            "ASC";

    private static final String SORT_DIRECTION_DESC =
            "DESC";

    private static final String CASE_VIEW_PERMISSION =
            "case.view";

    private static final String CASE_UPDATE_PERMISSION =
            "case.update";

    private static final String CASE_UPDATE_EVENT_TYPE =
            "CASE_UPDATE";

    private static final String CASE_UPDATE_ENTITY_TYPE =
            "CASE";

    private static final String CASE_UPDATE_ACTION =
            "UPDATE";

    private static final String CASE_UPDATE_SOURCE_COMPONENT =
            "CASE";
    private static final String CASE_REVIEW_EVENT_TYPE =
            "CASE_REVIEW";

    private static final String CASE_REVIEW_ENTITY_TYPE =
            "CASE";

    private static final String CASE_REVIEW_ACTION =
            "REVIEW";

    private static final String CASE_REVIEW_SOURCE_COMPONENT =
            "CASE";

    private static final String INVESTIGATION_SEARCH_EVENT_TYPE =
            "INVESTIGATION_SEARCH";

    private static final String INVESTIGATION_SEARCH_ENTITY_TYPE =
            "CASE";

    private static final String INVESTIGATION_SEARCH_ACTION =
            "SEARCH";

    private static final String INVESTIGATION_SEARCH_SOURCE_COMPONENT =
            "CASE";

    private final CaseRepository caseRepository;
    private final CaseAlertRepository caseAlertRepository;
    private final CaseAssignmentRepository caseAssignmentRepository;
    private final CaseTaskRepository caseTaskRepository;
    private final CaseCommentRepository caseCommentRepository;
    private final CaseEvidenceRepository caseEvidenceRepository;
    private final CaseStatusHistoryRepository caseStatusHistoryRepository;
    private final CaseResolutionRepository caseResolutionRepository;
    private final CaseEscalationRepository caseEscalationRepository;
    private final CaseSlaRepository caseSlaRepository;
    private final CaseNotificationRepository caseNotificationRepository;
    private final CaseHistoryRepository caseHistoryRepository;
    private final AlertRepository alertRepository;
    private final AuditEventServiceInterface auditEventService;
    private final AuditEntityChangeServiceInterface auditEntityChangeService;

    private final CaseMapper caseMapper;
    private final CaseAssignmentMapper caseAssignmentMapper;
    private final CaseTaskMapper caseTaskMapper;
    private final CaseCommentMapper caseCommentMapper;
    private final CaseEvidenceMapper caseEvidenceMapper;
    private final CaseStatusHistoryMapper caseStatusHistoryMapper;
    private final CaseResolutionMapper caseResolutionMapper;
    private final CaseEscalationMapper caseEscalationMapper;
    private final CaseSlaMapper caseSlaMapper;
    private final CaseNotificationMapper caseNotificationMapper;
    private final CaseHistoryMapper caseHistoryMapper;

    public CaseService(
            CaseRepository caseRepository,
            CaseAlertRepository caseAlertRepository,
            CaseAssignmentRepository caseAssignmentRepository,
            CaseTaskRepository caseTaskRepository,
            CaseCommentRepository caseCommentRepository,
            CaseEvidenceRepository caseEvidenceRepository,
            CaseStatusHistoryRepository caseStatusHistoryRepository,
            CaseResolutionRepository caseResolutionRepository,
            CaseEscalationRepository caseEscalationRepository,
            CaseSlaRepository caseSlaRepository,
            CaseNotificationRepository caseNotificationRepository,
            CaseHistoryRepository caseHistoryRepository,
            AlertRepository alertRepository,
            CaseMapper caseMapper,
            CaseAssignmentMapper caseAssignmentMapper,
            CaseTaskMapper caseTaskMapper,
            CaseCommentMapper caseCommentMapper,
            CaseEvidenceMapper caseEvidenceMapper,
            CaseStatusHistoryMapper caseStatusHistoryMapper,
            CaseResolutionMapper caseResolutionMapper,
            CaseEscalationMapper caseEscalationMapper,
            CaseSlaMapper caseSlaMapper,
            CaseNotificationMapper caseNotificationMapper,
            CaseHistoryMapper caseHistoryMapper,
            AuditEventServiceInterface auditEventService,
            AuditEntityChangeServiceInterface auditEntityChangeService) {

        this.caseRepository = caseRepository;
        this.caseAlertRepository = caseAlertRepository;
        this.caseAssignmentRepository = caseAssignmentRepository;
        this.caseTaskRepository = caseTaskRepository;
        this.caseCommentRepository = caseCommentRepository;
        this.caseEvidenceRepository = caseEvidenceRepository;
        this.caseStatusHistoryRepository = caseStatusHistoryRepository;
        this.caseResolutionRepository = caseResolutionRepository;
        this.caseEscalationRepository = caseEscalationRepository;
        this.caseSlaRepository = caseSlaRepository;
        this.caseNotificationRepository = caseNotificationRepository;
        this.caseHistoryRepository = caseHistoryRepository;
        this.alertRepository = alertRepository;

        this.caseMapper = caseMapper;
        this.caseAssignmentMapper = caseAssignmentMapper;
        this.caseTaskMapper = caseTaskMapper;
        this.caseCommentMapper = caseCommentMapper;
        this.caseEvidenceMapper = caseEvidenceMapper;
        this.caseStatusHistoryMapper = caseStatusHistoryMapper;
        this.caseResolutionMapper = caseResolutionMapper;
        this.caseEscalationMapper = caseEscalationMapper;
        this.caseSlaMapper = caseSlaMapper;
        this.caseNotificationMapper = caseNotificationMapper;
        this.caseHistoryMapper = caseHistoryMapper;

        this.auditEventService =
                auditEventService;

        this.auditEntityChangeService =
                auditEntityChangeService;
    }

    @Override
    @Transactional
    public CaseResponse createCase(
            CaseRequest request) {

        validateUniqueCaseNumber(
                request.getCaseNumber()
        );

        Case caseEntity =
                caseMapper.toEntity(
                        request
                );

        applyCaseDefaults(
                caseEntity
        );

        return caseMapper.toResponse(
                caseRepository.save(
                        caseEntity
                )
        );
    }

    @Override
    @Transactional
    public CaseResponse createCaseFromAlert(
            CaseFromAlertRequest request) {

        validateUniqueCaseNumber(
                request.getCaseNumber()
        );

        Alert alert =
                alertRepository
                        .findByAlertId(
                                request.getAlertId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Alert not found: "
                                                + request.getAlertId()
                                )
                        );

        if (caseAlertRepository.existsBySourceAlertId(
                alert.getAlertId())) {

            throw new DuplicateRecordException(
                    "Alert is already linked to a case: "
                            + alert.getAlertId()
            );
        }

        if ("CLOSED".equals(
                alert.getStatus())) {

            throw new ValidationException(
                    "Alert is not available for case creation"
            );
        }
        Case caseEntity =
                new Case();

        caseEntity.setCaseNumber(
                request.getCaseNumber()
        );

        caseEntity.setOrganizationId(
                request.getOrganizationId()
        );

        caseEntity.setTransactionId(
                alert.getTransactionId()
        );

        caseEntity.setCustomerId(
                alert.getCustomerId()
        );

        caseEntity.setCaseType(
                request.getCaseType()
        );

        if (request.getCategory() != null) {
            caseEntity.setCategory(
                    request.getCategory()
            );
        } else {
            caseEntity.setCategory(
                    alert.getCategory()
            );
        }

        if (request.getSeverity() != null) {
            caseEntity.setSeverity(
                    request.getSeverity()
            );
        } else if (alert.getSeverity() != null) {
            caseEntity.setSeverity(
                    alert.getSeverity()
            );
        }

        caseEntity.setPriority(
                request.getPriority()
        );

        caseEntity.setAssignedTeam(
                request.getAssignedTeam()
        );

        caseEntity.setAssignedUser(
                request.getAssignedUser()
        );

        caseEntity.setDueDate(
                request.getDueDate()
        );

        caseEntity.setTenantId(
                request.getTenantId()
        );

        applyCaseDefaults(
                caseEntity
        );

        Case savedCase =
                caseRepository.save(
                        caseEntity
                );

        CaseAlert caseAlert =
                new CaseAlert();

        caseAlert.setCaseId(
                savedCase.getCaseId()
        );

        caseAlert.setTransactionId(
                alert.getTransactionId()
        );

        caseAlert.setAlertType(
                alert.getAlertType()
        );

        caseAlert.setAlertSource(
                ALERT_SOURCE
        );

        caseAlert.setRiskScore(
                alert.getRiskScore()
        );

        caseAlert.setSeverity(
                alert.getSeverity() != null
                        ? alert.getSeverity()
                        : savedCase.getSeverity()
        );

        caseAlert.setGeneratedAt(
                alert.getGeneratedAt()
        );

        caseAlert.setSourceAlertId(
                alert.getAlertId()
        );

        caseAlertRepository.save(
                caseAlert
        );

        return caseMapper.toResponse(
                savedCase
        );
    }

    @Override
    @Transactional
    public CaseAssignmentResponse assignCase(
            UUID caseId,
            CaseAssignmentRequest request) {

        Case caseEntity =
                getExistingCase(
                        caseId
                );
        if ("CLOSED".equals(
                caseEntity.getCurrentStatus())
                || caseEntity.getClosedAt() != null) {

            throw new ValidationException(
                    "Case is not available for assignment"
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        CaseAssignment assignment =
                caseAssignmentMapper.toEntity(
                        request
                );

        assignment.setCaseId(
                caseId
        );

        assignment.setAssignedAt(
                now
        );

        CaseAssignment savedAssignment =
                caseAssignmentRepository.save(
                        assignment
                );

        caseEntity.setAssignedUser(
                request.getAssignedTo()
        );

        caseEntity.setAssignedTeam(
                request.getAssignedTeam()
        );

        caseEntity.setUpdatedAt(
                now
        );

        caseRepository.save(
                caseEntity
        );

        return caseAssignmentMapper.toResponse(
                savedAssignment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseAssignmentResponse> getCaseAssignments(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseAssignmentRepository
                .findByCaseIdOrderByAssignedAtDesc(
                        caseId
                )
                .stream()
                .map(caseAssignmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseTaskResponse createCaseTask(
            UUID caseId,
            CaseTaskRequest request) {

        getExistingCase(
                caseId
        );

        CaseTask task =
                caseTaskMapper.toEntity(
                        request
                );

        task.setCaseId(
                caseId
        );

        task.setCreatedAt(
                LocalDateTime.now()
        );

        return caseTaskMapper.toResponse(
                caseTaskRepository.save(
                        task
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseTaskResponse getCaseTaskById(
            UUID caseId,
            UUID taskId) {

        getExistingCase(
                caseId
        );

        CaseTask task =
                caseTaskRepository
                        .findByTaskId(
                                taskId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case task not found: "
                                                + taskId
                                )
                        );

        if (!caseId.equals(
                task.getCaseId())) {

            throw new ResourceNotFoundException(
                    "Case task not found for case: "
                            + caseId
            );
        }

        return caseTaskMapper.toResponse(
                task
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTaskResponse> getCaseTasks(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseTaskRepository
                .findByCaseIdOrderByCreatedAtDesc(
                        caseId
                )
                .stream()
                .map(caseTaskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseCommentResponse createCaseComment(
            UUID caseId,
            CaseCommentRequest request) {

        getExistingCase(
                caseId
        );

        CaseComment comment =
                caseCommentMapper.toEntity(
                        request
                );

        comment.setCaseId(
                caseId
        );

        comment.setCreatedAt(
                LocalDateTime.now()
        );

        return caseCommentMapper.toResponse(
                caseCommentRepository.save(
                        comment
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseCommentResponse getCaseCommentById(
            UUID caseId,
            UUID commentId) {

        getExistingCase(
                caseId
        );

        CaseComment comment =
                caseCommentRepository
                        .findByCommentId(
                                commentId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case comment not found: "
                                                + commentId
                                )
                        );

        if (!caseId.equals(
                comment.getCaseId())) {

            throw new ResourceNotFoundException(
                    "Case comment not found for case: "
                            + caseId
            );
        }

        return caseCommentMapper.toResponse(
                comment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseCommentResponse> getCaseComments(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseCommentRepository
                .findByCaseIdOrderByCreatedAtDesc(
                        caseId
                )
                .stream()
                .map(caseCommentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseEvidenceResponse createCaseEvidence(
            UUID caseId,
            CaseEvidenceRequest request) {

        getExistingCase(
                caseId
        );

        CaseEvidence evidence =
                caseEvidenceMapper.toEntity(
                        request
                );

        evidence.setCaseId(
                caseId
        );

        LocalDateTime now =
                LocalDateTime.now();

        evidence.setUploadedAt(
                now
        );

        evidence.setCreatedAt(
                now
        );

        evidence.setCreatedBy(
                evidence.getUploadedBy()
        );

        evidence.setUpdatedAt(
                now
        );

        return caseEvidenceMapper.toResponse(
                caseEvidenceRepository.save(
                        evidence
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseEvidenceResponse getCaseEvidenceById(
            UUID caseId,
            UUID evidenceId) {

        getExistingCase(
                caseId
        );

        CaseEvidence evidence =
                caseEvidenceRepository
                        .findByEvidenceIdAndDeletedAtIsNull(
                                evidenceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case evidence not found: "
                                                + evidenceId
                                )
                        );

        if (!caseId.equals(
                evidence.getCaseId())) {

            throw new ResourceNotFoundException(
                    "Case evidence not found for case: "
                            + caseId
            );
        }

        return caseEvidenceMapper.toResponse(
                evidence
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseEvidenceResponse> getCaseEvidence(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseEvidenceRepository
                .findByCaseIdAndDeletedAtIsNullOrderByUploadedAtDesc(
                        caseId
                )
                .stream()
                .map(caseEvidenceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseEvidenceResponse updateCaseEvidence(
            UUID caseId,
            UUID evidenceId,
            CaseEvidenceUpdateRequest request) {

        if (request.getUpdatedBy() == null) {
            throw new RequestValidationException(
                    "Evidence update actor is required"
            );
        }

        Case caseEntity =
                getExistingCase(
                        caseId
                );

        CaseEvidence evidence =
                caseEvidenceRepository
                        .findByEvidenceIdAndDeletedAtIsNull(
                                evidenceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case evidence not found: "
                                                + evidenceId
                                )
                        );

        if (!caseId.equals(
                evidence.getCaseId())) {

            throw new ResourceNotFoundException(
                    "Case evidence not found for case: "
                            + caseId
            );
        }

        Map<String, Object> previousValue =
                new LinkedHashMap<>();

        Map<String, Object> currentValue =
                new LinkedHashMap<>();

        previousValue.put(
                "evidenceId",
                evidenceId.toString()
        );

        currentValue.put(
                "evidenceId",
                evidenceId.toString()
        );

        boolean hasUpdate =
                false;

        if (request.getEvidenceType() != null) {

            if (request.getEvidenceType().isBlank()) {
                throw new RequestValidationException(
                        "Evidence type cannot be blank"
                );
            }

            previousValue.put(
                    "evidenceType",
                    evidence.getEvidenceType()
            );

            evidence.setEvidenceType(
                    request.getEvidenceType()
            );

            currentValue.put(
                    "evidenceType",
                    evidence.getEvidenceType()
            );

            hasUpdate = true;
        }

        if (request.getEvidenceCategory() != null) {

            previousValue.put(
                    "evidenceCategory",
                    evidence.getEvidenceCategory()
            );

            evidence.setEvidenceCategory(
                    request.getEvidenceCategory()
            );

            currentValue.put(
                    "evidenceCategory",
                    evidence.getEvidenceCategory()
            );

            hasUpdate = true;
        }

        if (request.getEvidenceName() != null) {

            previousValue.put(
                    "evidenceName",
                    evidence.getEvidenceName()
            );

            evidence.setEvidenceName(
                    request.getEvidenceName()
            );

            currentValue.put(
                    "evidenceName",
                    evidence.getEvidenceName()
            );

            hasUpdate = true;
        }

        if (request.getEvidenceDescription() != null) {

            previousValue.put(
                    "evidenceDescription",
                    evidence.getEvidenceDescription()
            );

            evidence.setEvidenceDescription(
                    request.getEvidenceDescription()
            );

            currentValue.put(
                    "evidenceDescription",
                    evidence.getEvidenceDescription()
            );

            hasUpdate = true;
        }

        if (request.getValidationStatus() != null) {

            previousValue.put(
                    "validationStatus",
                    evidence.getValidationStatus()
            );

            evidence.setValidationStatus(
                    request.getValidationStatus()
            );

            currentValue.put(
                    "validationStatus",
                    evidence.getValidationStatus()
            );

            hasUpdate = true;
        }

        if (request.getConfidentialityLevel() != null) {

            previousValue.put(
                    "confidentialityLevel",
                    evidence.getConfidentialityLevel()
            );

            evidence.setConfidentialityLevel(
                    request.getConfidentialityLevel()
            );

            currentValue.put(
                    "confidentialityLevel",
                    evidence.getConfidentialityLevel()
            );

            hasUpdate = true;
        }

        if (!hasUpdate) {
            throw new RequestValidationException(
                    "At least one evidence field is required for update"
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        evidence.setUpdatedAt(
                now
        );

        evidence.setUpdatedBy(
                request.getUpdatedBy()
        );

        CaseEvidence savedEvidence =
                caseEvidenceRepository.save(
                        evidence
                );

        CaseHistory history =
                new CaseHistory();

        history.setCaseId(
                caseId
        );

        history.setEventType(
                "EVIDENCE_UPDATED"
        );

        history.setEventDescription(
                "Case evidence updated"
        );

        history.setPreviousValue(
                previousValue.toString()
        );

        history.setNewValue(
                currentValue.toString()
        );

        history.setChangedBy(
                request.getUpdatedBy()
        );

        history.setChangedAt(
                now
        );

        caseHistoryRepository.save(
                history
        );

        AuditEventRequest auditEventRequest =
                new AuditEventRequest();

        auditEventRequest.setOrganizationId(
                caseEntity.getOrganizationId()
        );

        auditEventRequest.setTenantId(
                caseEntity.getTenantId()
        );

        auditEventRequest.setUserId(
                request.getUpdatedBy()
        );

        auditEventRequest.setEventType(
                "EVIDENCE_UPDATED"
        );

        auditEventRequest.setEntityType(
                "CASE"
        );

        auditEventRequest.setEntityId(
                caseId
        );

        auditEventRequest.setAction(
                "UPDATE"
        );

        auditEventRequest.setSourceComponent(
                "CASE"
        );

        auditEventRequest.setEventResult(
                "SUCCESS"
        );

        auditEventRequest.setEventDetails(
                Map.of(
                        "caseId",
                        caseId.toString(),
                        "evidenceId",
                        evidenceId.toString()
                )
        );

        AuditEventResponse auditEvent =
                auditEventService.createAuditEvent(
                        auditEventRequest
                );

        AuditEntityChangeRequest entityChangeRequest =
                new AuditEntityChangeRequest();

        entityChangeRequest.setAuditEventId(
                auditEvent.getAuditEventId()
        );

        entityChangeRequest.setEntityType(
                "CASE"
        );

        entityChangeRequest.setEntityId(
                caseId
        );

        entityChangeRequest.setOperation(
                "UPDATE"
        );

        entityChangeRequest.setPreviousValue(
                previousValue
        );

        entityChangeRequest.setCurrentValue(
                currentValue
        );

        auditEntityChangeService.createAuditEntityChange(
                entityChangeRequest
        );

        return caseEvidenceMapper.toResponse(
                savedEvidence
        );
    }

    @Override
    @Transactional
    public void deleteCaseEvidence(
            UUID caseId,
            UUID evidenceId,
            UUID deletedBy) {

        if (deletedBy == null) {
            throw new RequestValidationException(
                    "Evidence deletion actor is required"
            );
        }

        getExistingCase(
                caseId
        );

        CaseEvidence evidence =
                caseEvidenceRepository
                        .findByEvidenceIdAndDeletedAtIsNull(
                                evidenceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case evidence not found: "
                                                + evidenceId
                                )
                        );

        if (!caseId.equals(
                evidence.getCaseId())) {

            throw new ResourceNotFoundException(
                    "Case evidence not found for case: "
                            + caseId
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        evidence.setDeletedAt(
                now
        );

        evidence.setDeletedBy(
                deletedBy
        );

        evidence.setUpdatedAt(
                now
        );

        evidence.setUpdatedBy(
                deletedBy
        );

        caseEvidenceRepository.save(
                evidence
        );
    }

    @Override
    @Transactional
    public CaseResponse updateCaseStatus(
            UUID caseId,
            CaseStatusUpdateRequest request) {

        Case caseEntity =
                getExistingCase(
                        caseId
                );

        if ("CLOSED".equals(
                request.getCurrentStatus())) {

            throw new ValidationException(
                    "Case closure requires a case resolution"
            );
        }

        String previousStatus =
                caseEntity.getCurrentStatus();

        LocalDateTime now =
                LocalDateTime.now();

        caseEntity.setCurrentStatus(
                request.getCurrentStatus()
        );

        caseEntity.setUpdatedAt(
                now
        );

        Case savedCase =
                caseRepository.save(
                        caseEntity
                );

        CaseStatusHistory history =
                new CaseStatusHistory();

        history.setCaseId(
                caseId
        );

        history.setPreviousStatus(
                previousStatus
        );

        history.setCurrentStatus(
                request.getCurrentStatus()
        );

        history.setChangeReason(
                request.getChangeReason()
        );

        history.setChangedBy(
                request.getChangedBy()
        );

        history.setChangedAt(
                now
        );

        caseStatusHistoryRepository.save(
                history
        );

        return caseMapper.toResponse(
                savedCase
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseStatusHistoryResponse> getCaseStatusHistory(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseStatusHistoryRepository
                .findByCaseIdOrderByChangedAtDesc(
                        caseId
                )
                .stream()
                .map(caseStatusHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseResolutionResponse createCaseResolution(
            UUID caseId,
            CaseResolutionRequest request) {

        Case caseEntity =
                getExistingCase(
                        caseId
                );

        if ("CLOSED".equals(
                caseEntity.getCurrentStatus())
                || caseEntity.getClosedAt() != null) {

            throw new ValidationException(
                    "Case already closed: "
                            + caseId
            );
        }

        String previousStatus =
                caseEntity.getCurrentStatus();

        LocalDateTime now =
                LocalDateTime.now();

        CaseResolution resolution =
                caseResolutionMapper.toEntity(
                        request
                );

        resolution.setCaseId(
                caseId
        );

        resolution.setResolvedAt(
                now
        );

        CaseResolution savedResolution =
                caseResolutionRepository.save(
                        resolution
                );

        caseEntity.setCurrentStatus(
                "CLOSED"
        );

        caseEntity.setClosedAt(
                now
        );

        caseEntity.setUpdatedAt(
                now
        );

        caseRepository.save(
                caseEntity
        );

        CaseStatusHistory history =
                new CaseStatusHistory();

        history.setCaseId(
                caseId
        );

        history.setPreviousStatus(
                previousStatus
        );

        history.setCurrentStatus(
                "CLOSED"
        );

        history.setChangedBy(
                request.getResolvedBy()
        );

        history.setChangedAt(
                now
        );

        caseStatusHistoryRepository.save(
                history
        );

        return caseResolutionMapper.toResponse(
                savedResolution
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseResolutionResponse getCaseResolutionById(
            UUID caseId,
            UUID resolutionId) {

        getExistingCase(
                caseId
        );

        CaseResolution resolution =
                caseResolutionRepository
                        .findByResolutionId(
                                resolutionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case resolution not found: "
                                                + resolutionId
                                )
                        );

        if (!caseId.equals(
                resolution.getCaseId())) {

            throw new ResourceNotFoundException(
                    "Case resolution not found for case: "
                            + caseId
            );
        }

        return caseResolutionMapper.toResponse(
                resolution
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseResolutionResponse> getCaseResolutions(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseResolutionRepository
                .findByCaseIdOrderByResolvedAtDesc(
                        caseId
                )
                .stream()
                .map(caseResolutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseEscalationResponse createCaseEscalation(
            UUID caseId,
            CaseEscalationRequest request) {

        getExistingCase(
                caseId
        );

        CaseEscalation escalation =
                caseEscalationMapper.toEntity(
                        request
                );

        escalation.setCaseId(
                caseId
        );

        escalation.setEscalatedAt(
                LocalDateTime.now()
        );

        return caseEscalationMapper.toResponse(
                caseEscalationRepository.save(
                        escalation
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseEscalationResponse getCaseEscalationById(
            UUID caseId,
            UUID escalationId) {

        getExistingCase(
                caseId
        );

        CaseEscalation escalation =
                caseEscalationRepository
                        .findByEscalationId(
                                escalationId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case escalation not found: "
                                                + escalationId
                                )
                        );

        if (!caseId.equals(
                escalation.getCaseId())) {

            throw new ResourceNotFoundException(
                    "Case escalation not found for case: "
                            + caseId
            );
        }

        return caseEscalationMapper.toResponse(
                escalation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseEscalationResponse> getCaseEscalations(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseEscalationRepository
                .findByCaseIdOrderByEscalatedAtDesc(
                        caseId
                )
                .stream()
                .map(caseEscalationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseSlaResponse createCaseSla(
            UUID caseId,
            CaseSlaRequest request) {

        getExistingCase(
                caseId
        );

        CaseSla sla =
                caseSlaMapper.toEntity(
                        request
                );

        sla.setCaseId(
                caseId
        );

        sla.setCalculatedAt(
                LocalDateTime.now()
        );

        return caseSlaMapper.toResponse(
                caseSlaRepository.save(
                        sla
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseSlaResponse getCaseSlaById(
            UUID caseId,
            UUID slaId) {

        getExistingCase(
                caseId
        );

        CaseSla sla =
                caseSlaRepository
                        .findBySlaIdAndCaseId(
                                slaId,
                                caseId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case SLA not found: "
                                                + slaId
                                )
                        );

        return caseSlaMapper.toResponse(
                sla
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseSlaResponse> getCaseSlas(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseSlaRepository
                .findByCaseIdOrderByDeadlineAsc(
                        caseId
                )
                .stream()
                .map(caseSlaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseNotificationResponse createCaseNotification(
            UUID caseId,
            CaseNotificationRequest request) {

        getExistingCase(
                caseId
        );

        CaseNotification notification =
                caseNotificationMapper.toEntity(
                        request
                );

        notification.setCaseId(
                caseId
        );

        notification.setCreatedAt(
                LocalDateTime.now()
        );

        return caseNotificationMapper.toResponse(
                caseNotificationRepository.save(
                        notification
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseNotificationResponse getCaseNotificationById(
            UUID caseId,
            UUID caseNotificationId) {

        getExistingCase(
                caseId
        );

        CaseNotification notification =
                caseNotificationRepository
                        .findByCaseNotificationIdAndCaseId(
                                caseNotificationId,
                                caseId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case notification not found: "
                                                + caseNotificationId
                                )
                        );

        return caseNotificationMapper.toResponse(
                notification
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseNotificationResponse> getCaseNotifications(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseNotificationRepository
                .findByCaseIdOrderByCreatedAtDesc(
                        caseId
                )
                .stream()
                .map(caseNotificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseHistoryResponse createCaseHistory(
            UUID caseId,
            CaseHistoryRequest request) {

        getExistingCase(
                caseId
        );

        CaseHistory history =
                caseHistoryMapper.toEntity(
                        request
                );

        history.setCaseId(
                caseId
        );

        history.setChangedAt(
                LocalDateTime.now()
        );

        return caseHistoryMapper.toResponse(
                caseHistoryRepository.save(
                        history
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CaseHistoryResponse getCaseHistoryById(
            UUID caseId,
            UUID historyId) {

        getExistingCase(
                caseId
        );

        CaseHistory history =
                caseHistoryRepository
                        .findByHistoryIdAndCaseId(
                                historyId,
                                caseId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case history not found: "
                                                + historyId
                                )
                        );

        return caseHistoryMapper.toResponse(
                history
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseHistoryResponse> getCaseHistory(
            UUID caseId) {

        getExistingCase(
                caseId
        );

        return caseHistoryRepository
                .findByCaseIdOrderByChangedAtDesc(
                        caseId
                )
                .stream()
                .map(caseHistoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(
            noRollbackFor = {
                    AccessDeniedException.class,
                    ResourceNotFoundException.class,
                    RequestValidationException.class,
                    ValidationException.class
            }
    )
    public CaseResponse updateCase(
            UUID caseId,
            CaseUpdateRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireCaseUpdatePermission(
                securityContext,
                caseId
        );

        Case caseEntity;

        try {
            caseEntity =
                    getExistingCase(
                            caseId
                    );

        } catch (ResourceNotFoundException exception) {

            recordCaseUpdateAudit(
                    securityContext,
                    caseId,
                    "REJECTED",
                    "CASE_NOT_FOUND",
                    null,
                    null
            );

            throw exception;
        }

        if ("CLOSED".equals(
                caseEntity.getCurrentStatus()
        )) {

            recordCaseUpdateAudit(
                    securityContext,
                    caseId,
                    "REJECTED",
                    "CASE_CLOSED",
                    null,
                    null
            );

            throw new ValidationException(
                    "Closed case cannot be updated"
            );
        }

        Map<String, Object> previousValue =
                new LinkedHashMap<>();

        Map<String, Object> currentValue =
                new LinkedHashMap<>();

        if (request.getPriority() != null) {

            if (request.getPriority().isBlank()) {

                recordCaseUpdateAudit(
                        securityContext,
                        caseId,
                        "REJECTED",
                        "INVALID_UPDATE_REQUEST",
                        null,
                        null
                );

                throw new RequestValidationException(
                        "Priority cannot be blank"
                );
            }

            previousValue.put(
                    "priority",
                    caseEntity.getPriority()
            );

            caseEntity.setPriority(
                    request.getPriority()
            );

            currentValue.put(
                    "priority",
                    caseEntity.getPriority()
            );
        }

        if (request.getDueDate() != null) {

            previousValue.put(
                    "dueDate",
                    caseEntity.getDueDate() == null
                            ? null
                            : caseEntity.getDueDate()
                                    .toString()
            );

            caseEntity.setDueDate(
                    request.getDueDate()
            );

            currentValue.put(
                    "dueDate",
                    caseEntity.getDueDate()
                            .toString()
            );
        }

        if (currentValue.isEmpty()) {

            recordCaseUpdateAudit(
                    securityContext,
                    caseId,
                    "REJECTED",
                    "INVALID_UPDATE_REQUEST",
                    null,
                    null
            );

            throw new RequestValidationException(
                    "At least one case field is required for update"
            );
        }

        caseEntity.setUpdatedAt(
                LocalDateTime.now()
        );

        try {
            Case savedCase =
                    caseRepository.saveAndFlush(
                            caseEntity
                    );

            AuditEventResponse auditEvent =
                    recordCaseUpdateAudit(
                            securityContext,
                            caseId,
                            "SUCCESS",
                            null,
                            currentValue,
                            null
                    );

            AuditEntityChangeRequest
                    entityChangeRequest =
                    new AuditEntityChangeRequest();

            entityChangeRequest.setAuditEventId(
                    auditEvent.getAuditEventId()
            );

            entityChangeRequest.setEntityType(
                    CASE_UPDATE_ENTITY_TYPE
            );

            entityChangeRequest.setEntityId(
                    caseId
            );

            entityChangeRequest.setOperation(
                    CASE_UPDATE_ACTION
            );

            entityChangeRequest.setPreviousValue(
                    new LinkedHashMap<>(
                            previousValue
                    )
            );

            entityChangeRequest.setCurrentValue(
                    new LinkedHashMap<>(
                            currentValue
                    )
            );

            auditEntityChangeService
                    .createAuditEntityChange(
                            entityChangeRequest
                    );

            return caseMapper.toResponse(
                    savedCase
            );

        } catch (RuntimeException exception) {

            recordCaseUpdateFailureAudit(
                    securityContext,
                    caseId,
                    currentValue,
                    exception
            );

            throw exception;
        }
    }

    @Override
    public CaseResponse getCaseById(
            UUID caseId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireCaseReviewPermission(
                securityContext,
                caseId
        );

        try {
            CaseResponse response =
                    caseMapper.toResponse(
                            getExistingCase(
                                    caseId
                            )
                    );

            recordCaseReviewAudit(
                    securityContext,
                    caseId,
                    "SUCCESS",
                    null,
                    null
            );

            return response;

        } catch (ResourceNotFoundException exception) {

            recordCaseReviewAudit(
                    securityContext,
                    caseId,
                    "REJECTED",
                    "CASE_NOT_FOUND",
                    null
            );

            throw exception;

        } catch (RuntimeException exception) {

            recordCaseReviewAudit(
                    securityContext,
                    caseId,
                    "FAILURE",
                    "CASE_REVIEW_FAILED",
                    exception
            );

            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CaseResponse getCaseByNumber(
            String caseNumber) {

        Case caseEntity =
                caseRepository
                        .findByCaseNumber(
                                caseNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Case not found: "
                                                + caseNumber
                                )
                        );

        return caseMapper.toResponse(
                caseEntity
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseResponse> getCasesByCustomerId(
            UUID customerId) {

        return caseRepository
                .findByCustomerIdOrderByCreatedAtDesc(
                        customerId
                )
                .stream()
                .map(caseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseResponse> getCasesByTransactionId(
            UUID transactionId) {

        return caseRepository
                .findByTransactionIdOrderByCreatedAtDesc(
                        transactionId
                )
                .stream()
                .map(caseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseResponse> getCasesByStatus(
            String currentStatus) {

        return caseRepository
                .findByCurrentStatusOrderByCreatedAtDesc(
                        currentStatus
                )
                .stream()
                .map(caseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseResponse> getCasesByPriority(
            String priority) {

        return caseRepository
                .findByPriorityOrderByCreatedAtDesc(
                        priority
                )
                .stream()
                .map(caseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseResponse> getCasesByAssignedUser(
            UUID assignedUser) {

        return caseRepository
                .findByAssignedUserOrderByCreatedAtDesc(
                        assignedUser
                )
                .stream()
                .map(caseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseResponse> getCasesByAssignedTeam(
            String assignedTeam) {

        return caseRepository
                .findByAssignedTeamOrderByCreatedAtDesc(
                        assignedTeam
                )
                .stream()
                .map(caseMapper::toResponse)
                .toList();
    }

    @Override
    public PageResponse<CaseResponse> searchCases(
            String status,
            String priority,
            UUID assignedUser,
            String assignedTeam,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Map<String, Object> criteria =
                investigationSearchCriteria(
                        status,
                        priority,
                        assignedUser,
                        assignedTeam,
                        page,
                        size,
                        sort,
                        direction
                );

        requireCaseViewPermission(
                securityContext,
                criteria
        );

        PageResponse<CaseResponse> response;

        try {
            validateCaseSearchRequest(
                    page,
                    size,
                    sort,
                    direction
            );

            Sort.Direction sortDirection =
                    SORT_DIRECTION_ASC.equalsIgnoreCase(
                            direction
                    )
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;

            PageRequest pageRequest =
                    PageRequest.of(
                            page,
                            size,
                            Sort.by(
                                    sortDirection,
                                    sort
                            )
                    );

            Specification<Case> specification =
                    (root, query, criteriaBuilder) -> {

                        List<Predicate> predicates =
                                new ArrayList<>();

                        if (hasText(status)) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(
                                                    "currentStatus"
                                            ),
                                            status
                                    )
                            );
                        }

                        if (hasText(priority)) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(
                                                    "priority"
                                            ),
                                            priority
                                    )
                            );
                        }

                        if (assignedUser != null) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(
                                                    "assignedUser"
                                            ),
                                            assignedUser
                                    )
                            );
                        }

                        if (hasText(assignedTeam)) {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(
                                                    "assignedTeam"
                                            ),
                                            assignedTeam
                                    )
                            );
                        }

                        return criteriaBuilder.and(
                                predicates.toArray(
                                        new Predicate[0]
                                )
                        );
                    };

            Page<Case> casePage =
                    caseRepository.findAll(
                            specification,
                            pageRequest
                    );

            List<CaseResponse> content =
                    casePage
                            .getContent()
                            .stream()
                            .map(caseMapper::toResponse)
                            .toList();

            response =
                    new PageResponse<>(
                            content,
                            casePage.getNumber(),
                            casePage.getSize(),
                            casePage.getTotalElements(),
                            casePage.getTotalPages(),
                            casePage.hasNext(),
                            casePage.hasPrevious()
                    );
        }
        catch (RequestValidationException exception) {

            recordInvestigationSearchAudit(
                    securityContext,
                    "REJECTED",
                    "INVALID_SEARCH_CRITERIA",
                    null,
                    criteria,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            recordInvestigationSearchAudit(
                    securityContext,
                    "FAILURE",
                    "INVESTIGATION_SEARCH_FAILED",
                    null,
                    criteria,
                    exception
            );

            throw exception;
        }

        recordInvestigationSearchAudit(
                securityContext,
                "SUCCESS",
                null,
                response.getTotalElements(),
                criteria,
                null
        );

        return response;
    }

    private Map<String, Object> investigationSearchCriteria(
            String status,
            String priority,
            UUID assignedUser,
            String assignedTeam,
            int page,
            int size,
            String sort,
            String direction) {

        Map<String, Object> criteria =
                new LinkedHashMap<>();

        criteria.put("status", status);
        criteria.put("priority", priority);
        criteria.put(
                "assignedUser",
                assignedUser == null
                        ? null
                        : assignedUser.toString()
        );
        criteria.put("assignedTeam", assignedTeam);
        criteria.put("page", page);
        criteria.put("size", size);
        criteria.put("sort", sort);
        criteria.put("direction", direction);

        return criteria;
    }

    private void requireCaseUpdatePermission(
            SecurityContext securityContext,
            UUID caseId) {

        if (!securityContext.hasPermission(
                CASE_UPDATE_PERMISSION
        )) {

            recordCaseUpdateAudit(
                    securityContext,
                    caseId,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null,
                    null
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + CASE_UPDATE_PERMISSION
            );
        }
    }

    private AuditEventResponse recordCaseUpdateAudit(
            SecurityContext securityContext,
            UUID caseId,
            String eventResult,
            String reason,
            Map<String, Object> currentValue,
            RuntimeException exception) {

        AuditEventRequest request =
                buildCaseUpdateAuditRequest(
                        securityContext,
                        caseId,
                        eventResult,
                        reason,
                        currentValue,
                        exception
                );

        return auditEventService.createAuditEvent(
                request
        );
    }

    private void recordCaseUpdateFailureAudit(
            SecurityContext securityContext,
            UUID caseId,
            Map<String, Object> currentValue,
            RuntimeException exception) {

        AuditEventRequest request =
                buildCaseUpdateAuditRequest(
                        securityContext,
                        caseId,
                        "FAILURE",
                        "CASE_UPDATE_FAILED",
                        currentValue,
                        exception
                );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }

    private AuditEventRequest buildCaseUpdateAuditRequest(
            SecurityContext securityContext,
            UUID caseId,
            String eventResult,
            String reason,
            Map<String, Object> currentValue,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                securityContext.getTenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                CASE_UPDATE_EVENT_TYPE
        );

        request.setEntityType(
                CASE_UPDATE_ENTITY_TYPE
        );

        request.setEntityId(
                caseId
        );

        request.setAction(
                CASE_UPDATE_ACTION
        );

        request.setSourceComponent(
                CASE_UPDATE_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                CASE_UPDATE_PERMISSION
        );

        if (currentValue != null
                && !currentValue.isEmpty()) {

            details.put(
                    "fields",
                    new ArrayList<>(
                            currentValue.keySet()
                    )
            );
        }

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (exception != null) {
            details.put(
                    "errorType",
                    exception.getClass()
                            .getName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        return request;
    }
    private void requireCaseReviewPermission(
            SecurityContext securityContext,
            UUID caseId) {

        if (!securityContext.hasPermission(
                CASE_VIEW_PERMISSION
        )) {

            recordCaseReviewAudit(
                    securityContext,
                    caseId,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + CASE_VIEW_PERMISSION
            );
        }
    }

    private void recordCaseReviewAudit(
            SecurityContext securityContext,
            UUID caseId,
            String eventResult,
            String reason,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                securityContext.getTenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                CASE_REVIEW_EVENT_TYPE
        );

        request.setEntityType(
                CASE_REVIEW_ENTITY_TYPE
        );

        request.setEntityId(
                caseId
        );

        request.setAction(
                CASE_REVIEW_ACTION
        );

        request.setSourceComponent(
                CASE_REVIEW_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                CASE_VIEW_PERMISSION
        );

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (exception != null) {
            details.put(
                    "errorType",
                    exception.getClass().getName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void requireCaseViewPermission(
            SecurityContext securityContext,
            Map<String, Object> criteria) {

        if (!securityContext.hasPermission(
                CASE_VIEW_PERMISSION
        )) {

            recordInvestigationSearchAudit(
                    securityContext,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null,
                    criteria,
                    null
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + CASE_VIEW_PERMISSION
            );
        }
    }

    private void recordInvestigationSearchAudit(
            SecurityContext securityContext,
            String eventResult,
            String reason,
            Long resultCount,
            Map<String, Object> criteria,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                securityContext.getTenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                INVESTIGATION_SEARCH_EVENT_TYPE
        );

        request.setEntityType(
                INVESTIGATION_SEARCH_ENTITY_TYPE
        );

        request.setEntityId(null);

        request.setAction(
                INVESTIGATION_SEARCH_ACTION
        );

        request.setSourceComponent(
                INVESTIGATION_SEARCH_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                CASE_VIEW_PERMISSION
        );

        details.put(
                "criteria",
                new LinkedHashMap<>(
                        criteria
                )
        );

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (resultCount != null) {
            details.put(
                    "resultCount",
                    resultCount
            );
        }

        if (exception != null) {
            details.put(
                    "errorType",
                    exception.getClass()
                            .getSimpleName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void validateCaseSearchRequest(
            int page,
            int size,
            String sort,
            String direction) {

        if (page < 0) {
            throw new RequestValidationException(
                    "Page must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new RequestValidationException(
                    "Size must be between 1 and "
                            + MAX_PAGE_SIZE
            );
        }

        if (!DEFAULT_CASE_SORT.equals(
                sort
        )) {
            throw new RequestValidationException(
                    "Unsupported case sort field: "
                            + sort
            );
        }

        if (!SORT_DIRECTION_ASC.equalsIgnoreCase(
                direction
        )
                && !SORT_DIRECTION_DESC.equalsIgnoreCase(
                        direction
                )) {

            throw new RequestValidationException(
                    "Unsupported sort direction: "
                            + direction
            );
        }
    }

    private boolean hasText(
            String value) {

        return value != null
                && !value.isBlank();
    }

    private Case getExistingCase(
            UUID caseId) {

        return caseRepository
                .findByCaseId(
                        caseId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Case not found: "
                                        + caseId
                        )
                );
    }

    private void validateUniqueCaseNumber(
            String caseNumber) {

        caseRepository
                .findByCaseNumber(
                        caseNumber
                )
                .ifPresent(existingCase -> {
                    throw new IllegalArgumentException(
                            "Case number already exists: "
                                    + caseNumber
                    );
                });
    }

    private void applyCaseDefaults(
            Case caseEntity) {

        if (caseEntity.getSeverity() == null) {
            caseEntity.setSeverity(
                    DEFAULT_SEVERITY
            );
        }

        if (caseEntity.getPriority() == null) {
            caseEntity.setPriority(
                    DEFAULT_PRIORITY
            );
        }

        caseEntity.setCurrentStatus(
                INITIAL_STATUS
        );

        LocalDateTime now =
                LocalDateTime.now();

        caseEntity.setCreatedAt(
                now
        );

        caseEntity.setUpdatedAt(
                now
        );
    }
}
