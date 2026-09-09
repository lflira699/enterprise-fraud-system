package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.modules.risk.entity.RiskAssessment;
import com.efs.modules.risk.mapper.RiskAssessmentMapper;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentServiceAuthorizationTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "41414141-4141-4141-4141-414141414141"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "42424242-4242-4242-4242-424242424242"
            );

    private static final UUID SESSION_ID =
            UUID.fromString(
                    "43434343-4343-4343-4343-434343434343"
            );

    @Mock
    private RiskAssessmentRepository
            riskAssessmentRepository;

    @Mock
    private RiskAssessmentMapper
            riskAssessmentMapper;

    @Mock
    private TransactionRepository
            transactionRepository;

    @Mock
    private RiskScoringModelResolver
            riskScoringModelResolver;

    @Mock
    private RiskCalculator
            riskCalculator;

    @Mock
    private DomainEventOutboxService
            domainEventOutboxService;

    @Mock
    private AuditEventServiceInterface
            auditEventService;

    @InjectMocks
    private RiskAssessmentService
            service;

    @Test
    void shouldAuditSuccessfulRiskAssessmentReviewById() {

        UUID riskAssessmentId =
                UUID.randomUUID();

        RiskAssessment assessment =
                mock(
                        RiskAssessment.class
                );

        RiskAssessmentResponse response =
                mock(
                        RiskAssessmentResponse.class
                );

        when(
                riskAssessmentRepository
                        .findById(
                                riskAssessmentId
                        )
        ).thenReturn(
                Optional.of(
                        assessment
                )
        );

        when(
                riskAssessmentMapper
                        .toResponse(
                                assessment
                        )
        ).thenReturn(
                response
        );

        RiskAssessmentResponse result =
                service.getRiskAssessmentById(
                        riskAssessmentId,
                        authorizedContext()
                );

        assertSame(
                response,
                result
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertEquals(
                USER_ID,
                audit.getUserId()
        );

        assertEquals(
                TENANT_ID,
                audit.getTenantId()
        );

        assertEquals(
                SESSION_ID,
                audit.getSessionId()
        );

        assertEquals(
                "RISK_SCORE_REVIEWED",
                audit.getEventType()
        );

        assertEquals(
                "RISK_ASSESSMENT",
                audit.getEntityType()
        );

        assertEquals(
                riskAssessmentId,
                audit.getEntityId()
        );

        assertEquals(
                "VIEW",
                audit.getAction()
        );

        assertEquals(
                "RISK_ENGINE",
                audit.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                "risk.assessment.view",
                audit.getEventDetails()
                        .get(
                                "permissionCode"
                        )
        );

        assertEquals(
                riskAssessmentId.toString(),
                audit.getEventDetails()
                        .get(
                                "riskAssessmentId"
                        )
        );

        assertEquals(
                1,
                audit.getEventDetails()
                        .get(
                                "resultCount"
                        )
        );
    }

    @Test
    void shouldRejectReviewWithoutPermissionAndAuditRejection() {

        UUID riskAssessmentId =
                UUID.randomUUID();

        AccessDeniedException exception =
                assertThrows(
                        AccessDeniedException.class,
                        () ->
                                service.getRiskAssessmentById(
                                        riskAssessmentId,
                                        deniedContext()
                                )
                );

        assertEquals(
                "Missing required permission: risk.assessment.view",
                exception.getMessage()
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertEquals(
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                "MISSING_PERMISSION",
                audit.getEventDetails()
                        .get(
                                "reason"
                        )
        );

        assertEquals(
                "risk.assessment.view",
                audit.getEventDetails()
                        .get(
                                "permissionCode"
                        )
        );

        verifyNoInteractions(
                riskAssessmentRepository
        );
    }

    @Test
    void shouldAuditUnknownRiskAssessmentAsRejected() {

        UUID riskAssessmentId =
                UUID.randomUUID();

        when(
                riskAssessmentRepository
                        .findById(
                                riskAssessmentId
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.getRiskAssessmentById(
                                riskAssessmentId,
                                authorizedContext()
                        )
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertEquals(
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                "RISK_ASSESSMENT_NOT_FOUND",
                audit.getEventDetails()
                        .get(
                                "reason"
                        )
        );

        assertEquals(
                riskAssessmentId,
                audit.getEntityId()
        );
    }

    @Test
    void shouldAuditRiskAssessmentRetrievalFailure() {

        UUID riskAssessmentId =
                UUID.randomUUID();

        RuntimeException repositoryFailure =
                new RuntimeException(
                        "repository unavailable"
                );

        when(
                riskAssessmentRepository
                        .findById(
                                riskAssessmentId
                        )
        ).thenThrow(
                repositoryFailure
        );

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                service.getRiskAssessmentById(
                                        riskAssessmentId,
                                        authorizedContext()
                                )
                );

        assertSame(
                repositoryFailure,
                thrown
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertEquals(
                "FAILURE",
                audit.getEventResult()
        );

        assertEquals(
                "RISK_ASSESSMENT_RETRIEVAL_FAILED",
                audit.getEventDetails()
                        .get(
                                "reason"
                        )
        );

        assertEquals(
                "RuntimeException",
                audit.getEventDetails()
                        .get(
                                "errorType"
                        )
        );

        assertEquals(
                "repository unavailable",
                audit.getEventDetails()
                        .get(
                                "errorMessage"
                        )
        );
    }

    @Test
    void shouldAuditMultipleTransactionAssessmentsOnce() {

        UUID transactionId =
                UUID.randomUUID();

        RiskAssessment assessmentOne =
                mock(
                        RiskAssessment.class
                );

        RiskAssessment assessmentTwo =
                mock(
                        RiskAssessment.class
                );

        RiskAssessmentResponse responseOne =
                mock(
                        RiskAssessmentResponse.class
                );

        RiskAssessmentResponse responseTwo =
                mock(
                        RiskAssessmentResponse.class
                );

        when(
                riskAssessmentRepository
                        .findByTransactionIdOrderByAssessmentTimestampDesc(
                                transactionId
                        )
        ).thenReturn(
                List.of(
                        assessmentOne,
                        assessmentTwo
                )
        );

        when(
                riskAssessmentMapper
                        .toResponse(
                                assessmentOne
                        )
        ).thenReturn(
                responseOne
        );

        when(
                riskAssessmentMapper
                        .toResponse(
                                assessmentTwo
                        )
        ).thenReturn(
                responseTwo
        );

        List<RiskAssessmentResponse> result =
                service.getAssessmentsByTransaction(
                        transactionId,
                        authorizedContext()
                );

        assertEquals(
                2,
                result.size()
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService,
                times(1)
        ).createAuditEvent(
                captor.capture()
        );

        AuditEventRequest audit =
                captor.getValue();

        assertNull(
                audit.getEntityId()
        );

        assertEquals(
                USER_ID,
                audit.getUserId()
        );

        assertEquals(
                TENANT_ID,
                audit.getTenantId()
        );

        assertEquals(
                SESSION_ID,
                audit.getSessionId()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                transactionId.toString(),
                audit.getEventDetails()
                        .get(
                                "transactionId"
                        )
        );

        assertEquals(
                2,
                audit.getEventDetails()
                        .get(
                                "resultCount"
                        )
        );
    }

    @Test
    void shouldProtectEveryRiskAssessmentReadOperation() {

        UUID transactionId =
                UUID.randomUUID();

        SecurityContext denied =
                deniedContext();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getAssessmentsByTransaction(
                                transactionId,
                                denied
                        )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getLatestAssessmentByTransaction(
                                transactionId,
                                denied
                        )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getAssessmentsByTransactionAndType(
                                transactionId,
                                "TRANSACTION",
                                denied
                        )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.searchAssessments(
                                "LOW",
                                "PASS",
                                0,
                                25,
                                "assessmentTimestamp",
                                "DESC",
                                denied
                        )
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService,
                times(4)
        ).createAuditEvent(
                captor.capture()
        );

        for (
                AuditEventRequest audit :
                captor.getAllValues()
        ) {

            assertEquals(
                    "REJECTED",
                    audit.getEventResult()
            );

            assertEquals(
                    "MISSING_PERMISSION",
                    audit.getEventDetails()
                            .get(
                                    "reason"
                            )
            );
        }

        verifyNoInteractions(
                riskAssessmentRepository
        );
    }

    private SecurityContext authorizedContext() {

        return new SecurityContext(
                USER_ID,
                TENANT_ID,
                SESSION_ID,
                Set.of(),
                Set.of(
                        "risk.assessment.view"
                ),
                Set.of()
        );
    }

    private SecurityContext deniedContext() {

        return new SecurityContext(
                USER_ID,
                TENANT_ID,
                SESSION_ID,
                Set.of(),
                Set.of(),
                Set.of()
        );
    }
}