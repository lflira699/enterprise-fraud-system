package com.efs.modules.playbook.controller;

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
class PlaybookExecutionAlertOriginIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "a1010101-a101-a101-a101-a10101010101"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "a2020202-a202-a202-a202-a20202020202"
            );

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "a3030303-a303-a303-a303-a30303030303"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "a4040404-a404-a404-a404-a40404040404"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "a5050505-a505-a505-a505-a50505050505"
            );

    private static final UUID DECISION_ID =
            UUID.fromString(
                    "a6060606-a606-a606-a606-a60606060606"
            );

    private static final UUID ALERT_ID =
            UUID.fromString(
                    "a7070707-a707-a707-a707-a70707070707"
            );

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
    void shouldCreateExplicitPlaybookExecutionFromAlert()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion();

        String requestBody =
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
                                        "application/json"
                                )
                                .content(
                                        requestBody
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

        entityManager.flush();

        ExecutionRow persisted =
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
                                new ExecutionRow(
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
                persisted
        );

        assertNotNull(
                persisted.playbookExecutionId()
        );

        assertEquals(
                version.getPlaybookVersionId(),
                persisted.playbookVersionId()
        );

        assertEquals(
                ALERT_ID,
                persisted.alertId()
        );

        assertNull(
                persisted.scenarioId()
        );

        assertEquals(
                "TEST",
                persisted.status()
        );

        assertNotNull(
                persisted.startedAt()
        );

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
                                persisted
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
                )
                .andExpect(
                        jsonPath(
                                "$[0].status"
                        ).value(
                                "TEST"
                        )
                );
    }

    private PlaybookVersionResponse createPlaybookVersion() {

        String code =
                "PB-ALERT-ORIGIN-" +
                        UUID.randomUUID();

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
                "EFS-PB-ALERT-ORIGIN-ORG",
                "EFS Playbook Alert Origin Integration Test",
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
                "efs.pb.alert.origin",
                "EFS Playbook Alert Origin Test User",
                "efs.pb.alert.origin@example.com",
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
                "EFS-PB-ALERT-ORIGIN-CUSTOMER",
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
                "EFS-PB-ALERT-ORIGIN-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("750.00"),
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
                    priority,
                    status,
                    risk_score,
                    generated_at,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                ALERT_ID,
                CUSTOMER_ID,
                TRANSACTION_ID,
                DECISION_ID,
                RISK_ASSESSMENT_ID,
                "FRAUD",
                "TRANSACTION",
                "HIGH",
                "NEW",
                new BigDecimal("91.00"),
                now,
                now,
                now
        );
    }

    private record ExecutionRow(
            UUID playbookExecutionId,
            UUID playbookVersionId,
            UUID alertId,
            UUID scenarioId,
            String status,
            LocalDateTime startedAt) {
    }
}