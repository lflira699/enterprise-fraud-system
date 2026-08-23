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
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CaseServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "11111111-aaaa-1111-aaaa-111111111111"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "22222222-bbbb-2222-bbbb-222222222222"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "33333333-cccc-3333-cccc-333333333333"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "44444444-dddd-4444-dddd-444444444444"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "55555555-eeee-5555-eeee-555555555555"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "66666666-ffff-6666-ffff-666666666666"
            );

    private static final UUID ALERT_ID =
            UUID.fromString(
                    "77777777-aaaa-7777-aaaa-777777777777"
            );

    private static final UUID ASSIGNED_FROM =
            UUID.fromString(
                    "88888888-bbbb-8888-bbbb-888888888888"
            );

    private static final UUID ASSIGNED_TO =
            UUID.fromString(
                    "99999999-cccc-9999-cccc-999999999999"
            );

    @Autowired
    private CaseServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                ORGANIZATION_ID,
                "EFS-CASE-TEST-ORG",
                "EFS Case Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ASSIGNED_FROM,
                ORGANIZATION_ID,
                "efs.case.assignment.from",
                "EFS Case Assignment From",
                "efs.case.assignment.from@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ASSIGNED_TO,
                ORGANIZATION_ID,
                "efs.case.assignment.to",
                "EFS Case Assignment To",
                "efs.case.assignment.to@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );

        jdbcTemplate.update(
                """
                INSERT INTO customer.customer (
                    customer_id,
                    customer_number,
                    customer_type,
                    risk_level,
                    risk_score,
                    customer_status,
                    record_status,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                CUSTOMER_ID,
                "EFS-CASE-TEST-CUSTOMER",
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction (
                    transaction_id,
                    transaction_reference,
                    customer_id,
                    organization_id,
                    transaction_type,
                    amount,
                    currency_code,
                    transaction_status,
                    final_decision,
                    fraud_score,
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                TRANSACTION_ID,
                "EFS-CASE-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("1000.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CREATED_BY,
                1
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.risk_assessment (
                    risk_assessment_id,
                    transaction_id,
                    assessment_type,
                    assessment_stage,
                    overall_risk_score,
                    risk_level,
                    assessment_result,
                    confidence_score,
                    assessment_timestamp,
                    created_at,
                    updated_at,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                RISK_ASSESSMENT_ID,
                TRANSACTION_ID,
                "TRANSACTION",
                "DECISION",
                new BigDecimal("88.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("90.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction_decision (
                    decision_id,
                    transaction_id,
                    risk_assessment_id,
                    decision_type,
                    decision_source,
                    confidence_score,
                    decision_reason,
                    decision_timestamp,
                    is_final
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                DECISION_ID,
                TRANSACTION_ID,
                RISK_ASSESSMENT_ID,
                "ESCALATE",
                "DECISION_ENGINE",
                new BigDecimal("90.00"),
                "Escalate to investigation",
                LocalDateTime.now(),
                false
        );

        jdbcTemplate.update(
                """
                INSERT INTO alert.alert (
                    alert_id,
                    customer_id,
                    transaction_id,
                    decision_id,
                    risk_assessment_id,
                    alert_type,
                    category,
                    severity,
                    priority,
                    status,
                    risk_score,
                    generated_at,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ALERT_ID,
                CUSTOMER_ID,
                TRANSACTION_ID,
                DECISION_ID,
                RISK_ASSESSMENT_ID,
                "FRAUD",
                "TRANSACTION",
                "HIGH",
                "HIGH",
                "NEW",
                new BigDecimal("88.00"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateAndRetrieveCase() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-TEST-001"
                        )
                );

        assertNotNull(created.getCaseId());
        assertEquals("CASE-TEST-001", created.getCaseNumber());
        assertEquals(ORGANIZATION_ID, created.getOrganizationId());
        assertEquals(CUSTOMER_ID, created.getCustomerId());
        assertEquals(TRANSACTION_ID, created.getTransactionId());
        assertEquals("FRAUD_INVESTIGATION", created.getCaseType());
        assertEquals("TRANSACTION", created.getCategory());
        assertEquals("MEDIUM", created.getSeverity());
        assertEquals("NORMAL", created.getPriority());
        assertEquals("OPEN", created.getCurrentStatus());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    void shouldReturnCasesByCustomerAndTransaction() {

        service.createCase(
                buildRequest(
                        "CASE-TEST-002"
                )
        );

        assertEquals(
                1,
                service.getCasesByCustomerId(
                        CUSTOMER_ID
                ).size()
        );

        assertEquals(
                1,
                service.getCasesByTransactionId(
                        TRANSACTION_ID
                ).size()
        );
    }

    @Test
    void shouldFilterCases() {

        service.createCase(
                buildRequest(
                        "CASE-TEST-003"
                )
        );

        assertEquals(
                1,
                service.getCasesByStatus(
                        "OPEN"
                ).size()
        );

        assertEquals(
                1,
                service.getCasesByPriority(
                        "NORMAL"
                ).size()
        );
    }

    @Test
    void shouldRejectDuplicateCaseNumber() {

        service.createCase(
                buildRequest(
                        "CASE-TEST-004"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createCase(
                        buildRequest(
                                "CASE-TEST-004"
                        )
                )
        );
    }

    @Test
    void shouldCreateCaseFromAlertAndLinkSourceAlert() {

        CaseFromAlertRequest request =
                new CaseFromAlertRequest();

        request.setAlertId(ALERT_ID);
        request.setCaseNumber("CASE-ALERT-001");
        request.setOrganizationId(ORGANIZATION_ID);
        request.setCaseType("FRAUD_INVESTIGATION");

        CaseResponse created =
                service.createCaseFromAlert(
                        request
                );

        assertNotNull(created.getCaseId());
        assertEquals(CUSTOMER_ID, created.getCustomerId());
        assertEquals(TRANSACTION_ID, created.getTransactionId());
        assertEquals("TRANSACTION", created.getCategory());
        assertEquals("HIGH", created.getSeverity());
        assertEquals("NORMAL", created.getPriority());
        assertEquals("OPEN", created.getCurrentStatus());

        Integer linkCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_alert
                        WHERE case_id = ?
                          AND source_alert_id = ?
                        """,
                        Integer.class,
                        created.getCaseId(),
                        ALERT_ID
                );

        assertEquals(
                1,
                linkCount
        );
    }

    @Test
    void shouldRejectDuplicateCaseCreationFromSameAlert() {

        CaseFromAlertRequest firstRequest =
                new CaseFromAlertRequest();

        firstRequest.setAlertId(ALERT_ID);
        firstRequest.setCaseNumber("CASE-ALERT-002");
        firstRequest.setOrganizationId(ORGANIZATION_ID);
        firstRequest.setCaseType("FRAUD_INVESTIGATION");

        service.createCaseFromAlert(
                firstRequest
        );

        CaseFromAlertRequest secondRequest =
                new CaseFromAlertRequest();

        secondRequest.setAlertId(ALERT_ID);
        secondRequest.setCaseNumber("CASE-ALERT-003");
        secondRequest.setOrganizationId(ORGANIZATION_ID);
        secondRequest.setCaseType("FRAUD_INVESTIGATION");

        assertThrows(
                DuplicateRecordException.class,
                () -> service.createCaseFromAlert(
                        secondRequest
                )
        );
    }

    @Test
    void shouldAssignCaseAndUpdateCurrentAssignment() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-ASSIGNMENT-001"
                        )
                );

        assertNull(created.getAssignedUser());

        CaseAssignmentRequest request =
                new CaseAssignmentRequest();

        request.setAssignedFrom(ASSIGNED_FROM);
        request.setAssignedTo(ASSIGNED_TO);
        request.setAssignedTeam("FRAUD_INVESTIGATION");
        request.setAssignmentReason("Assigned for investigation");

        CaseAssignmentResponse assignment =
                service.assignCase(
                        created.getCaseId(),
                        request
                );

        assertNotNull(assignment.getAssignmentId());
        assertEquals(created.getCaseId(), assignment.getCaseId());
        assertEquals(ASSIGNED_FROM, assignment.getAssignedFrom());
        assertEquals(ASSIGNED_TO, assignment.getAssignedTo());
        assertEquals("FRAUD_INVESTIGATION", assignment.getAssignedTeam());
        assertEquals(
                "Assigned for investigation",
                assignment.getAssignmentReason()
        );
        assertNotNull(assignment.getAssignedAt());

        CaseResponse updated =
                service.getCaseById(
                        created.getCaseId()
                );

        assertEquals(ASSIGNED_TO, updated.getAssignedUser());
        assertEquals("FRAUD_INVESTIGATION", updated.getAssignedTeam());
    }

    @Test
    void shouldReturnCaseAssignmentHistory() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-ASSIGNMENT-002"
                        )
                );

        CaseAssignmentRequest request =
                new CaseAssignmentRequest();

        request.setAssignedFrom(ASSIGNED_FROM);
        request.setAssignedTo(ASSIGNED_TO);
        request.setAssignedTeam("FRAUD_INVESTIGATION");
        request.setAssignmentReason("Initial assignment");

        service.assignCase(
                created.getCaseId(),
                request
        );

        List<CaseAssignmentResponse> assignments =
                service.getCaseAssignments(
                        created.getCaseId()
                );

        assertEquals(1, assignments.size());
        assertEquals(ASSIGNED_TO, assignments.get(0).getAssignedTo());
        assertEquals(
                "FRAUD_INVESTIGATION",
                assignments.get(0).getAssignedTeam()
        );
        assertEquals(
                "Initial assignment",
                assignments.get(0).getAssignmentReason()
        );
    }

    @Test
    void shouldCreateAndRetrieveCaseTask() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-TASK-001"
                        )
                );

        CaseTaskResponse task =
                service.createCaseTask(
                        created.getCaseId(),
                        buildTaskRequest(
                                "Review transaction evidence"
                        )
                );

        assertNotNull(task.getTaskId());
        assertEquals(created.getCaseId(), task.getCaseId());
        assertEquals("Review transaction evidence", task.getTaskName());
        assertEquals(ASSIGNED_TO, task.getAssignedTo());
        assertEquals("HIGH", task.getPriority());
        assertEquals("OPEN", task.getStatus());
        assertNotNull(task.getCreatedAt());

        CaseTaskResponse retrieved =
                service.getCaseTaskById(
                        created.getCaseId(),
                        task.getTaskId()
                );

        assertEquals(
                task.getTaskId(),
                retrieved.getTaskId()
        );
    }

    @Test
    void shouldReturnCaseTasks() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-TASK-002"
                        )
                );

        service.createCaseTask(
                created.getCaseId(),
                buildTaskRequest(
                        "Review device activity"
                )
        );

        service.createCaseTask(
                created.getCaseId(),
                buildTaskRequest(
                        "Review transaction history"
                )
        );

        assertEquals(
                2,
                service.getCaseTasks(
                        created.getCaseId()
                ).size()
        );
    }

    @Test
    void shouldRejectTaskLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-TASK-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-TASK-004"
                        )
                );

        CaseTaskResponse task =
                service.createCaseTask(
                        firstCase.getCaseId(),
                        buildTaskRequest(
                                "Verify account activity"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseTaskById(
                        secondCase.getCaseId(),
                        task.getTaskId()
                )
        );
    }

    @Test
    void shouldCreateAndRetrieveCaseComment() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-COMMENT-001"
                        )
                );

        CaseCommentResponse comment =
                service.createCaseComment(
                        created.getCaseId(),
                        buildCommentRequest(
                                "Investigation started"
                        )
                );

        assertNotNull(comment.getCommentId());
        assertEquals(created.getCaseId(), comment.getCaseId());
        assertEquals("INVESTIGATION_NOTE", comment.getCommentType());
        assertEquals("Investigation started", comment.getCommentText());
        assertEquals("INTERNAL", comment.getVisibility());
        assertEquals(ASSIGNED_TO, comment.getCreatedBy());
        assertNotNull(comment.getCreatedAt());

        CaseCommentResponse retrieved =
                service.getCaseCommentById(
                        created.getCaseId(),
                        comment.getCommentId()
                );

        assertEquals(
                comment.getCommentId(),
                retrieved.getCommentId()
        );
    }

    @Test
    void shouldReturnCaseComments() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-COMMENT-002"
                        )
                );

        service.createCaseComment(
                created.getCaseId(),
                buildCommentRequest(
                        "First investigation note"
                )
        );

        service.createCaseComment(
                created.getCaseId(),
                buildCommentRequest(
                        "Second investigation note"
                )
        );

        assertEquals(
                2,
                service.getCaseComments(
                        created.getCaseId()
                ).size()
        );
    }

    @Test
    void shouldRejectCommentLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-COMMENT-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-COMMENT-004"
                        )
                );

        CaseCommentResponse comment =
                service.createCaseComment(
                        firstCase.getCaseId(),
                        buildCommentRequest(
                                "Investigation note"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseCommentById(
                        secondCase.getCaseId(),
                        comment.getCommentId()
                )
        );
    }

    @Test
    void shouldCreateAndRetrieveCaseEvidence() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-EVIDENCE-001"
                        )
                );

        CaseEvidenceResponse evidence =
                service.createCaseEvidence(
                        created.getCaseId(),
                        buildEvidenceRequest(
                                "TRANSACTION_SCREENSHOT"
                        )
                );

        assertNotNull(evidence.getEvidenceId());
        assertEquals(created.getCaseId(), evidence.getCaseId());
        assertEquals(TRANSACTION_ID, evidence.getTransactionId());
        assertEquals("TRANSACTION_SCREENSHOT", evidence.getEvidenceType());
        assertEquals("INTERNAL_CASE_TOOL", evidence.getSourceSystem());
        assertEquals(ASSIGNED_TO, evidence.getUploadedBy());
        assertNotNull(evidence.getUploadedAt());

        CaseEvidenceResponse retrieved =
                service.getCaseEvidenceById(
                        created.getCaseId(),
                        evidence.getEvidenceId()
                );

        assertEquals(
                evidence.getEvidenceId(),
                retrieved.getEvidenceId()
        );
    }

    @Test
    void shouldReturnCaseEvidence() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-EVIDENCE-002"
                        )
                );

        service.createCaseEvidence(
                created.getCaseId(),
                buildEvidenceRequest(
                        "TRANSACTION_SCREENSHOT"
                )
        );

        service.createCaseEvidence(
                created.getCaseId(),
                buildEvidenceRequest(
                        "DEVICE_EVIDENCE"
                )
        );

        assertEquals(
                2,
                service.getCaseEvidence(
                        created.getCaseId()
                ).size()
        );
    }

    @Test
    void shouldRejectEvidenceLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-EVIDENCE-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-EVIDENCE-004"
                        )
                );

        CaseEvidenceResponse evidence =
                service.createCaseEvidence(
                        firstCase.getCaseId(),
                        buildEvidenceRequest(
                                "TRANSACTION_SCREENSHOT"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseEvidenceById(
                        secondCase.getCaseId(),
                        evidence.getEvidenceId()
                )
        );
    }

    @Test
    void shouldUpdateCaseStatusAndCreateHistory() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-STATUS-001"
                        )
                );

        CaseResponse updated =
                service.updateCaseStatus(
                        created.getCaseId(),
                        buildStatusRequest(
                                "IN_PROGRESS",
                                "Investigation started"
                        )
                );

        assertEquals(
                "IN_PROGRESS",
                updated.getCurrentStatus()
        );

        List<CaseStatusHistoryResponse> history =
                service.getCaseStatusHistory(
                        created.getCaseId()
                );

        assertEquals(1, history.size());
        assertEquals("OPEN", history.get(0).getPreviousStatus());
        assertEquals("IN_PROGRESS", history.get(0).getCurrentStatus());
        assertEquals(ASSIGNED_TO, history.get(0).getChangedBy());
    }

    @Test
    void shouldPreserveSequentialCaseStatusHistory() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-STATUS-002"
                        )
                );

        service.updateCaseStatus(
                created.getCaseId(),
                buildStatusRequest(
                        "IN_PROGRESS",
                        "Investigation started"
                )
        );

        service.updateCaseStatus(
                created.getCaseId(),
                buildStatusRequest(
                        "PENDING_INFORMATION",
                        "Additional information required"
                )
        );

        List<CaseStatusHistoryResponse> history =
                service.getCaseStatusHistory(
                        created.getCaseId()
                );

        assertEquals(2, history.size());
        assertEquals("PENDING_INFORMATION", history.get(0).getCurrentStatus());
        assertEquals("IN_PROGRESS", history.get(0).getPreviousStatus());
        assertEquals("IN_PROGRESS", history.get(1).getCurrentStatus());
        assertEquals("OPEN", history.get(1).getPreviousStatus());
    }

    @Test
    void shouldCreateAndRetrieveCaseResolution() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-RESOLUTION-001"
                        )
                );

        CaseResolutionResponse resolution =
                service.createCaseResolution(
                        created.getCaseId(),
                        buildResolutionRequest(
                                "CONFIRMED_FRAUD"
                        )
                );

        assertNotNull(resolution.getResolutionId());
        assertEquals(created.getCaseId(), resolution.getCaseId());
        assertEquals("CONFIRMED_FRAUD", resolution.getResolutionType());
        assertEquals(
                "Investigation completed with documented resolution",
                resolution.getResolutionSummary()
        );
        assertEquals(
                new BigDecimal("1500.00"),
                resolution.getEconomicImpact()
        );
        assertEquals("GTQ", resolution.getCurrencyCode());
        assertEquals(ASSIGNED_TO, resolution.getResolvedBy());
        assertEquals(ASSIGNED_FROM, resolution.getApprovedBy());
        assertNotNull(resolution.getResolvedAt());

        CaseResolutionResponse retrieved =
                service.getCaseResolutionById(
                        created.getCaseId(),
                        resolution.getResolutionId()
                );

        assertEquals(
                resolution.getResolutionId(),
                retrieved.getResolutionId()
        );
    }

    @Test
    void shouldReturnCaseResolutions() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-RESOLUTION-002"
                        )
                );

        service.createCaseResolution(
                created.getCaseId(),
                buildResolutionRequest(
                        "CONFIRMED_FRAUD"
                )
        );

        service.createCaseResolution(
                created.getCaseId(),
                buildResolutionRequest(
                        "FALSE_POSITIVE"
                )
        );

        List<CaseResolutionResponse> resolutions =
                service.getCaseResolutions(
                        created.getCaseId()
                );

        assertEquals(2, resolutions.size());
    }

    @Test
    void shouldRejectResolutionLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-RESOLUTION-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-RESOLUTION-004"
                        )
                );

        CaseResolutionResponse resolution =
                service.createCaseResolution(
                        firstCase.getCaseId(),
                        buildResolutionRequest(
                                "CONFIRMED_FRAUD"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseResolutionById(
                        secondCase.getCaseId(),
                        resolution.getResolutionId()
                )
        );
    }

    @Test
    void shouldCreateAndRetrieveCaseEscalation() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-ESCALATION-001"
                        )
                );

        CaseEscalationResponse escalation =
                service.createCaseEscalation(
                        created.getCaseId(),
                        buildEscalationRequest(
                                "LEVEL_2"
                        )
                );

        assertNotNull(escalation.getEscalationId());
        assertEquals(created.getCaseId(), escalation.getCaseId());
        assertEquals("LEVEL_2", escalation.getEscalationLevel());
        assertEquals("FRAUD_INVESTIGATION", escalation.getFromTeam());
        assertEquals("FRAUD_REVIEW", escalation.getToTeam());
        assertEquals(
                "Additional review required",
                escalation.getEscalationReason()
        );
        assertEquals(ASSIGNED_TO, escalation.getEscalatedBy());
        assertNotNull(escalation.getEscalatedAt());
        assertNull(escalation.getResolvedAt());

        CaseEscalationResponse retrieved =
                service.getCaseEscalationById(
                        created.getCaseId(),
                        escalation.getEscalationId()
                );

        assertEquals(
                escalation.getEscalationId(),
                retrieved.getEscalationId()
        );
    }

    @Test
    void shouldReturnCaseEscalations() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-ESCALATION-002"
                        )
                );

        service.createCaseEscalation(
                created.getCaseId(),
                buildEscalationRequest(
                        "LEVEL_1"
                )
        );

        service.createCaseEscalation(
                created.getCaseId(),
                buildEscalationRequest(
                        "LEVEL_2"
                )
        );

        List<CaseEscalationResponse> escalations =
                service.getCaseEscalations(
                        created.getCaseId()
                );

        assertEquals(2, escalations.size());
    }

    @Test
    void shouldRejectEscalationLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-ESCALATION-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-ESCALATION-004"
                        )
                );

        CaseEscalationResponse escalation =
                service.createCaseEscalation(
                        firstCase.getCaseId(),
                        buildEscalationRequest(
                                "LEVEL_2"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseEscalationById(
                        secondCase.getCaseId(),
                        escalation.getEscalationId()
                )
        );
    }

    @Test
    void shouldCreateAndRetrieveCaseSla() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-SLA-001"
                        )
                );

        CaseSlaResponse sla =
                service.createCaseSla(
                        created.getCaseId(),
                        buildSlaRequest(
                                "INVESTIGATION"
                        )
                );

        assertNotNull(sla.getSlaId());
        assertEquals(created.getCaseId(), sla.getCaseId());
        assertEquals("INVESTIGATION", sla.getSlaType());
        assertEquals(1440, sla.getTargetMinutes());
        assertEquals(0, sla.getElapsedMinutes());
        assertNotNull(sla.getDeadline());
        assertFalse(sla.getBreached());
        assertNull(sla.getBreachReason());
        assertNotNull(sla.getCalculatedAt());

        CaseSlaResponse retrieved =
                service.getCaseSlaById(
                        created.getCaseId(),
                        sla.getSlaId()
                );

        assertEquals(
                sla.getSlaId(),
                retrieved.getSlaId()
        );
    }

    @Test
    void shouldReturnCaseSlas() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-SLA-002"
                        )
                );

        service.createCaseSla(
                created.getCaseId(),
                buildSlaRequest(
                        "INITIAL_REVIEW"
                )
        );

        service.createCaseSla(
                created.getCaseId(),
                buildSlaRequest(
                        "INVESTIGATION"
                )
        );

        List<CaseSlaResponse> slas =
                service.getCaseSlas(
                        created.getCaseId()
                );

        assertEquals(2, slas.size());
    }

    @Test
    void shouldRejectSlaLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-SLA-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-SLA-004"
                        )
                );

        CaseSlaResponse sla =
                service.createCaseSla(
                        firstCase.getCaseId(),
                        buildSlaRequest(
                                "INVESTIGATION"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseSlaById(
                        secondCase.getCaseId(),
                        sla.getSlaId()
                )
        );
    }

    @Test
    void shouldCreateAndRetrieveCaseNotification() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-NOTIFICATION-001"
                        )
                );

        CaseNotificationResponse notification =
                service.createCaseNotification(
                        created.getCaseId(),
                        buildNotificationRequest(
                                "CASE_ASSIGNED"
                        )
                );

        assertNotNull(notification.getCaseNotificationId());
        assertEquals(created.getCaseId(), notification.getCaseId());
        assertEquals("CASE_ASSIGNED", notification.getNotificationType());
        assertEquals(ASSIGNED_TO, notification.getRecipientUserId());
        assertEquals("PENDING", notification.getNotificationStatus());
        assertEquals(
                "NOTIFICATION-REF-001",
                notification.getNotificationReference()
        );
        assertNull(notification.getDeliveryResult());
        assertNotNull(notification.getCreatedAt());
        assertNull(notification.getProcessedAt());

        CaseNotificationResponse retrieved =
                service.getCaseNotificationById(
                        created.getCaseId(),
                        notification.getCaseNotificationId()
                );

        assertEquals(
                notification.getCaseNotificationId(),
                retrieved.getCaseNotificationId()
        );
    }

    @Test
    void shouldReturnCaseNotifications() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-NOTIFICATION-002"
                        )
                );

        service.createCaseNotification(
                created.getCaseId(),
                buildNotificationRequest(
                        "CASE_ASSIGNED"
                )
        );

        service.createCaseNotification(
                created.getCaseId(),
                buildNotificationRequest(
                        "CASE_ESCALATED"
                )
        );

        List<CaseNotificationResponse> notifications =
                service.getCaseNotifications(
                        created.getCaseId()
                );

        assertEquals(2, notifications.size());
    }

    @Test
    void shouldRejectNotificationLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-NOTIFICATION-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-NOTIFICATION-004"
                        )
                );

        CaseNotificationResponse notification =
                service.createCaseNotification(
                        firstCase.getCaseId(),
                        buildNotificationRequest(
                                "CASE_ASSIGNED"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseNotificationById(
                        secondCase.getCaseId(),
                        notification.getCaseNotificationId()
                )
        );
    }

    @Test
    void shouldCreateAndRetrieveCaseHistory() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-HISTORY-001"
                        )
                );

        CaseHistoryResponse history =
                service.createCaseHistory(
                        created.getCaseId(),
                        buildHistoryRequest(
                                "ASSIGNMENT_CHANGED",
                                "Case assignment changed"
                        )
                );

        assertNotNull(history.getHistoryId());
        assertEquals(created.getCaseId(), history.getCaseId());
        assertEquals("ASSIGNMENT_CHANGED", history.getEventType());
        assertEquals(
                "Case assignment changed",
                history.getEventDescription()
        );
        assertEquals(
                "UNASSIGNED",
                history.getPreviousValue()
        );
        assertEquals(
                "FRAUD_INVESTIGATION",
                history.getNewValue()
        );
        assertEquals(
                ASSIGNED_TO,
                history.getChangedBy()
        );
        assertNotNull(
                history.getChangedAt()
        );

        CaseHistoryResponse retrieved =
                service.getCaseHistoryById(
                        created.getCaseId(),
                        history.getHistoryId()
                );

        assertEquals(
                history.getHistoryId(),
                retrieved.getHistoryId()
        );
    }

    @Test
    void shouldReturnCaseHistory() {

        CaseResponse created =
                service.createCase(
                        buildRequest(
                                "CASE-HISTORY-002"
                        )
                );

        service.createCaseHistory(
                created.getCaseId(),
                buildHistoryRequest(
                        "ASSIGNMENT_CHANGED",
                        "Case assignment changed"
                )
        );

        service.createCaseHistory(
                created.getCaseId(),
                buildHistoryRequest(
                        "PRIORITY_CHANGED",
                        "Case priority changed"
                )
        );

        List<CaseHistoryResponse> history =
                service.getCaseHistory(
                        created.getCaseId()
                );

        assertEquals(
                2,
                history.size()
        );
    }

    @Test
    void shouldRejectHistoryLookupFromDifferentCase() {

        CaseResponse firstCase =
                service.createCase(
                        buildRequest(
                                "CASE-HISTORY-003"
                        )
                );

        CaseResponse secondCase =
                service.createCase(
                        buildRequest(
                                "CASE-HISTORY-004"
                        )
                );

        CaseHistoryResponse history =
                service.createCaseHistory(
                        firstCase.getCaseId(),
                        buildHistoryRequest(
                                "ASSIGNMENT_CHANGED",
                                "Case assignment changed"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getCaseHistoryById(
                        secondCase.getCaseId(),
                        history.getHistoryId()
                )
        );
    }

    private CaseRequest buildRequest(
            String caseNumber) {

        CaseRequest request =
                new CaseRequest();

        request.setCaseNumber(caseNumber);
        request.setOrganizationId(ORGANIZATION_ID);
        request.setTransactionId(TRANSACTION_ID);
        request.setCustomerId(CUSTOMER_ID);
        request.setCaseType("FRAUD_INVESTIGATION");
        request.setCategory("TRANSACTION");

        return request;
    }

    private CaseTaskRequest buildTaskRequest(
            String taskName) {

        CaseTaskRequest request =
                new CaseTaskRequest();

        request.setTaskName(taskName);
        request.setTaskDescription(
                "Review transaction and supporting evidence"
        );
        request.setAssignedTo(ASSIGNED_TO);
        request.setPriority("HIGH");
        request.setStatus("OPEN");
        request.setDueDate(
                LocalDateTime.now().plusDays(1)
        );

        return request;
    }

    private CaseCommentRequest buildCommentRequest(
            String commentText) {

        CaseCommentRequest request =
                new CaseCommentRequest();

        request.setCommentType(
                "INVESTIGATION_NOTE"
        );
        request.setCommentText(
                commentText
        );
        request.setVisibility(
                "INTERNAL"
        );
        request.setCreatedBy(
                ASSIGNED_TO
        );

        return request;
    }

    private CaseEvidenceRequest buildEvidenceRequest(
            String evidenceType) {

        CaseEvidenceRequest request =
                new CaseEvidenceRequest();

        request.setTransactionId(
                TRANSACTION_ID
        );
        request.setEvidenceType(
                evidenceType
        );
        request.setSourceSystem(
                "INTERNAL_CASE_TOOL"
        );
        request.setStorageUri(
                "case-evidence://transaction/screenshot-001"
        );
        request.setChecksumSha256(
                "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        );
        request.setUploadedBy(
                ASSIGNED_TO
        );

        return request;
    }

    private CaseStatusUpdateRequest buildStatusRequest(
            String currentStatus,
            String changeReason) {

        CaseStatusUpdateRequest request =
                new CaseStatusUpdateRequest();

        request.setCurrentStatus(
                currentStatus
        );
        request.setChangeReason(
                changeReason
        );
        request.setChangedBy(
                ASSIGNED_TO
        );

        return request;
    }

    private CaseResolutionRequest buildResolutionRequest(
            String resolutionType) {

        CaseResolutionRequest request =
                new CaseResolutionRequest();

        request.setResolutionType(
                resolutionType
        );

        request.setResolutionSummary(
                "Investigation completed with documented resolution"
        );

        request.setEconomicImpact(
                new BigDecimal("1500.00")
        );

        request.setCurrencyCode(
                "GTQ"
        );

        request.setResolvedBy(
                ASSIGNED_TO
        );

        request.setApprovedBy(
                ASSIGNED_FROM
        );

        return request;
    }

    private CaseEscalationRequest buildEscalationRequest(
            String escalationLevel) {

        CaseEscalationRequest request =
                new CaseEscalationRequest();

        request.setEscalationLevel(
                escalationLevel
        );

        request.setFromTeam(
                "FRAUD_INVESTIGATION"
        );

        request.setToTeam(
                "FRAUD_REVIEW"
        );

        request.setEscalationReason(
                "Additional review required"
        );

        request.setEscalatedBy(
                ASSIGNED_TO
        );

        return request;
    }

    private CaseSlaRequest buildSlaRequest(
            String slaType) {

        CaseSlaRequest request =
                new CaseSlaRequest();

        request.setSlaType(
                slaType
        );

        request.setTargetMinutes(
                1440
        );

        request.setElapsedMinutes(
                0
        );

        request.setDeadline(
                LocalDateTime.now().plusDays(1)
        );

        request.setBreached(
                false
        );

        request.setBreachReason(
                null
        );

        return request;
    }

    private CaseNotificationRequest buildNotificationRequest(
            String notificationType) {

        CaseNotificationRequest request =
                new CaseNotificationRequest();

        request.setNotificationType(
                notificationType
        );

        request.setRecipientUserId(
                ASSIGNED_TO
        );

        request.setNotificationStatus(
                "PENDING"
        );

        request.setNotificationReference(
                "NOTIFICATION-REF-001"
        );

        request.setDeliveryResult(
                null
        );

        request.setProcessedAt(
                null
        );

        return request;
    }

    private CaseHistoryRequest buildHistoryRequest(
            String eventType,
            String eventDescription) {

        CaseHistoryRequest request =
                new CaseHistoryRequest();

        request.setEventType(
                eventType
        );

        request.setEventDescription(
                eventDescription
        );

        request.setPreviousValue(
                "UNASSIGNED"
        );

        request.setNewValue(
                "FRAUD_INVESTIGATION"
        );

        request.setChangedBy(
                ASSIGNED_TO
        );

        return request;
    }
}