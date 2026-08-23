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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RulePolicyControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "15151515-1515-1515-1515-151515151515"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "16161616-1616-1616-1616-161616161616"
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
                "EFS-RULE-POLICY-API-ORG",
                "EFS Rule Policy API Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id,
                    organization_id,
                    tenant_code,
                    tenant_name,
                    status,
                    environment
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                TENANT_ID,
                ORGANIZATION_ID,
                "EFS-RULE-POLICY-API-TENANT",
                "EFS Rule Policy API Tenant",
                "ACTIVE",
                "DEVELOPMENT"
        );
    }

    @Test
    void shouldCreateRulePolicyThroughApi() throws Exception {

        String requestBody =
                """
                {
                    "policyCode": "POLICY-API-001",
                    "policyName": "Transaction Risk Policy",
                    "description": "Transaction fraud policy",
                    "policyType": "TRANSACTION",
                    "organizationId": "%s",
                    "tenantId": "%s",
                    "status": "ACTIVE",
                    "priority": 1
                }
                """.formatted(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        mockMvc.perform(
                        post("/api/v1/rules/policies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyId").exists())
                .andExpect(jsonPath("$.policyCode").value(
                        "POLICY-API-001"
                ))
                .andExpect(jsonPath("$.policyName").value(
                        "Transaction Risk Policy"
                ))
                .andExpect(jsonPath("$.policyType").value(
                        "TRANSACTION"
                ))
                .andExpect(jsonPath("$.organizationId").value(
                        ORGANIZATION_ID.toString()
                ))
                .andExpect(jsonPath("$.tenantId").value(
                        TENANT_ID.toString()
                ))
                .andExpect(jsonPath("$.status").value(
                        "ACTIVE"
                ))
                .andExpect(jsonPath("$.priority").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldRetrieveRulePolicyByIdThroughApi()
            throws Exception {

        UUID policyId =
                insertRulePolicy(
                        "POLICY-API-002",
                        "ATO Policy",
                        "ATO",
                        "ACTIVE",
                        (short) 1
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/policies/{policyId}",
                                policyId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyId").value(
                        policyId.toString()
                ))
                .andExpect(jsonPath("$.policyCode").value(
                        "POLICY-API-002"
                ))
                .andExpect(jsonPath("$.policyType").value(
                        "ATO"
                ));
    }

    @Test
    void shouldRetrieveRulePolicyByCodeThroughApi()
            throws Exception {

        UUID policyId =
                insertRulePolicy(
                        "POLICY-API-003",
                        "Device Risk Policy",
                        "DEVICE",
                        "ACTIVE",
                        (short) 2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/policies/code/{policyCode}",
                                "POLICY-API-003"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyId").value(
                        policyId.toString()
                ))
                .andExpect(jsonPath("$.policyCode").value(
                        "POLICY-API-003"
                ));
    }

    @Test
    void shouldRetrievePoliciesByStatusThroughApi()
            throws Exception {

        insertRulePolicy(
                "POLICY-API-004",
                "Third Active Policy",
                "TRANSACTION",
                "ACTIVE",
                (short) 3
        );

        insertRulePolicy(
                "POLICY-API-005",
                "First Active Policy",
                "ATO",
                "ACTIVE",
                (short) 1
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/policies/status/{status}",
                                "ACTIVE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value(1))
                .andExpect(jsonPath("$[0].policyCode").value(
                        "POLICY-API-005"
                ))
                .andExpect(jsonPath("$[1].priority").value(3))
                .andExpect(jsonPath("$[1].policyCode").value(
                        "POLICY-API-004"
                ));
    }

    @Test
    void shouldRetrievePoliciesByOrganizationThroughApi()
            throws Exception {

        insertRulePolicy(
                "POLICY-API-006",
                "Organization Policy",
                "TRANSACTION",
                "ACTIVE",
                (short) 1
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/policies/organization/{organizationId}",
                                ORGANIZATION_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].organizationId").value(
                        ORGANIZATION_ID.toString()
                ))
                .andExpect(jsonPath("$[0].policyCode").value(
                        "POLICY-API-006"
                ));
    }

    @Test
    void shouldRetrievePoliciesByTenantThroughApi()
            throws Exception {

        insertRulePolicy(
                "POLICY-API-007",
                "Tenant Policy",
                "ATO",
                "ACTIVE",
                (short) 1
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/policies/tenant/{tenantId}",
                                TENANT_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value(
                        TENANT_ID.toString()
                ))
                .andExpect(jsonPath("$[0].policyCode").value(
                        "POLICY-API-007"
                ));
    }

    @Test
    void shouldRetrievePoliciesByTypeThroughApi()
            throws Exception {

        insertRulePolicy(
                "POLICY-API-008",
                "Second Transaction Policy",
                "TRANSACTION",
                "ACTIVE",
                (short) 2
        );

        insertRulePolicy(
                "POLICY-API-009",
                "First Transaction Policy",
                "TRANSACTION",
                "ACTIVE",
                (short) 1
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/policies/type/{policyType}",
                                "TRANSACTION"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value(1))
                .andExpect(jsonPath("$[0].policyCode").value(
                        "POLICY-API-009"
                ))
                .andExpect(jsonPath("$[1].priority").value(2))
                .andExpect(jsonPath("$[1].policyCode").value(
                        "POLICY-API-008"
                ));
    }

    private UUID insertRulePolicy(
            String policyCode,
            String policyName,
            String policyType,
            String status,
            short priority) {

        UUID policyId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_policy (
                    policy_id,
                    policy_code,
                    policy_name,
                    description,
                    policy_type,
                    organization_id,
                    tenant_id,
                    status,
                    effective_from,
                    effective_to,
                    priority,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    NULL,
                    ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                policyId,
                policyCode,
                policyName,
                "Rule policy API integration test",
                policyType,
                ORGANIZATION_ID,
                TENANT_ID,
                status,
                priority
        );

        return policyId;
    }
}