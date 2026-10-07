package com.efs.modules.risk.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.event.dto.FraudEventReference;
import com.efs.modules.event.repository.FraudEventRepository;
import com.efs.modules.event.service.FraudEventLookupServiceInterface;
import com.efs.modules.risk.dto.EventRiskAssessmentResponse;
import com.efs.modules.risk.entity.EventRiskAssessment;
import com.efs.modules.risk.mapper.EventRiskAssessmentMapper;
import com.efs.modules.risk.repository.EventRiskAssessmentRepository;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRiskAssessmentReviewTest {

    @Mock
    private EventRiskAssessmentRepository eventRiskAssessmentRepository;

    @Mock
    private EventRiskAssessmentMapper eventRiskAssessmentMapper;

    @Mock
    private FraudEventRepository fraudEventRepository;

    private FraudEventLookupServiceInterface fraudEventLookupService;

    @Mock
    private RiskScoringModelResolver riskScoringModelResolver;

    @Mock
    private RiskCalculator riskCalculator;

    @Mock
    private SecurityContextProvider securityContextProvider;

    @Mock
    private UserAccountLookupServiceInterface userAccountLookupService;

    @Mock
    private EventRiskAssessmentAuditService eventRiskAssessmentAuditService;

    private EventRiskAssessmentService service;

    private UUID organizationId;
    private UUID tenantId;
    private UUID userId;
    private UUID sessionId;
    private UUID assessmentId;
    private UUID fraudEventId;

    private SecurityContext authorizedContext;

    @BeforeEach
    void setUp() {
        fraudEventLookupService =
                (
                        lookupFraudEventId,
                        lookupOrganizationId,
                        lookupTenantId
                ) ->
                        fraudEventRepository
                                .findByFraudEventIdAndOrganizationIdAndTenantId(
                                        lookupFraudEventId,
                                        lookupOrganizationId,
                                        lookupTenantId
                                )
                                .map(
                                        event ->
                                                new FraudEventReference(
                                                        event.getFraudEventId(),
                                                        event.getOrganizationId(),
                                                        event.getTenantId(),
                                                        event.getCorrelationId()
                                                )
                                )
                                .orElseThrow(
                                        () ->
                                                new com.efs.shared.exception.ResourceNotFoundException(
                                                        "Fraud event not found."
                                                )
                                );

        service =
                new EventRiskAssessmentService(
                        eventRiskAssessmentRepository,
                        eventRiskAssessmentMapper,
                        fraudEventLookupService,
                        riskScoringModelResolver,
                        riskCalculator,
                        securityContextProvider,
                        userAccountLookupService,
                        eventRiskAssessmentAuditService
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

        assessmentId =
                UUID.fromString(
                        "55555555-5555-5555-5555-555555555555"
                );

        fraudEventId =
                UUID.fromString(
                        "66666666-6666-6666-6666-666666666666"
                );

        authorizedContext =
                new SecurityContext(
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

        org.mockito.Mockito.lenient()
                .when(
                        userAccountLookupService
                                .getAuthorizedUser(
                                        userId
                                )
                )
                .thenReturn(
                        new UserAccountReference(
                                userId,
                                organizationId,
                                tenantId,
                                "uc032@example.test"
                        )
                );
    }

    @Test
    void readByIdUsesOrganizationAndTenantIsolation() {

        EventRiskAssessment assessment =
                eventRiskAssessment();

        EventRiskAssessmentResponse response =
                new EventRiskAssessmentResponse();

        when(
                eventRiskAssessmentRepository
                        .findByEventRiskAssessmentIdAndOrganizationIdAndTenantId(
                                assessmentId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        assessment
                )
        );

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                assessment
                        )
        ).thenReturn(
                response
        );

        EventRiskAssessmentResponse result =
                service.getEventRiskAssessmentById(
                        assessmentId,
                        authorizedContext
                );

        assertEquals(
                response,
                result
        );

        verify(
                eventRiskAssessmentRepository
        ).findByEventRiskAssessmentIdAndOrganizationIdAndTenantId(
                assessmentId,
                organizationId,
                tenantId
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).saveAndFlush(
                any()
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).delete(
                any()
        );
    }

    @Test
    void fraudEventHistoryReturnsEmptyCollectionWhenNoneExists() {

        when(
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdOrderByAssessmentTimestampDesc(
                                fraudEventId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                List.of()
        );

        List<EventRiskAssessmentResponse> result =
                service.getEventRiskAssessmentsByFraudEventId(
                        fraudEventId,
                        authorizedContext
                );

        assertEquals(
                0,
                result.size()
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).saveAndFlush(
                any()
        );
    }

    @Test
    void fraudEventHistoryPreservesMultipleAssessments() {

        EventRiskAssessment first =
                eventRiskAssessment();

        EventRiskAssessment second =
                eventRiskAssessment();

        EventRiskAssessmentResponse firstResponse =
                new EventRiskAssessmentResponse();

        EventRiskAssessmentResponse secondResponse =
                new EventRiskAssessmentResponse();

        when(
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdOrderByAssessmentTimestampDesc(
                                fraudEventId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                List.of(
                        first,
                        second
                )
        );

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                first
                        )
        ).thenReturn(
                firstResponse
        );

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                second
                        )
        ).thenReturn(
                secondResponse
        );

        List<EventRiskAssessmentResponse> result =
                service.getEventRiskAssessmentsByFraudEventId(
                        fraudEventId,
                        authorizedContext
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                firstResponse,
                result.get(0)
        );

        assertEquals(
                secondResponse,
                result.get(1)
        );
    }

    @Test
    void missingViewPermissionIsRejected() {

        SecurityContext denied =
                new SecurityContext(
                        userId,
                        tenantId,
                        sessionId,
                        Set.of(),
                        Set.of(),
                        Set.of()
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.getEventRiskAssessmentById(
                                assessmentId,
                                denied
                        )
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).findByEventRiskAssessmentIdAndOrganizationIdAndTenantId(
                any(),
                any(),
                any()
        );
    }

    private EventRiskAssessment eventRiskAssessment() {

        return new EventRiskAssessment(
                fraudEventId,
                organizationId,
                tenantId,
                UUID.fromString(
                        "77777777-7777-7777-7777-777777777777"
                ),
                "EVENT_MODEL",
                "v1",
                new java.math.BigDecimal("50.00"),
                "MEDIUM",
                null,
                null,
                java.util.Map.of(),
                1L
        );
    }}