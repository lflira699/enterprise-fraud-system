package com.efs.modules.alert.service;

import com.efs.modules.alert.dto.AlertAssignmentRequest;
import com.efs.modules.alert.dto.AlertClosureRequest;
import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.dto.AlertStatusUpdateRequest;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AlertServiceIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "10101010-1010-1010-1010-101010101010"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "20202020-2020-2020-2020-202020202020"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "30303030-3030-3030-3030-303030303030"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "40404040-4040-4040-4040-404040404040"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "50505050-5050-5050-5050-505050505050"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "60606060-6060-6060-6060-606060606060"
            );

    private static final UUID CHANGED_BY =
            UUID.fromString(
                    "70707070-7070-7070-7070-707070707070"
            );

    private static final UUID WRONG_TRANSACTION_ID =
            UUID.fromString(
                    "80808080-8080-8080-8080-808080808080"
            );

    private static final UUID WRONG_RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "90909090-9090-9090-9090-909090909090"
            );

    private static final UUID ASSIGNED_USER_ID =
            UUID.fromString(
                    "abababab-abab-abab-abab-abababababab"
            );

    @Autowired
    private AlertServiceInterface service;

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
                "EFS-ALERT-TEST-ORG",
                "EFS Alert Test Organization",
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
                ASSIGNED_USER_ID,
                ORGANIZATION_ID,
                "efs.alert.test.user",
                "EFS Alert Test User",
                "efs.alert.test@example.com",
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
                "EFS-ALERT-TEST-CUSTOMER",
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
                "EFS-ALERT-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("500.00"),
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
                new BigDecimal("90.00"),
                "ALTO",
                "REVIEW",
                new BigDecimal("92.00"),
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
                new BigDecimal("92.00"),
                "Escalar inmediatamente",
                LocalDateTime.now(),
                false
        );
    }

    @Test
    void shouldCreateAndRetrieveAlert() {

        AlertResponse created =
                service.createAlert(
                        buildRequest()
                );

        assertNotNull(created.getAlertId());
        assertEquals(CUSTOMER_ID, created.getCustomerId());
        assertEquals(TRANSACTION_ID, created.getTransactionId());
        assertEquals(RISK_ASSESSMENT_ID, created.getRiskAssessmentId());
        assertEquals(DECISION_ID, created.getDecisionId());
        assertEquals("FRAUD", created.getAlertType());
        assertEquals("HIGH", created.getPriority());
        assertEquals("NEW", created.getStatus());
    }

    @Test
    void shouldReturnAlertsByDecision() {

        service.createAlert(buildRequest());

        List<AlertResponse> alerts =
                service.getAlertsByDecisionId(DECISION_ID);

        assertEquals(1, alerts.size());
    }

    @Test
    void shouldFilterAlerts() {

        service.createAlert(buildRequest());

        assertEquals(1, service.getAlertsByStatus("NEW").size());
        assertEquals(1, service.getAlertsByPriority("HIGH").size());
        assertEquals(1, service.getAlertsByType("FRAUD").size());
    }

    @Test
    void shouldUpdateAlertStatusAndCreateHistory() {

        AlertResponse created =
                service.createAlert(buildRequest());

        AlertStatusUpdateRequest request =
                new AlertStatusUpdateRequest();

        request.setStatus("IN_PROGRESS");
        request.setChangedBy(CHANGED_BY);
        request.setChangeReason("Investigation started");

        AlertResponse updated =
                service.updateAlertStatus(
                        created.getAlertId(),
                        request
                );

        assertEquals("IN_PROGRESS", updated.getStatus());

        List<AlertHistoryResponse> history =
                service.getAlertHistory(created.getAlertId());

        assertEquals(1, history.size());
        assertEquals("STATUS_CHANGE", history.get(0).getActionType());
    }

    @Test
    void shouldRejectUnsupportedAlertStatus() {

        AlertResponse created =
                service.createAlert(buildRequest());

        AlertStatusUpdateRequest request =
                new AlertStatusUpdateRequest();

        request.setStatus("UNKNOWN");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateAlertStatus(
                        created.getAlertId(),
                        request
                )
        );
    }

    @Test
    void shouldRejectTransactionMismatch() {

        AlertRequest request =
                buildRequest();

        request.setTransactionId(
                WRONG_TRANSACTION_ID
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAlert(request)
        );
    }

    @Test
    void shouldRejectRiskAssessmentMismatch() {

        AlertRequest request =
                buildRequest();

        request.setRiskAssessmentId(
                WRONG_RISK_ASSESSMENT_ID
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAlert(request)
        );
    }

    @Test
    void shouldAssignAlertAndCreateHistory() {

        AlertResponse created =
                service.createAlert(buildRequest());

        AlertAssignmentRequest request =
                new AlertAssignmentRequest();

        request.setAssignedTo(ASSIGNED_USER_ID);
        request.setAssignedTeam("FRAUD_INVESTIGATION");
        request.setChangedBy(CHANGED_BY);
        request.setChangeReason("Assigned for investigation");

        AlertResponse assigned =
                service.assignAlert(
                        created.getAlertId(),
                        request
                );

        assertEquals(
                ASSIGNED_USER_ID,
                assigned.getAssignedTo()
        );

        assertEquals(
                "FRAUD_INVESTIGATION",
                assigned.getAssignedTeam()
        );

        List<AlertHistoryResponse> history =
                service.getAlertHistory(created.getAlertId());

        assertEquals(1, history.size());
        assertEquals("ASSIGNMENT", history.get(0).getActionType());
    }

    @Test
    void shouldCloseAlertAndCreateHistory() {

        AlertResponse created =
                service.createAlert(buildRequest());

        AlertClosureRequest request =
                new AlertClosureRequest();

        request.setInvestigationResult(
                "No confirmed fraud after investigation"
        );

        request.setClosureReason(
                "False positive"
        );

        request.setClosedBy(
                CHANGED_BY
        );

        AlertResponse closed =
                service.closeAlert(
                        created.getAlertId(),
                        request
                );

        assertEquals(
                "CLOSED",
                closed.getStatus()
        );

        assertEquals(
                "False positive",
                closed.getClosureReason()
        );

        assertNotNull(
                closed.getClosedAt()
        );

        List<AlertHistoryResponse> history =
                service.getAlertHistory(
                        created.getAlertId()
                );

        assertEquals(
                1,
                history.size()
        );

        assertEquals(
                "CLOSURE",
                history.get(0).getActionType()
        );

        assertEquals(
                "NEW",
                history.get(0).getPreviousStatus()
        );

        assertEquals(
                "CLOSED",
                history.get(0).getNewStatus()
        );

        assertEquals(
                CHANGED_BY,
                history.get(0).getChangedBy()
        );
    }

    @Test
    void shouldRejectGenericStatusChangeToClosed() {

        AlertResponse created =
                service.createAlert(buildRequest());

        AlertStatusUpdateRequest request =
                new AlertStatusUpdateRequest();

        request.setStatus(
                "CLOSED"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateAlertStatus(
                        created.getAlertId(),
                        request
                )
        );
    }

    @Test
    void shouldRejectModificationAfterClosure() {

        AlertResponse created =
                service.createAlert(buildRequest());

        AlertClosureRequest closureRequest =
                new AlertClosureRequest();

        closureRequest.setInvestigationResult(
                "Investigation completed"
        );

        closureRequest.setClosureReason(
                "Closed after review"
        );

        closureRequest.setClosedBy(
                CHANGED_BY
        );

        service.closeAlert(
                created.getAlertId(),
                closureRequest
        );

        AlertAssignmentRequest assignmentRequest =
                new AlertAssignmentRequest();

        assignmentRequest.setAssignedTo(
                ASSIGNED_USER_ID
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.assignAlert(
                        created.getAlertId(),
                        assignmentRequest
                )
        );

        AlertStatusUpdateRequest statusRequest =
                new AlertStatusUpdateRequest();

        statusRequest.setStatus(
                "IN_PROGRESS"
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.updateAlertStatus(
                        created.getAlertId(),
                        statusRequest
                )
        );
    }

    private AlertRequest buildRequest() {

        AlertRequest request =
                new AlertRequest();

        request.setDecisionId(DECISION_ID);
        request.setAlertType("FRAUD");
        request.setCategory("TRANSACTION");
        request.setPriority("HIGH");
        request.setRiskScore(new BigDecimal("90.00"));

        return request;
    }
}