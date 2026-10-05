package com.efs.modules.risk.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.event.entity.FraudEvent;
import com.efs.modules.event.repository.FraudEventRepository;
import com.efs.modules.risk.dto.EventRiskAssessmentRequest;
import com.efs.modules.risk.entity.EventRiskAssessment;
import com.efs.modules.risk.mapper.EventRiskAssessmentMapper;
import com.efs.modules.risk.repository.EventRiskAssessmentRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
class EventRiskAssessmentServiceTest {

    @Mock
    private EventRiskAssessmentRepository eventRiskAssessmentRepository;

    @Mock
    private EventRiskAssessmentMapper eventRiskAssessmentMapper;

    @Mock
    private FraudEventRepository fraudEventRepository;

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
    private UUID fraudEventId;
    private UUID correlationId;

    private SecurityContext securityContext;
    private FraudEvent fraudEvent;
    private RiskScoringModel model;

    @BeforeEach
    void setUp() {

        service =
                new EventRiskAssessmentService(
                        eventRiskAssessmentRepository,
                        eventRiskAssessmentMapper,
                        fraudEventRepository,
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

        fraudEventId =
                UUID.fromString(
                        "55555555-5555-5555-5555-555555555555"
                );

        correlationId =
                UUID.fromString(
                        "66666666-6666-6666-6666-666666666666"
                );

        securityContext =
                new SecurityContext(
                        userId,
                        tenantId,
                        sessionId,
                        Set.of(),
                        Set.of(),
                        Set.of()
                );

        fraudEvent =
                new FraudEvent(
                        organizationId,
                        tenantId,
                        null,
                        "EVENT",
                        "TEST",
                        "UC-032",
                        "UC-032-IDEMPOTENCY",
                        correlationId,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        org.springframework.test.util.ReflectionTestUtils.setField(
                fraudEvent,
                "fraudEventId",
                fraudEventId
        );

        model =
                new RiskScoringModel(
                        "EVENT_MODEL",
                        "v1",
                        new BigDecimal("0"),
                        new BigDecimal("100"),
                        List.of(
                                new RiskScoringModel.Factor(
                                        "VELOCITY",
                                        true,
                                        new BigDecimal("1.00")
                                )
                        ),
                        List.of(
                                new RiskScoringModel.Threshold(
                                        "VERY_LOW",
                                        new BigDecimal("0")
                                ),
                                new RiskScoringModel.Threshold(
                                        "LOW",
                                        new BigDecimal("20")
                                ),
                                new RiskScoringModel.Threshold(
                                        "MEDIUM",
                                        new BigDecimal("40")
                                ),
                                new RiskScoringModel.Threshold(
                                        "HIGH",
                                        new BigDecimal("60")
                                ),
                                new RiskScoringModel.Threshold(
                                        "CRITICAL",
                                        new BigDecimal("80")
                                )
                        )
                );

        org.mockito.Mockito.lenient()
                .when(
                        securityContextProvider
                                .getCurrentContext()
                )
                .thenReturn(
                        securityContext
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
    void rejectsFraudEventNotFoundWithoutPersistence() {

        EventRiskAssessmentRequest request =
                request(
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("50.00")
                        )
                );

        when(
                fraudEventRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantId(
                                fraudEventId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.assess(
                                request
                        )
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).saveAndFlush(
                any()
        );

        verify(
                eventRiskAssessmentAuditService
        ).recordRejected(
                eq(organizationId),
                eq(securityContext),
                eq(fraudEventId),
                eq(null),
                eq(null),
                eq(null),
                eq("FRAUD_EVENT_NOT_FOUND"),
                any(ResourceNotFoundException.class)
        );
    }

    @Test
    void rejectsUnknownFactorWithoutPersistence() {

        EventRiskAssessmentRequest request =
                request(
                        Map.of(
                                "UNKNOWN_FACTOR",
                                new BigDecimal("50.00")
                        )
                );

        stubFraudEvent();

        when(
                riskScoringModelResolver
                        .resolveEvent(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                model
        );

        assertThrows(
                RuntimeException.class,
                () ->
                        service.assess(
                                request
                        )
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).saveAndFlush(
                any()
        );
    }

    @Test
    void rejectsMissingEnabledFactorWithoutPersistence() {

        EventRiskAssessmentRequest request =
                request(
                        Map.of()
                );

        stubFraudEvent();

        when(
                riskScoringModelResolver
                        .resolveEvent(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                model
        );

        assertThrows(
                RuntimeException.class,
                () ->
                        service.assess(
                                request
                        )
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).saveAndFlush(
                any()
        );
    }

    @Test
    void doesNotInvokeForbiddenDownstreamProcessing() {

        Set<String> forbiddenDependencyNames =
                Set.of(
                        "DomainEventOutboxService",
                        "RabbitTemplate",
                        "DecisionService",
                        "AlertService",
                        "CaseService",
                        "WorkflowService",
                        "PlaybookService"
                );

        for (
                java.lang.reflect.Field field :
                EventRiskAssessmentService.class
                        .getDeclaredFields()
        ) {

            String dependencyType =
                    field.getType()
                            .getSimpleName();

            org.junit.jupiter.api.Assertions.assertFalse(
                    forbiddenDependencyNames.contains(
                            dependencyType
                    ),
                    "Forbidden downstream dependency: "
                            + dependencyType
            );
        }
    }


    @Test
    void validEligibleFraudEventIsAssessed() {

        EventRiskAssessment assessment =
                executeNewAssessment();

        assertEquals(
                fraudEventId,
                assessment.getFraudEventId()
        );

        assertEquals(
                new BigDecimal("50.00"),
                assessment.getOverallRiskScore()
        );

        assertEquals(
                "MEDIUM",
                assessment.getRiskLevel()
        );
    }

    @Test
    void fraudEventWithoutCorrelationIsNotEligible() {

        FraudEvent ineligible =
                fraudEvent(
                        organizationId,
                        tenantId,
                        fraudEventId,
                        null
                );

        when(
                fraudEventRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantId(
                                fraudEventId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        ineligible
                )
        );

        assertThrows(
                com.efs.shared.exception.RequestValidationException.class,
                () ->
                        service.assess(
                                request(
                                        Map.of(
                                                "VELOCITY",
                                                new BigDecimal("50.00")
                                        )
                                )
                        )
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).saveAndFlush(
                any()
        );

        verify(
                eventRiskAssessmentAuditService
        ).recordRejected(
                eq(organizationId),
                eq(securityContext),
                eq(fraudEventId),
                eq(null),
                eq(null),
                eq(null),
                eq("EVENT_RISK_NOT_ELIGIBLE"),
                any(com.efs.shared.exception.RequestValidationException.class)
        );
    }

    @Test
    void riskSubjectTypeRemainsEvent() throws Exception {

        java.lang.reflect.Field fraudEventIdentity =
                EventRiskAssessment.class
                        .getDeclaredField(
                                "fraudEventId"
                        );

        assertEquals(
                UUID.class,
                fraudEventIdentity.getType()
        );

        assertThrows(
                NoSuchFieldException.class,
                () ->
                        EventRiskAssessment.class
                                .getDeclaredField(
                                        "subjectType"
                                )
        );

        assertThrows(
                NoSuchFieldException.class,
                () ->
                        EventRiskAssessment.class
                                .getDeclaredField(
                                        "subjectId"
                                )
        );
    }

    @Test
    void preservesFraudEventIdentity() {

        EventRiskAssessment assessment =
                executeNewAssessment();

        assertEquals(
                fraudEvent.getFraudEventId(),
                assessment.getFraudEventId()
        );
    }

    @Test
    void assessmentIdentityIsIndependentFromFraudEventIdentity()
            throws Exception {

        java.lang.reflect.Field assessmentIdentity =
                EventRiskAssessment.class
                        .getDeclaredField(
                                "eventRiskAssessmentId"
                        );

        java.lang.reflect.Field subjectIdentity =
                EventRiskAssessment.class
                        .getDeclaredField(
                                "fraudEventId"
                        );

        org.junit.jupiter.api.Assertions.assertTrue(
                assessmentIdentity.isAnnotationPresent(
                        jakarta.persistence.Id.class
                )
        );

        org.junit.jupiter.api.Assertions.assertFalse(
                subjectIdentity.isAnnotationPresent(
                        jakarta.persistence.Id.class
                )
        );

        org.junit.jupiter.api.Assertions.assertNotEquals(
                assessmentIdentity.getName(),
                subjectIdentity.getName()
        );
    }

    @Test
    void preservesOrganizationContext() {

        EventRiskAssessment assessment =
                executeNewAssessment();

        assertEquals(
                organizationId,
                assessment.getOrganizationId()
        );
    }

    @Test
    void preservesTenantContext() {

        EventRiskAssessment assessment =
                executeNewAssessment();

        assertEquals(
                tenantId,
                assessment.getTenantId()
        );
    }

    @Test
    void preservesCorrelationContext() {

        EventRiskAssessment assessment =
                executeNewAssessment();

        assertEquals(
                correlationId,
                assessment.getCorrelationId()
        );
    }

    @Test
    void preservesModelVersion() {

        EventRiskAssessment assessment =
                executeNewAssessment();

        assertEquals(
                "EVENT_MODEL",
                assessment.getModelId()
        );

        assertEquals(
                "v1",
                assessment.getModelVersion()
        );
    }

    @Test
    void resolvesValidFactorInput() {

        stubFraudEvent();

        when(
                riskScoringModelResolver
                        .resolveEvent(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                model
        );

        RiskCalculationResult calculation =
                calculation(
                        "v1",
                        new BigDecimal("50.00")
                );

        when(
                riskCalculator.calculate(
                        eq(model),
                        eq(
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("50.00")
                                )
                        )
                )
        ).thenReturn(
                calculation
        );

        when(
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                                fraudEventId,
                                organizationId,
                                tenantId,
                                correlationId,
                                "EVENT_MODEL",
                                "v1"
                        )
        ).thenReturn(
                List.of()
        );

        when(
                eventRiskAssessmentRepository
                        .saveAndFlush(
                                any(EventRiskAssessment.class)
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                any(EventRiskAssessment.class)
                        )
        ).thenReturn(
                new com.efs.modules.risk.dto.EventRiskAssessmentResponse()
        );

        service.assess(
                request(
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("50.00")
                        )
                )
        );

        verify(
                riskCalculator
        ).calculate(
                eq(model),
                eq(
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("50.00")
                        )
                )
        );
    }

    @Test
    void persistsHistoricalAssessment() {

        executeNewAssessment();

        verify(
                eventRiskAssessmentRepository
        ).saveAndFlush(
                any(EventRiskAssessment.class)
        );
    }

    @Test
    void reusesExactSameProcessAssessment() {

        stubFraudEvent();

        when(
                riskScoringModelResolver
                        .resolveEvent(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                model
        );

        RiskCalculationResult calculation =
                calculation(
                        "v1",
                        new BigDecimal("50.00")
                );

        when(
                riskCalculator.calculate(
                        eq(model),
                        eq(
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("50.00")
                                )
                        )
                )
        ).thenReturn(
                calculation
        );

        EventRiskAssessment reusable =
                assessment(
                        organizationId,
                        tenantId,
                        fraudEventId,
                        correlationId,
                        "v1",
                        new BigDecimal("50.0")
                );

        when(
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                                fraudEventId,
                                organizationId,
                                tenantId,
                                correlationId,
                                "EVENT_MODEL",
                                "v1"
                        )
        ).thenReturn(
                List.of(
                        reusable
                )
        );

        com.efs.modules.risk.dto.EventRiskAssessmentResponse response =
                new com.efs.modules.risk.dto.EventRiskAssessmentResponse();

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                reusable
                        )
        ).thenReturn(
                response
        );

        org.junit.jupiter.api.Assertions.assertSame(
                response,
                service.assess(
                        request(
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("50.00")
                                )
                        )
                )
        );

        verify(
                eventRiskAssessmentRepository,
                never()
        ).saveAndFlush(
                any()
        );

        verify(
                eventRiskAssessmentAuditService
        ).recordSuccess(
                reusable,
                securityContext,
                true
        );
    }

    @Test
    void doesNotReuseOutsideSameProcessCorrelation() {

        UUID otherCorrelation =
                UUID.fromString(
                        "77777777-7777-7777-7777-777777777777"
                );

        FraudEvent otherProcessEvent =
                fraudEvent(
                        organizationId,
                        tenantId,
                        fraudEventId,
                        otherCorrelation
                );

        when(
                fraudEventRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantId(
                                fraudEventId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        otherProcessEvent
                )
        );

        stubModelCalculationAndNewPersistence(
                model,
                calculation(
                        "v1",
                        new BigDecimal("50.00")
                ),
                otherCorrelation
        );

        service.assess(
                request(
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("50.00")
                        )
                )
        );

        verify(
                eventRiskAssessmentRepository
        ).findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                fraudEventId,
                organizationId,
                tenantId,
                otherCorrelation,
                "EVENT_MODEL",
                "v1"
        );

        verify(
                eventRiskAssessmentRepository
        ).saveAndFlush(
                any(EventRiskAssessment.class)
        );
    }

    @Test
    void doesNotReuseDifferentModelVersion() {

        RiskScoringModel versionTwo =
                new RiskScoringModel(
                        "EVENT_MODEL",
                        "v2",
                        new BigDecimal("0"),
                        new BigDecimal("100"),
                        List.of(
                                new RiskScoringModel.Factor(
                                        "VELOCITY",
                                        true,
                                        new BigDecimal("1.00")
                                )
                        ),
                        model.thresholds()
                );

        stubFraudEvent();

        stubModelCalculationAndNewPersistence(
                versionTwo,
                calculation(
                        "v2",
                        new BigDecimal("50.00")
                ),
                correlationId
        );

        service.assess(
                request(
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("50.00")
                        )
                )
        );

        verify(
                eventRiskAssessmentRepository
        ).findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                fraudEventId,
                organizationId,
                tenantId,
                correlationId,
                "EVENT_MODEL",
                "v2"
        );

        verify(
                eventRiskAssessmentRepository
        ).saveAndFlush(
                any(EventRiskAssessment.class)
        );
    }

    @Test
    void doesNotReuseDifferentRelevantInputs() {

        stubFraudEvent();

        when(
                riskScoringModelResolver
                        .resolveEvent(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                model
        );

        when(
                riskCalculator.calculate(
                        eq(model),
                        eq(
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("50.00")
                                )
                        )
                )
        ).thenReturn(
                calculation(
                        "v1",
                        new BigDecimal("50.00")
                )
        );

        EventRiskAssessment differentInputs =
                assessment(
                        organizationId,
                        tenantId,
                        fraudEventId,
                        correlationId,
                        "v1",
                        new BigDecimal("40.00")
                );

        when(
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                                fraudEventId,
                                organizationId,
                                tenantId,
                                correlationId,
                                "EVENT_MODEL",
                                "v1"
                        )
        ).thenReturn(
                List.of(
                        differentInputs
                )
        );

        when(
                eventRiskAssessmentRepository
                        .saveAndFlush(
                                any(EventRiskAssessment.class)
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                any(EventRiskAssessment.class)
                        )
        ).thenReturn(
                new com.efs.modules.risk.dto.EventRiskAssessmentResponse()
        );

        service.assess(
                request(
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("50.00")
                        )
                )
        );

        verify(
                eventRiskAssessmentRepository
        ).saveAndFlush(
                any(EventRiskAssessment.class)
        );
    }

    @Test
    void doesNotReuseAcrossOrganizations() {

        executeNewAssessment();

        verify(
                fraudEventRepository
        ).findByFraudEventIdAndOrganizationIdAndTenantId(
                fraudEventId,
                organizationId,
                tenantId
        );

        verify(
                eventRiskAssessmentRepository
        ).findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                fraudEventId,
                organizationId,
                tenantId,
                correlationId,
                "EVENT_MODEL",
                "v1"
        );
    }

    @Test
    void doesNotReuseAcrossTenants() {

        executeNewAssessment();

        verify(
                fraudEventRepository
        ).findByFraudEventIdAndOrganizationIdAndTenantId(
                fraudEventId,
                organizationId,
                tenantId
        );

        verify(
                eventRiskAssessmentRepository
        ).findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                fraudEventId,
                organizationId,
                tenantId,
                correlationId,
                "EVENT_MODEL",
                "v1"
        );
    }
    private void stubFraudEvent() {

        when(
                fraudEventRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantId(
                                fraudEventId,
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        fraudEvent
                )
        );
    }


    private EventRiskAssessment executeNewAssessment() {

        stubFraudEvent();

        when(
                riskScoringModelResolver
                        .resolveEvent(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                model
        );

        RiskCalculationResult calculation =
                calculation(
                        "v1",
                        new BigDecimal("50.00")
                );

        when(
                riskCalculator.calculate(
                        eq(model),
                        eq(
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("50.00")
                                )
                        )
                )
        ).thenReturn(
                calculation
        );

        when(
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                                fraudEventId,
                                organizationId,
                                tenantId,
                                correlationId,
                                "EVENT_MODEL",
                                "v1"
                        )
        ).thenReturn(
                List.of()
        );

        when(
                eventRiskAssessmentRepository
                        .saveAndFlush(
                                any(EventRiskAssessment.class)
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                any(EventRiskAssessment.class)
                        )
        ).thenReturn(
                new com.efs.modules.risk.dto.EventRiskAssessmentResponse()
        );

        service.assess(
                request(
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("50.00")
                        )
                )
        );

        org.mockito.ArgumentCaptor<EventRiskAssessment> captor =
                org.mockito.ArgumentCaptor.forClass(
                        EventRiskAssessment.class
                );

        verify(
                eventRiskAssessmentRepository
        ).saveAndFlush(
                captor.capture()
        );

        return captor.getValue();
    }

    private RiskCalculationResult calculation(
            String modelVersion,
            BigDecimal score) {

        return new RiskCalculationResult(
                "EVENT_MODEL",
                modelVersion,
                score,
                "MEDIUM",
                List.of(
                        new RiskCalculationResult.FactorContribution(
                                "VELOCITY",
                                score,
                                new BigDecimal("1.00"),
                                score
                        )
                )
        );
    }

    private EventRiskAssessment assessment(
            UUID assessmentOrganizationId,
            UUID assessmentTenantId,
            UUID assessmentFraudEventId,
            UUID assessmentCorrelationId,
            String assessmentModelVersion,
            BigDecimal persistedFactorScore) {

        return new EventRiskAssessment(
                assessmentFraudEventId,
                assessmentOrganizationId,
                assessmentTenantId,
                assessmentCorrelationId,
                "EVENT_MODEL",
                assessmentModelVersion,
                persistedFactorScore,
                "MEDIUM",
                null,
                null,
                Map.of(
                        "factorScores",
                        Map.of(
                                "VELOCITY",
                                persistedFactorScore
                        ),
                        "factorWeights",
                        Map.of(
                                "VELOCITY",
                                new BigDecimal("1.00")
                        )
                ),
                1L
        );
    }

    private FraudEvent fraudEvent(
            UUID eventOrganizationId,
            UUID eventTenantId,
            UUID eventFraudEventId,
            UUID eventCorrelationId) {

        FraudEvent event =
                new FraudEvent(
                        eventOrganizationId,
                        eventTenantId,
                        null,
                        "EVENT",
                        "TEST",
                        "UC-032",
                        "UC-032-IDEMPOTENCY",
                        eventCorrelationId,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        org.springframework.test.util.ReflectionTestUtils.setField(
                event,
                "fraudEventId",
                eventFraudEventId
        );

        return event;
    }

    private void stubModelCalculationAndNewPersistence(
            RiskScoringModel selectedModel,
            RiskCalculationResult calculation,
            UUID selectedCorrelationId) {

        when(
                riskScoringModelResolver
                        .resolveEvent(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                selectedModel
        );

        when(
                riskCalculator.calculate(
                        eq(selectedModel),
                        eq(
                                Map.of(
                                        "VELOCITY",
                                        new BigDecimal("50.00")
                                )
                        )
                )
        ).thenReturn(
                calculation
        );

        when(
                eventRiskAssessmentRepository
                        .findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
                                fraudEventId,
                                organizationId,
                                tenantId,
                                selectedCorrelationId,
                                calculation.modelName(),
                                calculation.modelVersion()
                        )
        ).thenReturn(
                List.of()
        );

        when(
                eventRiskAssessmentRepository
                        .saveAndFlush(
                                any(EventRiskAssessment.class)
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        when(
                eventRiskAssessmentMapper
                        .toResponse(
                                any(EventRiskAssessment.class)
                        )
        ).thenReturn(
                new com.efs.modules.risk.dto.EventRiskAssessmentResponse()
        );
    }
    private EventRiskAssessmentRequest request(
            Map<String, BigDecimal> factorScores) {

        EventRiskAssessmentRequest request =
                new EventRiskAssessmentRequest();

        request.setFraudEventId(
                fraudEventId
        );

        request.setFactorScores(
                factorScores
        );

        return request;
    }
}