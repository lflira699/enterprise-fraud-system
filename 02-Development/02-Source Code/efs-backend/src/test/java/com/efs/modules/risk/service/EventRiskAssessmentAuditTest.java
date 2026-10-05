package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.risk.entity.EventRiskAssessment;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventRiskAssessmentAuditTest {

    @Mock
    private AuditEventServiceInterface auditEventService;

    private EventRiskAssessmentAuditService auditService;

    private UUID organizationId;
    private UUID tenantId;
    private UUID userId;
    private UUID sessionId;
    private UUID fraudEventId;
    private UUID correlationId;

    @BeforeEach
    void setUp() {

        auditService =
                new EventRiskAssessmentAuditService(
                        auditEventService
                );

        organizationId =
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                );

        tenantId =
                UUID.fromString(
                        "22222222-2222-2222-2222-222222222222"
                );

        userId =
                UUID.fromString(
                        "33333333-3333-3333-3333-333333333333"
                );

        sessionId =
                UUID.fromString(
                        "44444444-4444-4444-4444-444444444444"
                );

        fraudEventId =
                UUID.fromString(
                        "55555555-5555-5555-5555-555555555555"
                );

        correlationId =
                UUID.fromString(
                        "66666666-6666-6666-6666-666666666666"
                );
    }

    @Test
    void successPreservesExecutionContextAndModelTraceability() {

        EventRiskAssessment assessment =
                new EventRiskAssessment(
                        fraudEventId,
                        organizationId,
                        tenantId,
                        correlationId,
                        "EVENT_MODEL",
                        "v1",
                        new BigDecimal("72.50"),
                        "HIGH",
                        null,
                        null,
                        Map.of(
                                "factorScores",
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("72.50")
                                ),
                                "factorWeights",
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("1.00")
                                )
                        ),
                        5L
                );

        SecurityContext context =
                context();

        auditService.recordSuccess(
                assessment,
                context,
                false
        );

        AuditEventRequest request =
                capturedRequest();

        assertEquals(
                organizationId,
                request.getOrganizationId()
        );

        assertEquals(
                tenantId,
                request.getTenantId()
        );

        assertEquals(
                userId,
                request.getUserId()
        );

        assertEquals(
                sessionId,
                request.getSessionId()
        );

        assertEquals(
                correlationId,
                request.getCorrelationId()
        );

        assertEquals(
                "EVENT_RISK_ASSESSED",
                request.getEventType()
        );

        assertEquals(
                "RISK_ASSESSMENT",
                request.getEntityType()
        );

        assertEquals(
                "CALCULATE",
                request.getAction()
        );

        assertEquals(
                "RISK_ENGINE",
                request.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                request.getEventResult()
        );

        assertEquals(
                fraudEventId.toString(),
                request.getEventDetails()
                        .get("fraudEventId")
        );

        assertEquals(
                "EVENT_MODEL",
                request.getEventDetails()
                        .get("modelId")
        );

        assertEquals(
                "v1",
                request.getEventDetails()
                        .get("modelVersion")
        );

        assertEquals(
                false,
                request.getEventDetails()
                        .get("reused")
        );
    }

    @Test
    void reusedSuccessPreservesReusedFlag() {

        EventRiskAssessment assessment =
                new EventRiskAssessment(
                        fraudEventId,
                        organizationId,
                        tenantId,
                        correlationId,
                        "EVENT_MODEL",
                        "v1",
                        new BigDecimal("72.50"),
                        "HIGH",
                        null,
                        null,
                        Map.of(),
                        5L
                );

        auditService.recordSuccess(
                assessment,
                context(),
                true
        );

        AuditEventRequest request =
                capturedRequest();

        assertEquals(
                true,
                request.getEventDetails()
                        .get("reused")
        );
    }

    @Test
    void rejectedPreservesMaximumAvailableContext() {

        auditService.recordRejected(
                organizationId,
                context(),
                fraudEventId,
                correlationId,
                "EVENT_MODEL",
                "v1",
                "EVENT_RISK_FACTOR_UNRESOLVED",
                new IllegalArgumentException(
                        "factor missing"
                )
        );

        AuditEventRequest request =
                capturedRequest();

        assertEquals(
                organizationId,
                request.getOrganizationId()
        );

        assertEquals(
                tenantId,
                request.getTenantId()
        );

        assertEquals(
                userId,
                request.getUserId()
        );

        assertEquals(
                sessionId,
                request.getSessionId()
        );

        assertEquals(
                correlationId,
                request.getCorrelationId()
        );

        assertEquals(
                "REJECTED",
                request.getEventResult()
        );

        assertEquals(
                "EVENT_RISK_FACTOR_UNRESOLVED",
                request.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                "EVENT_MODEL",
                request.getEventDetails()
                        .get("modelId")
        );

        assertEquals(
                "v1",
                request.getEventDetails()
                        .get("modelVersion")
        );
    }

    @Test
    void earlyRejectedAuditDoesNotSynthesizeUnavailableContext() {

        auditService.recordRejected(
                null,
                null,
                fraudEventId,
                null,
                null,
                null,
                "EVENT_RISK_NOT_ELIGIBLE",
                new IllegalStateException(
                        "not eligible"
                )
        );

        AuditEventRequest request =
                capturedRequest();

        assertNull(
                request.getOrganizationId()
        );

        assertNull(
                request.getTenantId()
        );

        assertNull(
                request.getUserId()
        );

        assertNull(
                request.getSessionId()
        );

        assertNull(
                request.getCorrelationId()
        );

        assertEquals(
                "REJECTED",
                request.getEventResult()
        );

        assertEquals(
                fraudEventId.toString(),
                request.getEventDetails()
                        .get("fraudEventId")
        );

        assertEquals(
                "EVENT_RISK_NOT_ELIGIBLE",
                request.getEventDetails()
                        .get("reason")
        );

        assertTrue(
                !request.getEventDetails()
                        .containsKey("modelId")
        );

        assertTrue(
                !request.getEventDetails()
                        .containsKey("modelVersion")
        );
    }

    @Test
    void failureUsesExistingAuditTaxonomy() {

        auditService.recordFailure(
                organizationId,
                context(),
                fraudEventId,
                correlationId,
                "EVENT_MODEL",
                "v1",
                "EVENT_RISK_ASSESSMENT_FAILED",
                new IllegalStateException(
                        "failed"
                )
        );

        AuditEventRequest request =
                capturedRequest();

        assertEquals(
                "FAILURE",
                request.getEventResult()
        );

        assertEquals(
                "EVENT_RISK_ASSESSMENT_FAILED",
                request.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                "IllegalStateException",
                request.getEventDetails()
                        .get("exceptionType")
        );
    }

    private SecurityContext context() {

        return new SecurityContext(
                userId,
                tenantId,
                sessionId,
                Set.of(),
                Set.of(
                        RiskAssessmentServiceInterface
                                .RISK_ASSESSMENT_VIEW_PERMISSION
                ),
                Set.of()
        );
    }

    private AuditEventRequest capturedRequest() {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                captor.capture()
        );

        return captor.getValue();
    }
}