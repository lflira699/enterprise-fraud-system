package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.mapper.CustomerRiskProfileMapper;
import com.efs.modules.customer.repository.CustomerHistoryRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.customer.repository.CustomerRiskProfileRepository;
import com.efs.modules.integration.service.DomainEventOutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CustomerRiskAssessmentErrorAuditTest {

    private CustomerRepository customerRepository;
    private CustomerRiskProfileRepository profileRepository;
    private CustomerHistoryRepository historyRepository;
    private CustomerRiskProfileMapper mapper;
    private RiskScoringModelResolver modelResolver;
    private RiskCalculator riskCalculator;
    private AuditEventServiceInterface auditEventService;
    private DomainEventOutboxService outboxService;
    private CustomerRiskAssessmentAuditService errorAuditService;

    private CustomerRiskAssessmentService service;

    @BeforeEach
    void setUp() {

        customerRepository =
                mock(CustomerRepository.class);

        profileRepository =
                mock(CustomerRiskProfileRepository.class);

        historyRepository =
                mock(CustomerHistoryRepository.class);

        mapper =
                mock(CustomerRiskProfileMapper.class);

        modelResolver =
                mock(RiskScoringModelResolver.class);

        riskCalculator =
                mock(RiskCalculator.class);

        auditEventService =
                mock(AuditEventServiceInterface.class);

        outboxService =
                mock(DomainEventOutboxService.class);

        errorAuditService =
                mock(CustomerRiskAssessmentAuditService.class);

        service =
                new CustomerRiskAssessmentService(
                        customerRepository,
                        profileRepository,
                        historyRepository,
                        mapper,
                        modelResolver,
                        riskCalculator,
                        auditEventService,
                        outboxService,
                        errorAuditService
                );
    }

    @Test
    void shouldAuditE1AsRejectedWhenEnabledFactorIsMissing() {

        UUID customerId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        prepareCreate(customerId);

        RiskScoringModel model =
                mock(RiskScoringModel.class);

        when(
                modelResolver.resolveCustomer()
        ).thenReturn(model);

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Risk score is required for enabled factor: WATCHLIST"
                );

        when(
                riskCalculator.calculate(
                        any(RiskScoringModel.class),
                        anyMap()
                )
        ).thenThrow(exception);

        RuntimeException thrown =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                service.createRiskAssessment(
                                        customerId,
                                        request(correlationId)
                                )
                );

        assertSame(
                exception,
                thrown
        );

        verify(errorAuditService)
                .recordRejected(
                        customerId,
                        correlationId,
                        "CUSTOMER_INFORMATION_INSUFFICIENT",
                        exception
                );

        verify(
                errorAuditService,
                never()
        ).recordFailure(
                any(),
                any(),
                any(),
                any()
        );

        verifyNoInteractions(
                auditEventService,
                outboxService
        );
    }

    @Test
    void shouldAuditE2AsRejectedWhenRiskRulesAreUnavailable() {

        UUID customerId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        prepareCreate(customerId);

        IllegalStateException exception =
                new IllegalStateException(
                        "Required risk configuration is unavailable: "
                                + "EFS.RISK.CUSTOMER.ACTIVE_MODEL"
                );

        when(
                modelResolver.resolveCustomer()
        ).thenThrow(exception);

        RuntimeException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.createRiskAssessment(
                                        customerId,
                                        request(correlationId)
                                )
                );

        assertSame(
                exception,
                thrown
        );

        verify(errorAuditService)
                .recordRejected(
                        customerId,
                        correlationId,
                        "RISK_EVALUATION_RULES_UNAVAILABLE",
                        exception
                );

        verify(
                errorAuditService,
                never()
        ).recordFailure(
                any(),
                any(),
                any(),
                any()
        );

        verifyNoInteractions(
                auditEventService,
                outboxService
        );
    }

    @Test
    void shouldAuditE3AsFailureForUnexpectedProcessingError() {

        UUID customerId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        prepareCreate(customerId);

        RiskScoringModel model =
                mock(RiskScoringModel.class);

        when(
                modelResolver.resolveCustomer()
        ).thenReturn(model);

        RuntimeException exception =
                new RuntimeException(
                        "Unexpected processing failure"
                );

        when(
                riskCalculator.calculate(
                        any(RiskScoringModel.class),
                        anyMap()
                )
        ).thenThrow(exception);

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                service.createRiskAssessment(
                                        customerId,
                                        request(correlationId)
                                )
                );

        assertSame(
                exception,
                thrown
        );

        verify(errorAuditService)
                .recordFailure(
                        customerId,
                        correlationId,
                        "CUSTOMER_RISK_ASSESSMENT_FAILED",
                        exception
                );

        verify(
                errorAuditService,
                never()
        ).recordRejected(
                any(),
                any(),
                any(),
                any()
        );

        verifyNoInteractions(
                auditEventService,
                outboxService
        );
    }

    @Test
    void shouldBuildRejectedAndFailureAuditEvents() {

        AuditEventServiceInterface delegate =
                mock(AuditEventServiceInterface.class);

        CustomerRiskAssessmentAuditService auditService =
                new CustomerRiskAssessmentAuditService(
                        delegate
                );

        UUID customerId =
                UUID.randomUUID();

        UUID rejectedCorrelationId =
                UUID.randomUUID();

        UUID failureCorrelationId =
                UUID.randomUUID();

        IllegalArgumentException rejectedException =
                new IllegalArgumentException(
                        "missing customer information"
                );

        RuntimeException failureException =
                new RuntimeException(
                        "processing failure"
                );

        auditService.recordRejected(
                customerId,
                rejectedCorrelationId,
                "CUSTOMER_INFORMATION_INSUFFICIENT",
                rejectedException
        );

        auditService.recordFailure(
                customerId,
                failureCorrelationId,
                "CUSTOMER_RISK_ASSESSMENT_FAILED",
                failureException
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                delegate,
                org.mockito.Mockito.times(2)
        ).createAuditEvent(
                captor.capture()
        );

        AuditEventRequest rejected =
                captor.getAllValues().get(0);

        assertEquals(
                "CUSTOMER_RISK_ASSESSED",
                rejected.getEventType()
        );

        assertEquals(
                "CUSTOMER_RISK_PROFILE",
                rejected.getEntityType()
        );

        assertEquals(
                "CALCULATE",
                rejected.getAction()
        );

        assertEquals(
                "RISK_ENGINE",
                rejected.getSourceComponent()
        );

        assertEquals(
                rejectedCorrelationId,
                rejected.getCorrelationId()
        );

        assertEquals(
                "REJECTED",
                rejected.getEventResult()
        );

        assertEquals(
                customerId.toString(),
                rejected.getEventDetails()
                        .get("customerId")
        );

        assertEquals(
                "CUSTOMER_INFORMATION_INSUFFICIENT",
                rejected.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                false,
                rejected.getEventDetails()
                        .get("reused")
        );

        AuditEventRequest failure =
                captor.getAllValues().get(1);

        assertEquals(
                "FAILURE",
                failure.getEventResult()
        );

        assertEquals(
                failureCorrelationId,
                failure.getCorrelationId()
        );

        assertEquals(
                "CUSTOMER_RISK_ASSESSMENT_FAILED",
                failure.getEventDetails()
                        .get("reason")
        );
    }

    @Test
    void rejectedAndFailureAuditMethodsShouldUseRequiresNew() throws Exception {

        Method rejectedMethod =
                CustomerRiskAssessmentAuditService.class
                        .getMethod(
                                "recordRejected",
                                UUID.class,
                                UUID.class,
                                String.class,
                                RuntimeException.class
                        );

        Method failureMethod =
                CustomerRiskAssessmentAuditService.class
                        .getMethod(
                                "recordFailure",
                                UUID.class,
                                UUID.class,
                                String.class,
                                RuntimeException.class
                        );

        Transactional rejectedTransactional =
                rejectedMethod.getAnnotation(
                        Transactional.class
                );

        Transactional failureTransactional =
                failureMethod.getAnnotation(
                        Transactional.class
                );

        assertNotNull(
                rejectedTransactional
        );

        assertNotNull(
                failureTransactional
        );

        assertEquals(
                Propagation.REQUIRES_NEW,
                rejectedTransactional.propagation()
        );

        assertEquals(
                Propagation.REQUIRES_NEW,
                failureTransactional.propagation()
        );
    }

    private void prepareCreate(
            UUID customerId) {

        when(
                customerRepository
                        .findByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
        ).thenReturn(
                Optional.of(
                        new Customer()
                )
        );

        when(
                historyRepository
                        .findFirstByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                "CUSTOMER_RISK_ASSESSED"
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(
                profileRepository
                        .existsByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
        ).thenReturn(false);
    }

    private CustomerRiskProfileRequest request(
            UUID correlationId) {

        CustomerRiskProfileRequest request =
                new CustomerRiskProfileRequest();

        request.setCorrelationId(
                correlationId
        );

        BigDecimal score =
                new BigDecimal("50");

        request.setBehaviorScore(score);
        request.setFraudScore(score);
        request.setAmlScore(score);
        request.setKycScore(score);
        request.setDeviceScore(score);
        request.setSanctionsScore(score);
        request.setPepScore(score);
        request.setWatchlistScore(score);

        return request;
    }
}