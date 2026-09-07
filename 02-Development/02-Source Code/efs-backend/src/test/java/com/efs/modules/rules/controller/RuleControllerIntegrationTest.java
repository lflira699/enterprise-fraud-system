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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldCreateRuleThroughApi() throws Exception {

        String requestBody =
                """
                {
                    "ruleCode": "RULE-API-001",
                    "ruleName": "High Value Transaction Rule",
                    "description": "Rule controller integration test",
                    "category": "TRANSACTION",
                    "severity": "HIGH",
                    "priority": 1,
                    "ownerTeam": "FRAUD_RULES",
                    "currentVersion": 1,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/rules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ruleId").exists())
                .andExpect(jsonPath("$.ruleCode").value("RULE-API-001"))
                .andExpect(jsonPath("$.ruleName").value("High Value Transaction Rule"))
                .andExpect(jsonPath("$.category").value("TRANSACTION"))
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.priority").value(1))
                .andExpect(jsonPath("$.ownerTeam").value("FRAUD_RULES"))
                .andExpect(jsonPath("$.currentVersion").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldRetrieveAllRulesThroughApi() throws Exception {

        insertRule(
                "RULE-LIST-API-001",
                "TRANSACTION",
                "HIGH",
                (short) 5,
                "ACTIVE"
        );

        insertRule(
                "RULE-LIST-API-002",
                "ATO",
                "CRITICAL",
                (short) 1,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/rules")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleCode").value("RULE-LIST-API-002"))
                .andExpect(jsonPath("$[0].priority").value(1))
                .andExpect(jsonPath("$[1].ruleCode").value("RULE-LIST-API-001"))
                .andExpect(jsonPath("$[1].priority").value(5));
    }

    @Test
    void shouldRetrieveRuleByIdThroughApi() throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-API-002",
                        "TRANSACTION",
                        "HIGH",
                        (short) 2,
                        "ACTIVE"
                );

        mockMvc.perform(
                        get("/api/v1/rules/{ruleId}",
                                ruleId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleId").value(ruleId.toString()))
                .andExpect(jsonPath("$.ruleCode").value("RULE-API-002"))
                .andExpect(jsonPath("$.category").value("TRANSACTION"))
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.priority").value(2))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldRetrieveRuleByCodeThroughApi() throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-API-003",
                        "ATO",
                        "CRITICAL",
                        (short) 1,
                        "ACTIVE"
                );

        mockMvc.perform(
                        get("/api/v1/rules/code/{ruleCode}",
                                "RULE-API-003")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleId").value(ruleId.toString()))
                .andExpect(jsonPath("$.ruleCode").value("RULE-API-003"))
                .andExpect(jsonPath("$.category").value("ATO"))
                .andExpect(jsonPath("$.severity").value("CRITICAL"));
    }

    @Test
    void shouldRetrieveRulesByStatusThroughApi() throws Exception {

        insertRule(
                "RULE-STATUS-API-001",
                "TRANSACTION",
                "HIGH",
                (short) 5,
                "ACTIVE"
        );

        insertRule(
                "RULE-STATUS-API-002",
                "TRANSACTION",
                "MEDIUM",
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/rules/status/{status}",
                                "ACTIVE")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value(2))
                .andExpect(jsonPath("$[1].priority").value(5));
    }

    @Test
    void shouldRetrieveRulesByCategoryThroughApi() throws Exception {

        insertRule(
                "RULE-CATEGORY-API-001",
                "ATO",
                "HIGH",
                (short) 4,
                "ACTIVE"
        );

        insertRule(
                "RULE-CATEGORY-API-002",
                "ATO",
                "CRITICAL",
                (short) 1,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/rules/category/{category}",
                                "ATO")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("ATO"))
                .andExpect(jsonPath("$[0].priority").value(1))
                .andExpect(jsonPath("$[1].category").value("ATO"))
                .andExpect(jsonPath("$[1].priority").value(4));
    }

    @Test
    void shouldRetrieveRulesBySeverityThroughApi() throws Exception {

        insertRule(
                "RULE-SEVERITY-API-001",
                "TRANSACTION",
                "HIGH",
                (short) 3,
                "ACTIVE"
        );

        insertRule(
                "RULE-SEVERITY-API-002",
                "ATO",
                "HIGH",
                (short) 1,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/rules/severity/{severity}",
                                "HIGH")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].severity").value("HIGH"))
                .andExpect(jsonPath("$[0].priority").value(1))
                .andExpect(jsonPath("$[1].severity").value("HIGH"))
                .andExpect(jsonPath("$[1].priority").value(3));
    }

    @Test
    void shouldCreateDraftRuleVersionThroughUpdateApiWithHistoryAndAudit()
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
                        "RULE-UPDATE-API-001",
                        "TRANSACTION",
                        "HIGH",
                        (short) 2,
                        "ACTIVE"
                );

        insertRuleVersion(
                ruleId,
                changedBy,
                1,
                "PUBLISHED",
                "Rule Controller Integration Test",
                "Rule controller integration test",
                "TRANSACTION",
                "HIGH",
                (short) 2,
                "FRAUD_RULES"
        );

        String requestBody =
                """
                {
                    "ruleName": "Updated Fraud Detection Rule",
                    "description": "Updated rule information",
                    "category": "ATO",
                    "severity": "CRITICAL",
                    "priority": 3,
                    "ownerTeam": "FRAUD_GOVERNANCE",
                    "changedBy": "%s",
                    "changeReason": "Controlled rule information update",
                    "correlationId": "%s"
                }
                """.formatted(
                        changedBy,
                        correlationId
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/rules/{ruleId}",
                                ruleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleVersionId").exists())
                .andExpect(jsonPath("$.ruleId")
                        .value(ruleId.toString()))
                .andExpect(jsonPath("$.versionNumber")
                        .value(2))
                .andExpect(jsonPath("$.ruleName")
                        .value("Updated Fraud Detection Rule"))
                .andExpect(jsonPath("$.description")
                        .value("Updated rule information"))
                .andExpect(jsonPath("$.category")
                        .value("ATO"))
                .andExpect(jsonPath("$.severity")
                        .value("CRITICAL"))
                .andExpect(jsonPath("$.priority")
                        .value(3))
                .andExpect(jsonPath("$.ownerTeam")
                        .value("FRAUD_GOVERNANCE"))
                .andExpect(jsonPath("$.publicationStatus")
                        .value("DRAFT"))
                .andExpect(jsonPath("$.changeSummary")
                        .value("Controlled rule information update"))
                .andExpect(jsonPath("$.createdBy")
                        .value(changedBy.toString()))
                .andExpect(jsonPath("$.createdAt")
                        .exists());

        entityManager.flush();

        Map<String, Object> persistedRule =
                jdbcTemplate.queryForMap(
                        """
                        SELECT
                            rule_code,
                            rule_name,
                            description,
                            category,
                            severity,
                            priority,
                            owner_team,
                            current_version,
                            status
                        FROM rules.rule
                        WHERE rule_id = ?
                        """,
                        ruleId
                );

        assertEquals(
                "RULE-UPDATE-API-001",
                persistedRule.get("rule_code")
        );

        assertEquals(
                "Rule Controller Integration Test",
                persistedRule.get("rule_name")
        );

        assertEquals(
                "Rule controller integration test",
                persistedRule.get("description")
        );

        assertEquals(
                "TRANSACTION",
                persistedRule.get("category")
        );

        assertEquals(
                "HIGH",
                persistedRule.get("severity")
        );

        assertEquals(
                2,
                ((Number) persistedRule
                        .get("priority"))
                        .intValue()
        );

        assertEquals(
                "FRAUD_RULES",
                persistedRule.get("owner_team")
        );

        assertEquals(
                1,
                ((Number) persistedRule
                        .get("current_version"))
                        .intValue()
        );

        assertEquals(
                "ACTIVE",
                persistedRule.get("status")
        );

        Map<String, Object> draftVersion =
                jdbcTemplate.queryForMap(
                        """
                        SELECT
                            version_number,
                            rule_name,
                            description,
                            category,
                            severity,
                            priority,
                            owner_team,
                            publication_status,
                            change_summary,
                            created_by
                        FROM rules.rule_version
                        WHERE rule_id = ?
                          AND version_number = 2
                        """,
                        ruleId
                );

        assertEquals(
                2,
                ((Number) draftVersion
                        .get("version_number"))
                        .intValue()
        );

        assertEquals(
                "Updated Fraud Detection Rule",
                draftVersion.get("rule_name")
        );

        assertEquals(
                "Updated rule information",
                draftVersion.get("description")
        );

        assertEquals(
                "ATO",
                draftVersion.get("category")
        );

        assertEquals(
                "CRITICAL",
                draftVersion.get("severity")
        );

        assertEquals(
                3,
                ((Number) draftVersion
                        .get("priority"))
                        .intValue()
        );

        assertEquals(
                "FRAUD_GOVERNANCE",
                draftVersion.get("owner_team")
        );

        assertEquals(
                "DRAFT",
                draftVersion.get("publication_status")
        );

        assertEquals(
                "Controlled rule information update",
                draftVersion.get("change_summary")
        );

        assertEquals(
                changedBy,
                draftVersion.get("created_by")
        );

        Integer publishedVersionCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_version
                        WHERE rule_id = ?
                          AND version_number = 1
                          AND publication_status = 'PUBLISHED'
                          AND rule_name = 'Rule Controller Integration Test'
                          AND category = 'TRANSACTION'
                          AND severity = 'HIGH'
                          AND priority = 2
                          AND owner_team = 'FRAUD_RULES'
                        """,
                        Integer.class,
                        ruleId
                );

        assertEquals(
                1,
                publishedVersionCount
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_type = 'RULE'
                          AND entity_id = ?
                          AND operation_type = 'UPDATE'
                          AND changed_by = ?
                          AND correlation_id = ?
                          AND change_reason = ?
                          AND previous_value
                              ->> 'ruleName'
                              = 'Rule Controller Integration Test'
                          AND current_value
                              ->> 'ruleName'
                              = 'Updated Fraud Detection Rule'
                        """,
                        Integer.class,
                        ruleId,
                        changedBy,
                        correlationId,
                        "Controlled rule information update"
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
                          AND event_type = 'RULE_UPDATED'
                          AND action = 'UPDATE'
                          AND source_component = 'RULE_ENGINE'
                          AND event_result = 'SUCCESS'
                          AND user_id = ?
                          AND correlation_id = ?
                          AND event_details
                              ->> 'ruleId' = ?
                          AND event_details
                              ->> 'versionNumber' = '2'
                          AND event_details
                              ->> 'publicationStatus' = 'DRAFT'
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
                          AND event_record.event_type = 'RULE_UPDATED'
                          AND change_record.previous_value
                              ->> 'ruleName'
                              = 'Rule Controller Integration Test'
                          AND change_record.current_value
                              ->> 'ruleName'
                              = 'Updated Fraud Detection Rule'
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
    void shouldReturnNotFoundWhenUpdatingUnknownRuleThroughApi()
            throws Exception {

        UUID unknownRuleId =
                UUID.randomUUID();

        String requestBody =
                """
                {
                    "ruleName": "Updated Rule",
                    "changedBy": "%s"
                }
                """.formatted(
                        UUID.randomUUID()
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/rules/{ruleId}",
                                unknownRuleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectRuleUpdateWithoutActorThroughApi()
            throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-UPDATE-API-002",
                        "TRANSACTION",
                        "HIGH",
                        (short) 2,
                        "ACTIVE"
                );

        String requestBody =
                """
                {
                    "ruleName": "Updated Rule Without Actor"
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/v1/rules/{ruleId}",
                                ruleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectRuleUpdateWithoutMutableFieldsThroughApi()
            throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-UPDATE-API-003",
                        "TRANSACTION",
                        "HIGH",
                        (short) 2,
                        "ACTIVE"
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
                        patch(
                                "/api/v1/rules/{ruleId}",
                                ruleId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("VALIDATION_ERROR"));
    }

    private UUID insertRule(
            String ruleCode,
            String category,
            String severity,
            short priority,
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                ruleId,
                ruleCode,
                "Rule Controller Integration Test",
                "Rule controller integration test",
                category,
                severity,
                priority,
                "FRAUD_RULES",
                1,
                status
        );

        return ruleId;
    }

    private UUID insertRuleVersion(
            UUID ruleId,
            UUID createdBy,
            int versionNumber,
            String publicationStatus,
            String ruleName,
            String description,
            String category,
            String severity,
            short priority,
            String ownerTeam) {

        UUID ruleVersionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_version (
                    rule_version_id,
                    rule_id,
                    version_number,
                    rule_name,
                    description,
                    category,
                    severity,
                    priority,
                    owner_team,
                    effective_from,
                    effective_to,
                    publication_status,
                    change_summary,
                    created_by,
                    approved_by,
                    created_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    NULL,
                    ?, ?, ?, NULL,
                    CURRENT_TIMESTAMP
                )
                """,
                ruleVersionId,
                ruleId,
                versionNumber,
                ruleName,
                description,
                category,
                severity,
                priority,
                ownerTeam,
                publicationStatus,
                "Initial published rule version",
                createdBy
        );

        return ruleVersionId;
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
                "RULE-UPD-" + suffix,
                "EFS Rule Update Test Organization",
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
                "efs.rule.update." + suffix,
                "EFS Rule Update Test User",
                "efs.rule.update."
                        + suffix
                        + "@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}