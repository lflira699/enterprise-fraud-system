package com.efs.modules.casemanagement.service;

import com.efs.modules.casemanagement.repository.CaseRepository;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class CaseReviewFailureAuditIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "e034e034-e034-e034-e034-e034e034e034"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "f034f034-f034-f034-f034-f034f034f034"
            );

    @Autowired
    private CaseServiceInterface caseService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private CaseRepository caseRepository;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldPersistFailureAuditWhenCaseRetrievalFails() {

        UUID caseId =
                UUID.randomUUID();

        String failureMessage =
                "UC034 forced case review failure";

        SecurityContext securityContext =
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of("case.view"),
                        Set.of()
                );

        when(
                caseRepository.findByCaseId(
                        caseId
                )
        ).thenThrow(
                new IllegalStateException(
                        failureMessage
                )
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> caseService.getCaseById(
                                caseId,
                                securityContext
                        )
                );

        assertEquals(
                failureMessage,
                exception.getMessage()
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_REVIEW'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'REVIEW'
                          AND source_component = 'CASE'
                          AND event_result = 'FAILURE'
                          AND event_details ->> 'reason' =
                              'CASE_REVIEW_FAILED'
                          AND event_details ->> 'permissionCode' =
                              'case.view'
                          AND event_details ->> 'errorType' =
                              'java.lang.IllegalStateException'
                          AND event_details ->> 'errorMessage' = ?
                        """,
                        Integer.class,
                        USER_ID,
                        caseId,
                        failureMessage
                );

        assertEquals(
                Integer.valueOf(1),
                auditCount
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
                ON CONFLICT DO NOTHING
                """,
                ORGANIZATION_ID,
                "EFS-CASE-REVIEW-FAILURE-TEST-ORG",
                "EFS Case Review Failure Test Organization",
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
                ON CONFLICT DO NOTHING
                """,
                USER_ID,
                ORGANIZATION_ID,
                "efs.case.review.failure.test",
                "EFS Case Review Failure Test User",
                "efs.case.review.failure.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}