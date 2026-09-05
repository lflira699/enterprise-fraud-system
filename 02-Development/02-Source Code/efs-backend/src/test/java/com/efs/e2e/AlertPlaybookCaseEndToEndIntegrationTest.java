package com.efs.e2e;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import com.efs.modules.playbook.service.PlaybookServiceInterface;
import com.efs.modules.playbook.service.PlaybookVersionServiceInterface;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlertPlaybookCaseEndToEndIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "b1010101-b101-b101-b101-b10101010101"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "b2020202-b202-b202-b202-b20202020202"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "b3030303-b303-b303-b303-b30303030303"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "b4040404-b404-b404-b404-b40404040404"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "b5050505-b505-b505-b505-b50505050505"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "b6060606-b606-b606-b606-b60606060606"
            );

    private static final UUID ALERT_ID =
            UUID.fromString(
                    "b7070707-b707-b707-b707-b70707070707"
            );

    private static final String CASE_NUMBER =
            "E2E-ALERT-PB-CASE-001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookVersionServiceInterface
            playbookVersionService;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
        insertCustomer();
        insertTransaction();
        insertRiskAssessment();
        insertDecision();
        insertAlert();
    }

    @Test
    void shouldAllowAlertToDrivePlaybookExecutionAndInvestigationCase()
            throws Exception {

        /*
         * ---------------------------------------------------------
         * 1. PLAYBOOK VERSION FIXTURE
         * ---------------------------------------------------------
         */

        PlaybookVersionResponse version =
                createPlaybookVersion();

        /*
         * ---------------------------------------------------------
         * 2. PLAYBOOK EXECUTION FROM ALERT
         * ---------------------------------------------------------
         */

        String executionRequest =
                """
                {
                    "playbookVersionId": "%s",
                    "alertId": "%s",
                    "status": "TEST"
                }
                """.formatted(
                        version.getPlaybookVersionId(),
                        ALERT_ID
                );

        mockMvc.perform(
                        post(
                                "/api/v1/playbook-executions"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        executionRequest
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.playbookExecutionId"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.playbookVersionId"
                        ).value(
                                version
                                        .getPlaybookVersionId()
                                        .toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.alertId"
                        ).value(
                                ALERT_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        ).value(
                                "TEST"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.startedAt"
                        ).exists()
                );

        /*
         * ---------------------------------------------------------
         * 3. INVESTIGATION CASE FROM SAME ALERT
         * ---------------------------------------------------------
         */

        String caseRequest =
                """
                {
                    "alertId": "%s",
                    "caseNumber": "%s",
                    "organizationId": "%s",
                    "caseType": "FRAUD_INVESTIGATION"
                }
                """.formatted(
                        ALERT_ID,
                        CASE_NUMBER,
                        ORGANIZATION_ID
                );

        mockMvc.perform(
                        post(
                                "/api/v1/cases/from-alert"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        caseRequest
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.caseId"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.caseNumber"
                        ).value(
                                CASE_NUMBER
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.organizationId"
                        ).value(
                                ORGANIZATION_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.customerId"
                        ).value(
                                CUSTOMER_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.transactionId"
                        ).value(
                                TRANSACTION_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.category"
                        ).value(
                                "TRANSACTION"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.severity"
                        ).value(
                                "HIGH"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.priority"
                        ).value(
                                "NORMAL"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.currentStatus"
                        ).value(
                                "OPEN"
                        )
                );

        entityManager.flush();

        /*
         * ---------------------------------------------------------
         * 4. PLAYBOOK EXECUTION PERSISTENCE
         * ---------------------------------------------------------
         */

        PlaybookExecutionRow execution =
                jdbcTemplate.queryForObject(
                        """
                        SELECT
                            playbook_execution_id,
                            playbook_version_id,
                            alert_id,
                            scenario_id,
                            status,
                            started_at
                        FROM playbook.playbook_execution
                        WHERE alert_id = ?
                        """,
                        (resultSet, rowNumber) ->
                                new PlaybookExecutionRow(
                                        resultSet.getObject(
                                                "playbook_execution_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "playbook_version_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "alert_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "scenario_id",
                                                UUID.class
                                        ),
                                        resultSet.getString(
                                                "status"
                                        ),
                                        resultSet.getObject(
                                                "started_at",
                                                LocalDateTime.class
                                        )
                                ),
                        ALERT_ID
                );

        assertNotNull(
                execution
        );

        assertNotNull(
                execution.playbookExecutionId()
        );

        assertEquals(
                version.getPlaybookVersionId(),
                execution.playbookVersionId()
        );

        assertEquals(
                ALERT_ID,
                execution.alertId()
        );

        assertNull(
                execution.scenarioId()
        );

        assertEquals(
                "TEST",
                execution.status()
        );

        assertNotNull(
                execution.startedAt()
        );

        /*
         * ---------------------------------------------------------
         * 5. CASE PERSISTENCE
         * ---------------------------------------------------------
         */

        CaseRow persistedCase =
                jdbcTemplate.queryForObject(
                        """
                        SELECT
                            case_id,
                            customer_id,
                            transaction_id,
                            current_status
                        FROM case_management.case
                        WHERE case_number = ?
                        """,
                        (resultSet, rowNumber) ->
                                new CaseRow(
                                        resultSet.getObject(
                                                "case_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "customer_id",
                                                UUID.class
                                        ),
                                        resultSet.getObject(
                                                "transaction_id",
                                                UUID.class
                                        ),
                                        resultSet.getString(
                                                "current_status"
                                        )
                                ),
                        CASE_NUMBER
                );

        assertNotNull(
                persistedCase
        );

        assertNotNull(
                persistedCase.caseId()
        );

        assertEquals(
                CUSTOMER_ID,
                persistedCase.customerId()
        );

        assertEquals(
                TRANSACTION_ID,
                persistedCase.transactionId()
        );

        assertEquals(
                "OPEN",
                persistedCase.currentStatus()
        );

        /*
         * ---------------------------------------------------------
         * 6. COMMON ALERT TRACEABILITY
         * ---------------------------------------------------------
         */

        Integer executionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM playbook.playbook_execution
                        WHERE alert_id = ?
                          AND playbook_version_id = ?
                        """,
                        Integer.class,
                        ALERT_ID,
                        version.getPlaybookVersionId()
                );

        assertEquals(
                1,
                executionCount
        );

        Integer caseCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case
                        WHERE case_id = ?
                          AND customer_id = ?
                          AND transaction_id = ?
                        """,
                        Integer.class,
                        persistedCase.caseId(),
                        CUSTOMER_ID,
                        TRANSACTION_ID
                );

        assertEquals(
                1,
                caseCount
        );

        Integer caseAlertLinkCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM case_management.case_alert
                        WHERE case_id = ?
                          AND source_alert_id = ?
                          AND transaction_id = ?
                        """,
                        Integer.class,
                        persistedCase.caseId(),
                        ALERT_ID,
                        TRANSACTION_ID
                );

        assertEquals(
                1,
                caseAlertLinkCount
        );

        /*
         * ---------------------------------------------------------
         * 7. API RETRIEVAL VALIDATION
         * ---------------------------------------------------------
         */

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-executions/alert/{alertId}",
                                ALERT_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.length()"
                        ).value(
                                1
                        )
                )
                .andExpect(
                        jsonPath(
                                "$[0].playbookExecutionId"
                        ).value(
                                execution
                                        .playbookExecutionId()
                                        .toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$[0].playbookVersionId"
                        ).value(
                                version
                                        .getPlaybookVersionId()
                                        .toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$[0].alertId"
                        ).value(
                                ALERT_ID.toString()
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/cases/{caseId}",
                                persistedCase.caseId()
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.caseId"
                        ).value(
                                persistedCase
                                        .caseId()
                                        .toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.customerId"
                        ).value(
                                CUSTOMER_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.transactionId"
                        ).value(
                                TRANSACTION_ID.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.currentStatus"
                        ).value(
                                "OPEN"
                        )
                );
    }

    private PlaybookVersionResponse createPlaybookVersion() {

        String code =
                "PB-ALERT-CASE-E2E-"
                        + UUID.randomUUID();

        PlaybookRequest playbookRequest =
                new PlaybookRequest();

        playbookRequest.setPlaybookCode(
                code
        );

        playbookRequest.setPlaybookName(
                code
        );

        playbookRequest.setStatus(
                "TEST"
        );

        PlaybookResponse playbook =
                playbookService.create(
                        playbookRequest
                );

        PlaybookVersionRequest versionRequest =
                new PlaybookVersionRequest();

        versionRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionRequest.setVersionNumber(
                1
        );

        versionRequest.setStatus(
                "TEST"
        );

        return playbookVersionService.create(
                versionRequest
        );
    }

    private void insertOrganization() {

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
                "EFS-ALERT-PB-CASE-E2E-ORG",
                "EFS Alert Playbook Case E2E",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser() {

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
                CREATED_BY,
                ORGANIZATION_ID,
                "efs.alert.pb.case.e2e",
                "EFS Alert Playbook Case E2E User",
                "efs.alert.pb.case.e2e@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertCustomer() {

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
                "EFS-ALERT-PB-CASE-CUSTOMER",
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );
    }

    private void insertTransaction() {

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
                "EFS-ALERT-PB-CASE-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("1250.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CREATED_BY,
                1
        );
    }

    private void insertRiskAssessment() {

        LocalDateTime now =
                LocalDateTime.now();

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
                new BigDecimal("91.00"),
                "HIGH",
                "REVIEW",
                new BigDecimal("93.00"),
                now,
                now,
                now,
                0
        );
    }

    private void insertDecision() {

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
                new BigDecimal("93.00"),
                "Escalate to investigation",
                LocalDateTime.now(),
                false
        );
    }

    private void insertAlert() {

        LocalDateTime now =
                LocalDateTime.now();

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
                new BigDecimal("91.00"),
                now,
                now,
                now
        );
    }

    private record PlaybookExecutionRow(
            UUID playbookExecutionId,
            UUID playbookVersionId,
            UUID alertId,
            UUID scenarioId,
            String status,
            LocalDateTime startedAt) {
    }

    private record CaseRow(
            UUID caseId,
            UUID customerId,
            UUID transactionId,
            String currentStatus) {
    }
}
