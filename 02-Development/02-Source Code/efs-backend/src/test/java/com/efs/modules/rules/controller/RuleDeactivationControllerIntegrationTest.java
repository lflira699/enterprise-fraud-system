package com.efs.modules.rules.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleDeactivationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldDeactivateActiveRuleWithHistoryAndAudit()
            throws Exception {

        UUID organizationId =
                UUID.randomUUID();

        UUID changedBy =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        insertRuleAdministrator(
                organizationId,
                changedBy
        );

        UUID ruleId =
                insertRule(
                        "RULE-DEACTIVATE-001",
                        "ACTIVE"
                );

        String requestBody =
                """
                {
                    "changedBy": "%s",
                    "changeReason": "Controlled rule deactivation",
                    "correlationId": "%s"
                }
                """.formatted(
                        changedBy,
                        correlationId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/deactivate",
                                ruleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleId")
                        .value(ruleId.toString()))
                .andExpect(jsonPath("$.ruleCode")
                        .value("RULE-DEACTIVATE-001"))
                .andExpect(jsonPath("$.status")
                        .value("INACTIVE"))
                .andExpect(jsonPath("$.currentVersion")
                        .value(1))
                .andExpect(jsonPath("$.updatedAt")
                        .exists());

        entityManager.flush();

        Map<String, Object> persistedRule =
                jdbcTemplate.queryForMap(
                        """
                        SELECT
                            status,
                            current_version
                        FROM rules.rule
                        WHERE rule_id = ?
                        """,
                        ruleId
                );

        assertEquals(
                "INACTIVE",
                persistedRule.get("status")
        );

        assertEquals(
                1,
                ((Number) persistedRule
                        .get("current_version"))
                        .intValue()
        );

        Integer ruleVersionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_version
                        WHERE rule_id = ?
                        """,
                        Integer.class,
                        ruleId
                );

        assertEquals(
                0,
                ruleVersionCount
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND operation_type = 'DEACTIVATION'
                          AND changed_by = ?
                          AND correlation_id = ?
                          AND change_reason = ?
                          AND previous_value
                              ->> 'status' = 'ACTIVE'
                          AND current_value
                              ->> 'status' = 'INACTIVE'
                        """,
                        Integer.class,
                        ruleId,
                        changedBy,
                        correlationId,
                        "Controlled rule deactivation"
                );

        assertEquals(
                1,
                historyCount
        );

        Integer auditEventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND event_type = 'RULE_DEACTIVATED'
                          AND action = 'DEACTIVATE'
                          AND source_component = 'RULE_ENGINE'
                          AND event_result = 'SUCCESS'
                          AND user_id = ?
                          AND correlation_id = ?
                          AND event_details
                              ->> 'ruleId' = ?
                          AND event_details
                              ->> 'previousStatus' = 'ACTIVE'
                          AND event_details
                              ->> 'newStatus' = 'INACTIVE'
                        """,
                        Integer.class,
                        ruleId,
                        changedBy,
                        correlationId,
                        ruleId.toString()
                );

        assertEquals(
                1,
                auditEventCount
        );

        Integer entityChangeCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_entity_change change_record
                        JOIN audit.audit_event event_record
                          ON event_record.audit_event_id =
                             change_record.audit_event_id
                        WHERE change_record.entity_type = 'RULE'
                          AND change_record.entity_id = ?
                          AND change_record.operation = 'UPDATE'
                          AND event_record.entity_id = ?
                          AND event_record.event_type = 'RULE_DEACTIVATED'
                          AND change_record.previous_value
                              ->> 'status' = 'ACTIVE'
                          AND change_record.current_value
                              ->> 'status' = 'INACTIVE'
                        """,
                        Integer.class,
                        ruleId,
                        ruleId
                );

        assertEquals(
                1,
                entityChangeCount
        );
    }

    @Test
    void shouldReturnInactiveRuleWithoutDuplicateAuditWhenAlreadyInactive()
            throws Exception {

        UUID organizationId =
                UUID.randomUUID();

        UUID changedBy =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        insertRuleAdministrator(
                organizationId,
                changedBy
        );

        UUID ruleId =
                insertRule(
                        "RULE-DEACTIVATE-002",
                        "INACTIVE"
                );

        String requestBody =
                """
                {
                    "changedBy": "%s",
                    "changeReason": "Repeated deactivation request",
                    "correlationId": "%s"
                }
                """.formatted(
                        changedBy,
                        correlationId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/deactivate",
                                ruleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleId")
                        .value(ruleId.toString()))
                .andExpect(jsonPath("$.status")
                        .value("INACTIVE"))
                .andExpect(jsonPath("$.currentVersion")
                        .value(1));

        entityManager.flush();

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND operation_type = 'DEACTIVATION'
                        """,
                        Integer.class,
                        ruleId
                );

        assertEquals(
                0,
                historyCount
        );

        Integer auditEventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND event_type = 'RULE_DEACTIVATED'
                        """,
                        Integer.class,
                        ruleId
                );

        assertEquals(
                0,
                auditEventCount
        );

        String persistedStatus =
                jdbcTemplate.queryForObject(
                        """
                        SELECT status
                        FROM rules.rule
                        WHERE rule_id = ?
                        """,
                        String.class,
                        ruleId
                );

        assertEquals(
                "INACTIVE",
                persistedStatus
        );
    }

    @Test
    void shouldReturnNotFoundWhenDeactivatingUnknownRule()
            throws Exception {

        UUID unknownRuleId =
                UUID.randomUUID();

        String requestBody =
                """
                {
                    "changedBy": "%s"
                }
                """.formatted(
                        UUID.randomUUID()
                );

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/deactivate",
                                unknownRuleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectRuleDeactivationWithoutActor()
            throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-DEACTIVATE-003",
                        "ACTIVE"
                );

        String requestBody =
                """
                {
                    "changeReason": "Missing actor"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/deactivate",
                                ruleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDeactivationWhenRuleIsNotActiveOrInactive()
            throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-DEACTIVATE-004",
                        "DRAFT"
                );

        String requestBody =
                """
                {
                    "changedBy": "%s"
                }
                """.formatted(
                        UUID.randomUUID()
                );

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/deactivate",
                                ruleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value("BUSINESS_VALIDATION_ERROR"));
    }

    private UUID insertRule(
            String ruleCode,
            String status) {

        UUID ruleId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule (
                    rule_id,
                    rule_code,
                    rule_name,
                    description,
                    category,
                    severity,
                    priority,
                    owner_team,
                    current_version,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                ruleId,
                ruleCode,
                "Rule Deactivation Integration Test",
                "Rule deactivation integration test",
                "TRANSACTION",
                "HIGH",
                (short) 2,
                "FRAUD_RULES",
                1,
                status
        );

        return ruleId;
    }

    private void insertRuleAdministrator(
            UUID organizationId,
            UUID userId) {

        String suffix =
                organizationId
                        .toString()
                        .substring(0, 8);

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
                organizationId,
                "RULE-DEACT-" + suffix,
                "EFS Rule Deactivation Test Organization",
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
                userId,
                organizationId,
                "efs.rule.deactivate." + suffix,
                "EFS Rule Deactivation Test User",
                "efs.rule.deactivate."
                        + suffix
                        + "@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}