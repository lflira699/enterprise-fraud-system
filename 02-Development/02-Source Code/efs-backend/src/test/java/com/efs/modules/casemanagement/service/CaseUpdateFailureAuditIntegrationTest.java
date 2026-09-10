package com.efs.modules.casemanagement.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.casemanagement.dto.CaseUpdateRequest;
import com.efs.modules.casemanagement.entity.Case;
import com.efs.modules.casemanagement.repository.CaseRepository;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class CaseUpdateFailureAuditIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "e035e035-e035-e035-e035-e035e035e035"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "f035f035-f035-f035-f035-f035f035f035"
            );

    @Autowired
    private CaseServiceInterface caseService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private CaseRepository caseRepository;

    @MockitoBean
    private AuditEntityChangeServiceInterface
            auditEntityChangeService;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldRollbackSuccessAuditAndPersistFailureAudit() {

        UUID caseId =
                UUID.randomUUID();

        Case caseEntity =
                new Case();

        caseEntity.setCaseId(
                caseId
        );

        caseEntity.setCaseNumber(
                "UC035-FAILURE-MOCK"
        );

        caseEntity.setOrganizationId(
                ORGANIZATION_ID
        );

        caseEntity.setCaseType(
                "FRAUD_INVESTIGATION"
        );

        caseEntity.setCategory(
                "TRANSACTION"
        );

        caseEntity.setSeverity(
                "MEDIUM"
        );

        caseEntity.setPriority(
                "NORMAL"
        );

        caseEntity.setCurrentStatus(
                "OPEN"
        );

        caseEntity.setCreatedAt(
                LocalDateTime.now()
        );

        caseEntity.setUpdatedAt(
                LocalDateTime.now()
        );

        when(
                caseRepository.findByCaseId(
                        caseId
                )
        ).thenReturn(
                Optional.of(
                        caseEntity
                )
        );

        when(
                caseRepository.saveAndFlush(
                        any(Case.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        String failureMessage =
                "UC035 forced audit entity change failure";

        doThrow(
                new IllegalStateException(
                        failureMessage
                )
        ).when(
                auditEntityChangeService
        ).createAuditEntityChange(
                any(
                        AuditEntityChangeRequest.class
                )
        );

        CaseUpdateRequest request =
                new CaseUpdateRequest();

        request.setPriority(
                "HIGH"
        );

        SecurityContext securityContext =
                new SecurityContext(
                        USER_ID,
                        null,
                        null,
                        Set.of(),
                        Set.of("case.update"),
                        Set.of()
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> caseService.updateCase(
                                caseId,
                                request,
                                securityContext
                        )
                );

        assertEquals(
                failureMessage,
                exception.getMessage()
        );

        Integer successAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE entity_type = 'CASE'
                          AND entity_id = ?
                          AND event_type = 'CASE_UPDATE'
                          AND event_result = 'SUCCESS'
                        """,
                        Integer.class,
                        caseId
                );

        assertEquals(
                Integer.valueOf(0),
                successAuditCount
        );

        Integer failureAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE user_id = ?
                          AND event_type = 'CASE_UPDATE'
                          AND entity_type = 'CASE'
                          AND entity_id = ?
                          AND action = 'UPDATE'
                          AND source_component = 'CASE'
                          AND event_result = 'FAILURE'
                          AND event_details ->> 'reason' =
                              'CASE_UPDATE_FAILED'
                          AND event_details ->> 'permissionCode' =
                              'case.update'
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
                failureAuditCount
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
                "EFS-UC035-FAILURE",
                "EFS UC035 Failure Test Organization",
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
                "efs.uc035.failure",
                "EFS UC035 Failure Test User",
                "efs.uc035.failure@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}
