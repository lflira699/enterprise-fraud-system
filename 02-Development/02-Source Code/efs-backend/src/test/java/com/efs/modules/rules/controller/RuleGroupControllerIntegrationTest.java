package com.efs.modules.rules.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleGroupControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateRuleGroupThroughApi() throws Exception {

        String requestBody =
                """
                {
                    "groupCode": "RULE-GROUP-API-001",
                    "groupName": "Transaction Risk Rules",
                    "description": "Transaction fraud detection rules",
                    "category": "TRANSACTION",
                    "status": "ACTIVE",
                    "executionOrder": 1
                }
                """;

        mockMvc.perform(
                        post("/api/v1/rules/groups")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ruleGroupId").exists())
                .andExpect(jsonPath("$.groupCode").value(
                        "RULE-GROUP-API-001"
                ))
                .andExpect(jsonPath("$.groupName").value(
                        "Transaction Risk Rules"
                ))
                .andExpect(jsonPath("$.description").value(
                        "Transaction fraud detection rules"
                ))
                .andExpect(jsonPath("$.category").value(
                        "TRANSACTION"
                ))
                .andExpect(jsonPath("$.status").value(
                        "ACTIVE"
                ))
                .andExpect(jsonPath("$.executionOrder").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldRetrieveRuleGroupByIdThroughApi()
            throws Exception {

        UUID ruleGroupId =
                insertRuleGroup(
                        "RULE-GROUP-API-002",
                        "ATO Rules",
                        "Account takeover rules",
                        "ATO",
                        "ACTIVE",
                        (short) 1
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/groups/{ruleGroupId}",
                                ruleGroupId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleGroupId").value(
                        ruleGroupId.toString()
                ))
                .andExpect(jsonPath("$.groupCode").value(
                        "RULE-GROUP-API-002"
                ))
                .andExpect(jsonPath("$.groupName").value(
                        "ATO Rules"
                ))
                .andExpect(jsonPath("$.category").value(
                        "ATO"
                ))
                .andExpect(jsonPath("$.status").value(
                        "ACTIVE"
                ));
    }

    @Test
    void shouldRetrieveRuleGroupByCodeThroughApi()
            throws Exception {

        UUID ruleGroupId =
                insertRuleGroup(
                        "RULE-GROUP-API-003",
                        "Device Risk Rules",
                        null,
                        "DEVICE",
                        "ACTIVE",
                        (short) 2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/groups/code/{groupCode}",
                                "RULE-GROUP-API-003"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleGroupId").value(
                        ruleGroupId.toString()
                ))
                .andExpect(jsonPath("$.groupCode").value(
                        "RULE-GROUP-API-003"
                ));
    }

    @Test
    void shouldRetrieveRuleGroupsByStatusOrderedByExecutionOrder()
            throws Exception {

        insertRuleGroup(
                "RULE-GROUP-API-004",
                "Third Active Group",
                null,
                "TRANSACTION",
                "ACTIVE",
                (short) 3
        );

        insertRuleGroup(
                "RULE-GROUP-API-005",
                "First Active Group",
                null,
                "ATO",
                "ACTIVE",
                (short) 1
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/groups/status/{status}",
                                "ACTIVE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].executionOrder").value(1))
                .andExpect(jsonPath("$[0].groupCode").value(
                        "RULE-GROUP-API-005"
                ))
                .andExpect(jsonPath("$[1].executionOrder").value(3))
                .andExpect(jsonPath("$[1].groupCode").value(
                        "RULE-GROUP-API-004"
                ));
    }

    @Test
    void shouldRetrieveRuleGroupsByCategoryOrderedByExecutionOrder()
            throws Exception {

        insertRuleGroup(
                "RULE-GROUP-API-006",
                "Second Transaction Group",
                null,
                "TRANSACTION",
                "ACTIVE",
                (short) 2
        );

        insertRuleGroup(
                "RULE-GROUP-API-007",
                "First Transaction Group",
                null,
                "TRANSACTION",
                "ACTIVE",
                (short) 1
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/groups/category/{category}",
                                "TRANSACTION"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].executionOrder").value(1))
                .andExpect(jsonPath("$[0].groupCode").value(
                        "RULE-GROUP-API-007"
                ))
                .andExpect(jsonPath("$[1].executionOrder").value(2))
                .andExpect(jsonPath("$[1].groupCode").value(
                        "RULE-GROUP-API-006"
                ));
    }

    private UUID insertRuleGroup(
            String groupCode,
            String groupName,
            String description,
            String category,
            String status,
            Short executionOrder) {

        UUID ruleGroupId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_group (
                    rule_group_id,
                    group_code,
                    group_name,
                    description,
                    category,
                    status,
                    execution_order,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                ruleGroupId,
                groupCode,
                groupName,
                description,
                category,
                status,
                executionOrder
        );

        return ruleGroupId;
    }
}