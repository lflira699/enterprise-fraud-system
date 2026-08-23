package com.efs.modules.rules.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleVersionControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    @Autowired
    private MockMvc mockMvc;

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
                "EFS-RULE-VERSION-API-ORG",
                "EFS Rule Version API Organization",
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
                CREATED_BY,
                ORGANIZATION_ID,
                "efs.rule.version.api",
                "EFS Rule Version API User",
                "efs.rule.version.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateRuleVersionThroughApi() throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-VERSION-API-001"
                );

        String requestBody =
                """
                {
                    "versionNumber": 1,
                    "effectiveFrom": "%s",
                    "effectiveTo": null,
                    "publicationStatus": "DRAFT",
                    "changeSummary": "Initial rule version",
                    "createdBy": "%s",
                    "approvedBy": null
                }
                """.formatted(
                        LocalDateTime.now(),
                        CREATED_BY
                );

        mockMvc.perform(
                        post("/api/v1/rules/{ruleId}/versions",
                                ruleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ruleVersionId").exists())
                .andExpect(jsonPath("$.ruleId").value(
                        ruleId.toString()
                ))
                .andExpect(jsonPath("$.versionNumber").value(1))
                .andExpect(jsonPath("$.publicationStatus").value(
                        "DRAFT"
                ))
                .andExpect(jsonPath("$.changeSummary").value(
                        "Initial rule version"
                ))
                .andExpect(jsonPath("$.createdBy").value(
                        CREATED_BY.toString()
                ))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRetrieveRuleVersionByIdThroughApi()
            throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-VERSION-API-002"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId,
                        1,
                        "DRAFT"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/versions/{ruleVersionId}",
                                ruleVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.ruleId").value(
                        ruleId.toString()
                ))
                .andExpect(jsonPath("$.versionNumber").value(1))
                .andExpect(jsonPath("$.publicationStatus").value(
                        "DRAFT"
                ))
                .andExpect(jsonPath("$.createdBy").value(
                        CREATED_BY.toString()
                ));
    }

    @Test
    void shouldRetrieveRuleVersionsByRuleIdThroughApi()
            throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-VERSION-API-003"
                );

        insertRuleVersion(
                ruleId,
                1,
                "DRAFT"
        );

        insertRuleVersion(
                ruleId,
                2,
                "PUBLISHED"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/{ruleId}/versions",
                                ruleId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].versionNumber").value(2))
                .andExpect(jsonPath("$[0].publicationStatus").value(
                        "PUBLISHED"
                ))
                .andExpect(jsonPath("$[1].versionNumber").value(1))
                .andExpect(jsonPath("$[1].publicationStatus").value(
                        "DRAFT"
                ));
    }

    @Test
    void shouldRetrieveRuleVersionByRuleAndVersionNumberThroughApi()
            throws Exception {

        UUID ruleId =
                insertRule(
                        "RULE-VERSION-API-004"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId,
                        3,
                        "PUBLISHED"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/{ruleId}/versions/{versionNumber}",
                                ruleId,
                                3
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleVersionId").value(
                        ruleVersionId.toString()
                ))
                .andExpect(jsonPath("$.ruleId").value(
                        ruleId.toString()
                ))
                .andExpect(jsonPath("$.versionNumber").value(3))
                .andExpect(jsonPath("$.publicationStatus").value(
                        "PUBLISHED"
                ));
    }

    @Test
    void shouldRetrieveRuleVersionsByPublicationStatusThroughApi()
            throws Exception {

        UUID firstRuleId =
                insertRule(
                        "RULE-VERSION-API-005"
                );

        UUID secondRuleId =
                insertRule(
                        "RULE-VERSION-API-006"
                );

        insertRuleVersion(
                firstRuleId,
                1,
                "PUBLISHED"
        );

        insertRuleVersion(
                secondRuleId,
                1,
                "PUBLISHED"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/versions/status/{publicationStatus}",
                                "PUBLISHED"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicationStatus").value(
                        "PUBLISHED"
                ))
                .andExpect(jsonPath("$[1].publicationStatus").value(
                        "PUBLISHED"
                ));
    }

    private UUID insertRule(
            String ruleCode) {

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
                "Rule Version API Test",
                "Rule used by V50 API integration tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "ACTIVE"
        );

        return ruleId;
    }

    private UUID insertRuleVersion(
            UUID ruleId,
            int versionNumber,
            String publicationStatus) {

        UUID ruleVersionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_version (
                    rule_version_id,
                    rule_id,
                    version_number,
                    effective_from,
                    effective_to,
                    publication_status,
                    change_summary,
                    created_by,
                    approved_by,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                ruleVersionId,
                ruleId,
                versionNumber,
                LocalDateTime.now(),
                null,
                publicationStatus,
                "Initial rule version",
                CREATED_BY,
                null
        );

        return ruleVersionId;
    }
}