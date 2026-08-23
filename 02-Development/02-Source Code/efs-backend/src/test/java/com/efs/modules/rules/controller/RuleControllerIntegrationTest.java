package com.efs.modules.rules.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}