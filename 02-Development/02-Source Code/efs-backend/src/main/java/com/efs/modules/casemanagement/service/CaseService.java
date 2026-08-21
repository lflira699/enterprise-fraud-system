package com.efs.modules.casemanagement.service;

import com.efs.modules.alert.entity.Alert;
import com.efs.modules.alert.repository.AlertRepository;
import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.dto.CaseFromAlertRequest;
import com.efs.modules.casemanagement.dto.CaseRequest;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.modules.casemanagement.entity.Case;
import com.efs.modules.casemanagement.entity.CaseAlert;
import com.efs.modules.casemanagement.entity.CaseAssignment;
import com.efs.modules.casemanagement.mapper.CaseAssignmentMapper;
import com.efs.modules.casemanagement.mapper.CaseMapper;
import com.efs.modules.casemanagement.repository.CaseAlertRepository;
import com.efs.modules.casemanagement.repository.CaseAssignmentRepository;
import com.efs.modules.casemanagement.repository.CaseRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final CaseRepository caseRepository;

    private final CaseAlertRepository caseAlertRepository;

    private final CaseAssignmentRepository caseAssignmentRepository;

    private final AlertRepository alertRepository;

    private final CaseMapper caseMapper;

    private final CaseAssignmentMapper caseAssignmentMapper;

    public CaseService(
            CaseRepository caseRepository,
            CaseAlertRepository caseAlertRepository,
            CaseAssignmentRepository caseAssignmentRepository,
            AlertRepository alertRepository,
            CaseMapper caseMapper,
            CaseAssignmentMapper caseAssignmentMapper) {

        this.caseRepository =
                caseRepository;

        this.caseAlertRepository =
                caseAlertRepository;

        this.caseAssignmentRepository =
                caseAssignmentRepository;

        this.alertRepository =
                alertRepository;

        this.caseMapper =
                caseMapper;

        this.caseAssignmentMapper =
                caseAssignmentMapper;
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

        Case savedCase =
                caseRepository.save(
                        caseEntity
                );

        return caseMapper.toResponse(
                savedCase
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