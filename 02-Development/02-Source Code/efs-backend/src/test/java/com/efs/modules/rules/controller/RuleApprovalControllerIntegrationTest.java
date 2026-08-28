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
class RuleApprovalControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "47474747-4747-4747-4747-474747474747"
            );

    private static final UUID SUBMITTED_BY =
            UUID.fromString(
                    "48484848-4848-4848-4848-484848484848"
            );

    private static final UUID REVIEWED_BY =
            UUID.fromString(
                    "49494949-4949-4949-4949-494949494949"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "50505050-5050-5050-5050-505050505050"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();

        insertUser(
                SUBMITTED_BY,
                "efs.rule.approval.api.submitter",
                "EFS Rule Approval API Submitter",
                "efs.rule.approval.api.submitter@example.com"
        );

        insertUser(
                REVIEWED_BY,
                "efs.rule.approval.api.reviewer",
                "EFS Rule Approval API Reviewer",
                "efs.rule.approval.api.reviewer@example.com"
        );
    }

    @Test
    void shouldCreateRuleApprovalThroughApi()
            throws Exception {

        String requestBody =
                """
                {
                    "entityType": "RULE",
                    "entityId": "%s",
                    "approvalStatus": "PENDING",
                    "submittedBy": "%s",
                    "reviewedBy": null,
                    "decisionComment": null,
                    "approvalLevel": 1
                }
                """.formatted(
                        ENTITY_ID,
                        SUBMITTED_BY
                );

        mockMvc.perform(
                        post("/api/v1/rules/approvals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.approvalId").exists())
                .andExpect(jsonPath("$.entityType").value(
                        "RULE"
                ))
                .andExpect(jsonPath("$.entityId").value(
                        ENTITY_ID.toString()
                ))
                .andExpect(jsonPath("$.approvalStatus").value(
                        "PENDING"
                ))
                .andExpect(jsonPath("$.submittedBy").value(
                        SUBMITTED_BY.toString()
                ))
                .andExpect(jsonPath("$.reviewedBy").doesNotExist())
                .andExpect(jsonPath("$.decisionComment").doesNotExist())
                .andExpect(jsonPath("$.approvalLevel").value(1))
                .andExpect(jsonPath("$.submittedAt").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRetrieveRuleApprovalByIdThroughApi()
            throws Exception {

        UUID approvalId =
                insertRuleApproval(
                        "RULE",
                        ENTITY_ID,
                        "PENDING",
                        SUBMITTED_BY,
                        null,
                        null,
                        (short) 1
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/approvals/{approvalId}",
                                approvalId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalId").value(
                        approvalId.toString()
                ))
                .andExpect(jsonPath("$.entityType").value(
                        "RULE"
                ))
                .andExpect(jsonPath("$.entityId").value(
                        ENTITY_ID.toString()
                ));
    }

    @Test
    void shouldRetrieveApprovalsByEntityThroughApi()
            throws Exception {

        insertRuleApproval(
                "RULE",
                ENTITY_ID,
                "PENDING",
                SUBMITTED_BY,
                null,
                null,
                (short) 1
        );

        Thread.sleep(5);

        insertRuleApproval(
                "RULE",
                ENTITY_ID,
                "APPROVED",
                SUBMITTED_BY,
                REVIEWED_BY,
                "Approved",
                (short) 2
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/approvals/entity/{entityType}/{entityId}",
                                "RULE",
                                ENTITY_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].approvalStatus").value(
                        "APPROVED"
                ))
                .andExpect(jsonPath("$[1].approvalStatus").value(
                        "PENDING"
                ));
    }

    @Test
    void shouldRetrieveApprovalsByStatusThroughApi()
            throws Exception {

        insertRuleApproval(
                "RULE",
                UUID.randomUUID(),
                "PENDING",
                SUBMITTED_BY,
                null,
                null,
                (short) 1
        );

        Thread.sleep(5);

        insertRuleApproval(
                "POLICY",
                UUID.randomUUID(),
                "PENDING",
                SUBMITTED_BY,
                null,
                null,
                (short) 1
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/approvals/status/{approvalStatus}",
                                "PENDING"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldRetrieveApprovalsBySubmittedByThroughApi()
            throws Exception {

        insertRuleApproval(
                "RULE",
                UUID.randomUUID(),
                "PENDING",
                SUBMITTED_BY,
                null,
                null,
                (short) 1
        );

        Thread.sleep(5);

        insertRuleApproval(
                "POLICY",
                UUID.randomUUID(),
                "APPROVED",
                SUBMITTED_BY,
                REVIEWED_BY,
                "Approved",
                (short) 2
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/approvals/submitted-by/{submittedBy}",
                                SUBMITTED_BY
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].submittedBy").value(
                        SUBMITTED_BY.toString()
                ))
                .andExpect(jsonPath("$[1].submittedBy").value(
                        SUBMITTED_BY.toString()
                ));
    }

    @Test
    void shouldRetrieveApprovalsByReviewedByThroughApi()
            throws Exception {

        insertRuleApproval(
                "RULE",
                UUID.randomUUID(),
                "APPROVED",
                SUBMITTED_BY,
                REVIEWED_BY,
                "Approved first",
                (short) 2
        );

        Thread.sleep(5);

        insertRuleApproval(
                "POLICY",
                UUID.randomUUID(),
                "REJECTED",
                SUBMITTED_BY,
                REVIEWED_BY,
                "Rejected second",
                (short) 2
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/approvals/reviewed-by/{reviewedBy}",
                                REVIEWED_BY
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].reviewedBy").value(
                        REVIEWED_BY.toString()
                ))
                .andExpect(jsonPath("$[1].reviewedBy").value(
                        REVIEWED_BY.toString()
                ));
    }

    private UUID insertRuleApproval(
            String entityType,
            UUID entityId,
            String approvalStatus,
            UUID submittedBy,
            UUID reviewedBy,
            String decisionComment,
            short approvalLevel) {

        UUID approvalId =
                UUID.randomUUID();

        if (reviewedBy == null) {

            jdbcTemplate.update(
                    """
                    INSERT INTO rules.rule_approval (
                        approval_id,
                        entity_type,
                        entity_id,
                        approval_status,
                        submitted_by,
                        submitted_at,
                        reviewed_by,
                        reviewed_at,
                        decision_comment,
                        approval_level,
                        created_at
                    )
                    VALUES (
                        ?, ?, ?, ?, ?,
                        clock_timestamp(),
                        NULL,
                        NULL,
                        ?,
                        ?,
                        clock_timestamp()
                    )
                    """,
                    approvalId,
                    entityType,
                    entityId,
                    approvalStatus,
                    submittedBy,
                    decisionComment,
                    approvalLevel
            );

        } else {

            jdbcTemplate.update(
                    """
                    INSERT INTO rules.rule_approval (
                        approval_id,
                        entity_type,
                        entity_id,
                        approval_status,
                        submitted_by,
                        submitted_at,
                        reviewed_by,
                        reviewed_at,
                        decision_comment,
                        approval_level,
                        created_at
                    )
                    VALUES (
                        ?, ?, ?, ?, ?,
                        clock_timestamp(),
                        ?,
                        clock_timestamp(),
                        ?,
                        ?,
                        clock_timestamp()
                    )
                    """,
                    approvalId,
                    entityType,
                    entityId,
                    approvalStatus,
                    submittedBy,
                    reviewedBy,
                    decisionComment,
                    approvalLevel
            );
        }

        return approvalId;
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
                "EFS-RULE-APPROVAL-API-ORG",
                "EFS Rule Approval API Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser(
            UUID userId,
            String username,
            String fullName,
            String email) {

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
                ORGANIZATION_ID,
                username,
                fullName,
                email,
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}