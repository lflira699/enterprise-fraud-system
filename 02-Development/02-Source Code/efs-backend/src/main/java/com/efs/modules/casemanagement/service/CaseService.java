package com.efs.modules.casemanagement.service;

import com.efs.modules.alert.entity.Alert;
import com.efs.modules.alert.repository.AlertRepository;
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
import com.efs.shared.pagination.PageResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
            CaseHistoryMapper caseHistoryMapper) {

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
    public void deleteCaseEvidence(
            UUID caseId,
            UUID evidenceId,
            UUID deletedBy) {

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

        getExistingCase(
                caseId
        );

        CaseResolution resolution =
                caseResolutionMapper.toEntity(
                        request
                );

        resolution.setCaseId(
                caseId
        );

        resolution.setResolvedAt(
                LocalDateTime.now()
        );

        return caseResolutionMapper.toResponse(
                caseResolutionRepository.save(
                        resolution
                )
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
    @Transactional(readOnly = true)
    public CaseResponse getCaseById(
            UUID caseId) {

        return caseMapper.toResponse(
                getExistingCase(
                        caseId
                )
        );
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
    @Transactional(readOnly = true)
    public PageResponse<CaseResponse> searchCases(
            String status,
            String priority,
            UUID assignedUser,
            String assignedTeam,
            int page,
            int size,
            String sort,
            String direction) {

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

        return new PageResponse<>(
                content,
                casePage.getNumber(),
                casePage.getSize(),
                casePage.getTotalElements(),
                casePage.getTotalPages(),
                casePage.hasNext(),
                casePage.hasPrevious()
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
