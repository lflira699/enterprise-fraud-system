package com.efs.modules.alert.service;

import com.efs.modules.alert.dto.AlertAssignmentRequest;
import com.efs.modules.alert.dto.AlertClosureRequest;
import com.efs.modules.alert.dto.AlertHistoryResponse;
import com.efs.modules.alert.dto.AlertRequest;
import com.efs.modules.alert.dto.AlertResponse;
import com.efs.modules.alert.dto.AlertStatusUpdateRequest;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
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

    private static final UUID REASSIGNED_USER_ID =
            UUID.fromString(
                    "bcbcbcbc-bcbc-bcbc-bcbc-bcbcbcbcbcbc"
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
                REASSIGNED_USER_ID,
                ORGANIZATION_ID,
                "efs.alert.reassigned.user",
                "EFS Alert Reassigned User",
                "efs.alert.reassigned@example.com",
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
    void shouldReviewAlertByIdWithoutModification() {

        AlertResponse created =
                service.createAlert(
                        buildRequest()
                );

        Integer versionBeforeReview =
                jdbcTemplate.queryForObject(
                        """
                        SELECT record_version
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        created.getAlertId()
                );

        Integer historyBeforeReview =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert_history
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        created.getAlertId()
                );

        AlertResponse reviewed =
                service.getAlertById(
                        created.getAlertId()
                );

        assertEquals(
                created.getAlertId(),
                reviewed.getAlertId()
        );

        assertEquals(
                CUSTOMER_ID,
                reviewed.getCustomerId()
        );

        assertEquals(
                TRANSACTION_ID,
                reviewed.getTransactionId()
        );

        assertEquals(
                DECISION_ID,
                reviewed.getDecisionId()
        );

        assertEquals(
                "NEW",
                reviewed.getStatus()
        );

        Integer versionAfterReview =
                jdbcTemplate.queryForObject(
                        """
                        SELECT record_version
                        FROM alert.alert
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        created.getAlertId()
                );

        Integer historyAfterReview =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM alert.alert_history
                        WHERE alert_id = ?
                        """,
                        Integer.class,
                        created.getAlertId()
                );

        assertEquals(
                versionBeforeReview,
                versionAfterReview
        );

        assertEquals(
                historyBeforeReview,
                historyAfterReview
        );
    }

    @Test
    void shouldRejectReviewForUnknownAlert() {

        UUID unknownAlertId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getAlertById(
                        unknownAlertId
                )
        );
    }

    @Test
    void shouldAllowReviewOfClosedAlert() {

        AlertResponse created =
                service.createAlert(
                        buildRequest()
                );

        AlertClosureRequest closureRequest =
                new AlertClosureRequest();

        closureRequest.setInvestigationResult(
                "Investigation completed"
        );

        closureRequest.setClosureReason(
                "False positive"
        );

        closureRequest.setClosedBy(
                CHANGED_BY
        );

        AlertResponse closed =
                service.closeAlert(
                        created.getAlertId(),
                        closureRequest
                );

        AlertResponse reviewed =
                service.getAlertById(
                        created.getAlertId()
                );

        assertEquals(
                closed.getAlertId(),
                reviewed.getAlertId()
        );

        assertEquals(
                "CLOSED",
                reviewed.getStatus()
        );

        assertEquals(
                closed.getClosedAt(),
                reviewed.getClosedAt()
        );

        assertEquals(
                "False positive",
                reviewed.getClosureReason()
        );
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
    void shouldAllowAlertReassignmentAndCreateHistory() {

        AlertResponse created =
                service.createAlert(
                        buildRequest()
                );

        AlertAssignmentRequest firstRequest =
                new AlertAssignmentRequest();

        firstRequest.setAssignedTo(
                ASSIGNED_USER_ID
        );
        firstRequest.setAssignedTeam(
                "FRAUD_INVESTIGATION"
        );
        firstRequest.setChangedBy(
                CHANGED_BY
        );
        firstRequest.setChangeReason(
                "Initial assignment"
        );

        service.assignAlert(
                created.getAlertId(),
                firstRequest
        );

        AlertAssignmentRequest secondRequest =
                new AlertAssignmentRequest();

        secondRequest.setAssignedTo(
                REASSIGNED_USER_ID
        );
        secondRequest.setAssignedTeam(
                "FRAUD_INVESTIGATION"
        );
        secondRequest.setChangedBy(
                CHANGED_BY
        );
        secondRequest.setChangeReason(
                "Reassigned for investigation"
        );

        AlertResponse reassigned =
                service.assignAlert(
                        created.getAlertId(),
                        secondRequest
                );

        assertEquals(
                REASSIGNED_USER_ID,
                reassigned.getAssignedTo()
        );

        List<AlertHistoryResponse> history =
                service.getAlertHistory(
                        created.getAlertId()
                );

        assertEquals(
                2,
                history.size()
        );

        assertEquals(
                "ASSIGNMENT",
                history.get(0).getActionType()
        );

        assertEquals(
                "ASSIGNMENT",
                history.get(1).getActionType()
        );
    }

    @Test
    void shouldRejectAssignmentForUnknownAlert() {

        UUID unknownAlertId =
                UUID.randomUUID();

        AlertAssignmentRequest request =
                new AlertAssignmentRequest();

        request.setAssignedTo(
                ASSIGNED_USER_ID
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.assignAlert(
                        unknownAlertId,
                        request
                )
        );
    }

    @Test
    void shouldRejectAssignmentForClosedAlert() {

        AlertResponse created =
                service.createAlert(
                        buildRequest()
                );

        AlertClosureRequest closureRequest =
                new AlertClosureRequest();

        closureRequest.setInvestigationResult(
                "Investigation completed"
        );

        closureRequest.setClosureReason(
                "Closed after investigation"
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

        assignmentRequest.setAssignedTeam(
                "FRAUD_INVESTIGATION"
        );

        assignmentRequest.setChangedBy(
                CHANGED_BY
        );

        assignmentRequest.setChangeReason(
                "Must not assign closed alert"
        );

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> service.assignAlert(
                                created.getAlertId(),
                                assignmentRequest
                        )
                );

        assertEquals(
                "Alert is not available for assignment",
                exception.getMessage()
        );

        AlertResponse persistedAlert =
                service.getAlertById(
                        created.getAlertId()
                );

        assertEquals(
                "CLOSED",
                persistedAlert.getStatus()
        );

        assertEquals(
                null,
                persistedAlert.getAssignedTo()
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
    void shouldRejectClosingUnknownAlert() {

        UUID unknownAlertId =
                UUID.randomUUID();

        AlertClosureRequest request =
                new AlertClosureRequest();

        request.setInvestigationResult(
                "Investigation completed"
        );

        request.setClosureReason(
                "Closure attempt for unknown alert"
        );

        request.setClosedBy(
                CHANGED_BY
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.closeAlert(
                        unknownAlertId,
                        request
                )
        );
    }

    @Test
    void shouldRejectClosingAlreadyClosedAlert() {

        AlertResponse created =
                service.createAlert(
                        buildRequest()
                );

        AlertClosureRequest firstRequest =
                new AlertClosureRequest();

        firstRequest.setInvestigationResult(
                "Investigation completed"
        );

        firstRequest.setClosureReason(
                "False positive"
        );

        firstRequest.setClosedBy(
                CHANGED_BY
        );

        AlertResponse firstClosure =
                service.closeAlert(
                        created.getAlertId(),
                        firstRequest
                );

        List<AlertHistoryResponse> historyBeforeRetry =
                service.getAlertHistory(
                        created.getAlertId()
                );

        AlertClosureRequest secondRequest =
                new AlertClosureRequest();

        secondRequest.setInvestigationResult(
                "Second closure attempt"
        );

        secondRequest.setClosureReason(
                "Must not replace original closure"
        );

        secondRequest.setClosedBy(
                CHANGED_BY
        );

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> service.closeAlert(
                                created.getAlertId(),
                                secondRequest
                        )
                );

        assertEquals(
                "Alert is already closed",
                exception.getMessage()
        );

        AlertResponse persistedAlert =
                service.getAlertById(
                        created.getAlertId()
                );

        assertEquals(
                "CLOSED",
                persistedAlert.getStatus()
        );

        assertEquals(
                firstClosure.getClosedAt(),
                persistedAlert.getClosedAt()
        );

        assertEquals(
                "False positive",
                persistedAlert.getClosureReason()
        );

        List<AlertHistoryResponse> historyAfterRetry =
                service.getAlertHistory(
                        created.getAlertId()
                );

        assertEquals(
                historyBeforeRetry.size(),
                historyAfterRetry.size()
        );

        assertEquals(
                1,
                historyAfterRetry.size()
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
                ValidationException.class,
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
