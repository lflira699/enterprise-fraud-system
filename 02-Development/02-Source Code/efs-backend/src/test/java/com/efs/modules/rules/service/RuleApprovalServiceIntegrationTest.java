package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleApprovalRequest;
import com.efs.modules.rules.dto.RuleApprovalResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleApprovalServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "43434343-4343-4343-4343-434343434343"
            );

    private static final UUID SUBMITTED_BY =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

    private static final UUID REVIEWED_BY =
            UUID.fromString(
                    "45454545-4545-4545-4545-454545454545"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "46464646-4646-4646-4646-464646464646"
            );

    @Autowired
    private RuleApprovalServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();

        insertUser(
                SUBMITTED_BY,
                "efs.rule.approval.submitter",
                "EFS Rule Approval Submitter",
                "efs.rule.approval.submitter@example.com"
        );

        insertUser(
                REVIEWED_BY,
                "efs.rule.approval.reviewer",
                "EFS Rule Approval Reviewer",
                "efs.rule.approval.reviewer@example.com"
        );
    }

    @Test
    void shouldCreateAndRetrieveRuleApprovalById() {

        RuleApprovalResponse created =
                service.createRuleApproval(
                        buildRequest(
                                "RULE",
                                ENTITY_ID,
                                "PENDING",
                                SUBMITTED_BY,
                                null,
                                null,
                                (short) 1
                        )
                );

        assertNotNull(
                created.getApprovalId()
        );

        assertEquals(
                "RULE",
                created.getEntityType()
        );

        assertEquals(
                ENTITY_ID,
                created.getEntityId()
        );

        assertEquals(
                "PENDING",
                created.getApprovalStatus()
        );

        assertEquals(
                SUBMITTED_BY,
                created.getSubmittedBy()
        );

        assertNotNull(
                created.getSubmittedAt()
        );

        assertNull(
                created.getReviewedBy()
        );

        assertNull(
                created.getReviewedAt()
        );

        assertNull(
                created.getDecisionComment()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getApprovalLevel()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        RuleApprovalResponse retrieved =
                service.getRuleApprovalById(
                        created.getApprovalId()
                );

        assertEquals(
                created.getApprovalId(),
                retrieved.getApprovalId()
        );
    }

    @Test
    void shouldCreateApprovalWithReviewerAndDecisionComment() {

        RuleApprovalResponse created =
                service.createRuleApproval(
                        buildRequest(
                                "POLICY",
                                ENTITY_ID,
                                "APPROVED",
                                SUBMITTED_BY,
                                REVIEWED_BY,
                                "Approved during integration test",
                                (short) 2
                        )
                );

        assertNotNull(
                created.getApprovalId()
        );

        assertEquals(
                REVIEWED_BY,
                created.getReviewedBy()
        );

        assertEquals(
                "Approved during integration test",
                created.getDecisionComment()
        );

        assertEquals(
                "APPROVED",
                created.getApprovalStatus()
        );

        assertEquals(
                Short.valueOf((short) 2),
                created.getApprovalLevel()
        );
    }

    @Test
    void shouldReturnApprovalsByEntityOrderedByCreatedAtDescending() {

        RuleApprovalResponse first =
                service.createRuleApproval(
                        buildRequest(
                                "RULE",
                                ENTITY_ID,
                                "PENDING",
                                SUBMITTED_BY,
                                null,
                                null,
                                (short) 1
                        )
                );

        sleepBriefly();

        RuleApprovalResponse second =
                service.createRuleApproval(
                        buildRequest(
                                "RULE",
                                ENTITY_ID,
                                "APPROVED",
                                SUBMITTED_BY,
                                REVIEWED_BY,
                                "Approved",
                                (short) 2
                        )
                );

        List<RuleApprovalResponse> approvals =
                service.getRuleApprovalsByEntity(
                        "RULE",
                        ENTITY_ID
                );

        assertEquals(
                2,
                approvals.size()
        );

        assertEquals(
                second.getApprovalId(),
                approvals.get(0).getApprovalId()
        );

        assertEquals(
                first.getApprovalId(),
                approvals.get(1).getApprovalId()
        );
    }

    @Test
    void shouldReturnApprovalsByStatusOrderedByCreatedAtDescending() {

        RuleApprovalResponse first =
                service.createRuleApproval(
                        buildRequest(
                                "RULE",
                                UUID.randomUUID(),
                                "PENDING",
                                SUBMITTED_BY,
                                null,
                                null,
                                (short) 1
                        )
                );

        sleepBriefly();

        RuleApprovalResponse second =
                service.createRuleApproval(
                        buildRequest(
                                "POLICY",
                                UUID.randomUUID(),
                                "PENDING",
                                SUBMITTED_BY,
                                null,
                                null,
                                (short) 1
                        )
                );

        List<RuleApprovalResponse> approvals =
                service.getRuleApprovalsByStatus(
                        "PENDING"
                );

        assertEquals(
                2,
                approvals.size()
        );

        assertEquals(
                second.getApprovalId(),
                approvals.get(0).getApprovalId()
        );

        assertEquals(
                first.getApprovalId(),
                approvals.get(1).getApprovalId()
        );
    }

    @Test
    void shouldReturnApprovalsBySubmittedByOrderedByCreatedAtDescending() {

        RuleApprovalResponse first =
                service.createRuleApproval(
                        buildRequest(
                                "RULE",
                                UUID.randomUUID(),
                                "PENDING",
                                SUBMITTED_BY,
                                null,
                                null,
                                (short) 1
                        )
                );

        sleepBriefly();

        RuleApprovalResponse second =
                service.createRuleApproval(
                        buildRequest(
                                "POLICY",
                                UUID.randomUUID(),
                                "APPROVED",
                                SUBMITTED_BY,
                                REVIEWED_BY,
                                "Approved",
                                (short) 2
                        )
                );

        List<RuleApprovalResponse> approvals =
                service.getRuleApprovalsBySubmittedBy(
                        SUBMITTED_BY
                );

        assertEquals(
                2,
                approvals.size()
        );

        assertEquals(
                second.getApprovalId(),
                approvals.get(0).getApprovalId()
        );

        assertEquals(
                first.getApprovalId(),
                approvals.get(1).getApprovalId()
        );
    }

    @Test
    void shouldReturnApprovalsByReviewedByOrderedByCreatedAtDescending() {

        RuleApprovalResponse first =
                service.createRuleApproval(
                        buildRequest(
                                "RULE",
                                UUID.randomUUID(),
                                "APPROVED",
                                SUBMITTED_BY,
                                REVIEWED_BY,
                                "Approved first",
                                (short) 2
                        )
                );

        sleepBriefly();

        RuleApprovalResponse second =
                service.createRuleApproval(
                        buildRequest(
                                "POLICY",
                                UUID.randomUUID(),
                                "REJECTED",
                                SUBMITTED_BY,
                                REVIEWED_BY,
                                "Rejected second",
                                (short) 2
                        )
                );

        List<RuleApprovalResponse> approvals =
                service.getRuleApprovalsByReviewedBy(
                        REVIEWED_BY
                );

        assertEquals(
                2,
                approvals.size()
        );

        assertEquals(
                second.getApprovalId(),
                approvals.get(0).getApprovalId()
        );

        assertEquals(
                first.getApprovalId(),
                approvals.get(1).getApprovalId()
        );
    }

    @Test
    void shouldRejectUnknownApprovalId() {

        UUID unknownApprovalId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleApprovalById(
                        unknownApprovalId
                )
        );
    }

    private RuleApprovalRequest buildRequest(
            String entityType,
            UUID entityId,
            String approvalStatus,
            UUID submittedBy,
            UUID reviewedBy,
            String decisionComment,
            short approvalLevel) {

        RuleApprovalRequest request =
                new RuleApprovalRequest();

        request.setEntityType(
                entityType
        );

        request.setEntityId(
                entityId
        );

        request.setApprovalStatus(
                approvalStatus
        );

        request.setSubmittedBy(
                submittedBy
        );

        request.setReviewedBy(
                reviewedBy
        );

        request.setDecisionComment(
                decisionComment
        );

        request.setApprovalLevel(
                approvalLevel
        );

        return request;
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
                "EFS-RULE-APPROVAL-TEST-ORG",
                "EFS Rule Approval Test Organization",
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

    private void sleepBriefly() {

        try {
            Thread.sleep(5);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}