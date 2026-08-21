package com.efs.modules.casemanagement.service;

import com.efs.modules.casemanagement.dto.CaseAssignmentRequest;
import com.efs.modules.casemanagement.dto.CaseAssignmentResponse;
import com.efs.modules.casemanagement.dto.CaseFromAlertRequest;
import com.efs.modules.casemanagement.dto.CaseRequest;
import com.efs.modules.casemanagement.dto.CaseResponse;
import com.efs.shared.exception.DuplicateRecordException;
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

        assertNotNull(
                created.getCaseId()
        );

        assertEquals(
                "CASE-TEST-001",
                created.getCaseNumber()
        );

        assertEquals(
                ORGANIZATION_ID,
                created.getOrganizationId()
        );

        assertEquals(
                CUSTOMER_ID,
                created.getCustomerId()
        );

        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );

        assertEquals(
                "FRAUD_INVESTIGATION",
                created.getCaseType()
        );

        assertEquals(
                "TRANSACTION",
                created.getCategory()
        );

        assertEquals(
                "MEDIUM",
                created.getSeverity()
        );

        assertEquals(
                "NORMAL",
                created.getPriority()
        );

        assertEquals(
                "OPEN",
                created.getCurrentStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );
    }

    @Test
    void shouldReturnCasesByCustomerAndTransaction() {

        service.createCase(
                buildRequest(
                        "CASE-TEST-002"
                )
        );

        List<CaseResponse> byCustomer =
                service.getCasesByCustomerId(
                        CUSTOMER_ID
                );

        assertEquals(
                1,
                byCustomer.size()
        );

        List<CaseResponse> byTransaction =
                service.getCasesByTransactionId(
                        TRANSACTION_ID
                );

        assertEquals(
                1,
                byTransaction.size()
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

        request.setAlertId(
                ALERT_ID
        );

        request.setCaseNumber(
                "CASE-ALERT-001"
        );

        request.setOrganizationId(
                ORGANIZATION_ID
        );

        request.setCaseType(
                "FRAUD_INVESTIGATION"
        );

        CaseResponse created =
                service.createCaseFromAlert(
                        request
                );

        assertNotNull(
                created.getCaseId()
        );

        assertEquals(
                CUSTOMER_ID,
                created.getCustomerId()
        );

        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );

        assertEquals(
                "TRANSACTION",
                created.getCategory()
        );

        assertEquals(
                "HIGH",
                created.getSeverity()
        );

        assertEquals(
                "NORMAL",
                created.getPriority()
        );

        assertEquals(
                "OPEN",
                created.getCurrentStatus()
        );

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

        firstRequest.setAlertId(
                ALERT_ID
        );

        firstRequest.setCaseNumber(
                "CASE-ALERT-002"
        );

        firstRequest.setOrganizationId(
                ORGANIZATION_ID
        );

        firstRequest.setCaseType(
                "FRAUD_INVESTIGATION"
        );

        service.createCaseFromAlert(
                firstRequest
        );

        CaseFromAlertRequest secondRequest =
                new CaseFromAlertRequest();

        secondRequest.setAlertId(
                ALERT_ID
        );

        secondRequest.setCaseNumber(
                "CASE-ALERT-003"
        );

        secondRequest.setOrganizationId(
                ORGANIZATION_ID
        );

        secondRequest.setCaseType(
                "FRAUD_INVESTIGATION"
        );

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

        assertNull(
                created.getAssignedUser()
        );

        CaseAssignmentRequest request =
                new CaseAssignmentRequest();

        request.setAssignedFrom(
                ASSIGNED_FROM
        );

        request.setAssignedTo(
                ASSIGNED_TO
        );

        request.setAssignedTeam(
                "FRAUD_INVESTIGATION"
        );

        request.setAssignmentReason(
                "Assigned for investigation"
        );

        CaseAssignmentResponse assignment =
                service.assignCase(
                        created.getCaseId(),
                        request
                );

        assertNotNull(
                assignment.getAssignmentId()
        );

        assertEquals(
                created.getCaseId(),
                assignment.getCaseId()
        );

        assertEquals(
                ASSIGNED_FROM,
                assignment.getAssignedFrom()
        );

        assertEquals(
                ASSIGNED_TO,
                assignment.getAssignedTo()
        );

        assertEquals(
                "FRAUD_INVESTIGATION",
                assignment.getAssignedTeam()
        );

        assertEquals(
                "Assigned for investigation",
                assignment.getAssignmentReason()
        );

        assertNotNull(
                assignment.getAssignedAt()
        );

        CaseResponse updated =
                service.getCaseById(
                        created.getCaseId()
                );

        assertEquals(
                ASSIGNED_TO,
                updated.getAssignedUser()
        );

        assertEquals(
                "FRAUD_INVESTIGATION",
                updated.getAssignedTeam()
        );
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

        request.setAssignedFrom(
                ASSIGNED_FROM
        );

        request.setAssignedTo(
                ASSIGNED_TO
        );

        request.setAssignedTeam(
                "FRAUD_INVESTIGATION"
        );

        request.setAssignmentReason(
                "Initial assignment"
        );

        service.assignCase(
                created.getCaseId(),
                request
        );

        List<CaseAssignmentResponse> assignments =
                service.getCaseAssignments(
                        created.getCaseId()
                );

        assertEquals(
                1,
                assignments.size()
        );

        assertEquals(
                ASSIGNED_TO,
                assignments.get(0).getAssignedTo()
        );

        assertEquals(
                "FRAUD_INVESTIGATION",
                assignments.get(0).getAssignedTeam()
        );

        assertEquals(
                "Initial assignment",
                assignments.get(0).getAssignmentReason()
        );
    }

    private CaseRequest buildRequest(
            String caseNumber) {

        CaseRequest request =
                new CaseRequest();

        request.setCaseNumber(
                caseNumber
        );

        request.setOrganizationId(
                ORGANIZATION_ID
        );

        request.setTransactionId(
                TRANSACTION_ID
        );

        request.setCustomerId(
                CUSTOMER_ID
        );

        request.setCaseType(
                "FRAUD_INVESTIGATION"
        );

        request.setCategory(
                "TRANSACTION"
        );

        return request;
    }
}