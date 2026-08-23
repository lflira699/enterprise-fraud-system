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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleMetricControllerIntegrationTest {

    private static final UUID RULE_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID RULE_VERSION_ID =
            UUID.fromString(
                    "34343434-3434-3434-3434-343434343434"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "35353535-3535-3535-3535-353535353535"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "36363636-3636-3636-3636-363636363636"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
        insertRule();
        insertRuleVersion();
    }

    @Test
    void shouldCreateRuleMetricThroughApi()
            throws Exception {

        String requestBody =
                """
                {
                    "ruleId": "%s",
                    "ruleVersionId": "%s",
                    "metricDate": "2026-08-22",
                    "executionCount": 100,
                    "matchCount": 25,
                    "confirmedFraudCount": 10,
                    "falsePositiveCount": 5,
                    "falseNegativeCount": 2,
                    "averageExecutionMs": 12.50,
                    "preventedAmount": 15000.75,
                    "currencyCode": "GTQ"
                }
                """.formatted(
                        RULE_ID,
                        RULE_VERSION_ID
                );

        mockMvc.perform(
                        post("/api/v1/rules/metrics")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metricId").exists())
                .andExpect(jsonPath("$.ruleId").value(
                        RULE_ID.toString()
                ))
                .andExpect(jsonPath("$.ruleVersionId").value(
                        RULE_VERSION_ID.toString()
                ))
                .andExpect(jsonPath("$.metricDate").value(
                        "2026-08-22"
                ))
                .andExpect(jsonPath("$.executionCount").value(100))
                .andExpect(jsonPath("$.matchCount").value(25))
                .andExpect(jsonPath("$.confirmedFraudCount").value(10))
                .andExpect(jsonPath("$.falsePositiveCount").value(5))
                .andExpect(jsonPath("$.falseNegativeCount").value(2))
                .andExpect(jsonPath("$.averageExecutionMs").value(12.50))
                .andExpect(jsonPath("$.preventedAmount").value(15000.75))
                .andExpect(jsonPath("$.currencyCode").value("GTQ"))
                .andExpect(jsonPath("$.calculatedAt").exists());
    }

    @Test
    void shouldRetrieveRuleMetricByIdThroughApi()
            throws Exception {

        UUID metricId =
                insertRuleMetric(
                        LocalDate.of(2026, 8, 22),
                        100L,
                        25L,
                        "GTQ"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/metrics/{metricId}",
                                metricId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricId").value(
                        metricId.toString()
                ))
                .andExpect(jsonPath("$.ruleId").value(
                        RULE_ID.toString()
                ))
                .andExpect(jsonPath("$.ruleVersionId").value(
                        RULE_VERSION_ID.toString()
                ))
                .andExpect(jsonPath("$.currencyCode").value(
                        "GTQ"
                ));
    }

    @Test
    void shouldRetrieveMetricsByRuleThroughApi()
            throws Exception {

        insertRuleMetric(
                LocalDate.of(2026, 8, 20),
                50L,
                10L,
                "GTQ"
        );

        insertRuleMetric(
                LocalDate.of(2026, 8, 22),
                100L,
                25L,
                "GTQ"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/metrics/rule/{ruleId}",
                                RULE_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].metricDate").value(
                        "2026-08-22"
                ))
                .andExpect(jsonPath("$[1].metricDate").value(
                        "2026-08-20"
                ));
    }

    @Test
    void shouldRetrieveMetricsByRuleVersionThroughApi()
            throws Exception {

        insertRuleMetric(
                LocalDate.of(2026, 8, 22),
                80L,
                20L,
                "USD"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/metrics/version/{ruleVersionId}",
                                RULE_VERSION_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ruleVersionId").value(
                        RULE_VERSION_ID.toString()
                ))
                .andExpect(jsonPath("$[0].currencyCode").value(
                        "USD"
                ));
    }

    @Test
    void shouldRetrieveMetricsByDateThroughApi()
            throws Exception {

        insertRuleMetric(
                LocalDate.of(2026, 8, 22),
                120L,
                30L,
                "GTQ"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/metrics/date/{metricDate}",
                                "2026-08-22"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].metricDate").value(
                        "2026-08-22"
                ))
                .andExpect(jsonPath("$[0].ruleId").value(
                        RULE_ID.toString()
                ));
    }

    private UUID insertRuleMetric(
            LocalDate metricDate,
            long executionCount,
            long matchCount,
            String currencyCode) {

        UUID metricId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_metric (
                    metric_id,
                    rule_id,
                    rule_version_id,
                    metric_date,
                    execution_count,
                    match_count,
                    confirmed_fraud_count,
                    false_positive_count,
                    false_negative_count,
                    average_execution_ms,
                    prevented_amount,
                    currency_code,
                    calculated_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP
                )
                """,
                metricId,
                RULE_ID,
                RULE_VERSION_ID,
                metricDate,
                executionCount,
                matchCount,
                5L,
                2L,
                1L,
                new BigDecimal("10.50"),
                new BigDecimal("5000.00"),
                currencyCode
        );

        return metricId;
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
                "EFS-RULE-METRIC-API-ORG",
                "EFS Rule Metric API Organization",
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
                USER_ID,
                ORGANIZATION_ID,
                "efs.rule.metric.api",
                "EFS Rule Metric API User",
                "efs.rule.metric.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertRule() {

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
                RULE_ID,
                "RULE-METRIC-API-001",
                "Rule Metric API Test",
                "Rule used by V57 REST integration tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "ACTIVE"
        );
    }

    private void insertRuleVersion() {

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
                VALUES (
                    ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    NULL,
                    ?,
                    ?,
                    ?,
                    NULL,
                    CURRENT_TIMESTAMP
                )
                """,
                RULE_VERSION_ID,
                RULE_ID,
                1,
                "PUBLISHED",
                "Rule metric API integration version",
                USER_ID
        );
    }
}