package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.mapper.RiskAssessmentMapper;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionRiskAssessmentErrorAuditTest {

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "91919191-9191-9191-9191-919191919191"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "92929292-9292-9292-9292-929292929292"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "93939393-9393-9393-9393-939393939393"
            );

    private RiskAssessmentRepository repository;
    private RiskAssessmentMapper mapper;
    private TransactionRepository transactionRepository;
    private RiskScoringModelResolver modelResolver;
    private RiskCalculator riskCalculator;
    private DomainEventOutboxService outboxService;
    private AuditEventServiceInterface auditEventService;
    private TransactionRiskAssessmentAuditService errorAuditService;

    private RiskAssessmentService service;

    @BeforeEach
    void setUp() {

        repository =
                mock(RiskAssessmentRepository.class);

        mapper =
                mock(RiskAssessmentMapper.class);

        transactionRepository =
                mock(TransactionRepository.class);

        modelResolver =
                mock(RiskScoringModelResolver.class);

        riskCalculator =
                mock(RiskCalculator.class);

        outboxService =
                mock(DomainEventOutboxService.class);

        auditEventService =
                mock(AuditEventServiceInterface.class);

        errorAuditService =
                mock(TransactionRiskAssessmentAuditService.class);

        service =
                new RiskAssessmentService(
                        repository,
                        mapper,
                        transactionRepository,
                        modelResolver,
                        riskCalculator,
                        outboxService,
                        auditEventService,
                        errorAuditService
                );
    }

    @Test
    void shouldAuditE1AsRejectedWhenEnabledFactorIsMissing() {

        prepareTransaction(
                CORRELATION_ID
        );

        RiskScoringModel model =
                mock(RiskScoringModel.class);

        when(
                modelResolver.resolve(
                        ORGANIZATION_ID,
                        null
                )
        ).thenReturn(model);

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Risk score is required for enabled factor: DEVICE"
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
                                        request()
                                )
                );

        assertSame(
                exception,
                thrown
        );

        verify(errorAuditService)
                .recordRejected(
                        TRANSACTION_ID,
                        CORRELATION_ID,
                        "TRANSACTION_INFORMATION_INSUFFICIENT",
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
    }

    @Test
    void shouldAuditE2AsRejectedWhenRiskRulesAreUnavailable() {

        prepareTransaction(
                CORRELATION_ID
        );

        IllegalStateException exception =
                new IllegalStateException(
                        "Required risk configuration is unavailable: "
                                + "EFS.RISK.ACTIVE_MODEL"
                );

        when(
                modelResolver.resolve(
                        ORGANIZATION_ID,
                        null
                )
        ).thenThrow(exception);

        RuntimeException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.createRiskAssessment(
                                        request()
                                )
                );

        assertSame(
                exception,
                thrown
        );

        verify(errorAuditService)
                .recordRejected(
                        TRANSACTION_ID,
                        CORRELATION_ID,
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
    }

    @Test
    void shouldAuditE3AsFailureForUnexpectedProcessingError() {

        prepareTransaction(
                CORRELATION_ID
        );

        RiskScoringModel model =
                mock(RiskScoringModel.class);

        when(
                modelResolver.resolve(
                        ORGANIZATION_ID,
                        null
                )
        ).thenReturn(model);

        RuntimeException exception =
                new RuntimeException(
                        "Unexpected transaction risk processing failure"
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
                                        request()
                                )
                );

        assertSame(
                exception,
                thrown
        );

        verify(errorAuditService)
                .recordFailure(
                        TRANSACTION_ID,
                        CORRELATION_ID,
                        "TRANSACTION_RISK_ASSESSMENT_FAILED",
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
    }

    @Test
    void shouldAuditMissingCorrelationIdSeparately() {

        prepareTransaction(
                null
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.createRiskAssessment(
                                        request()
                                )
                );

        assertEquals(
                "Transaction correlationId is required "
                        + "for RiskCalculated event",
                thrown.getMessage()
        );

        verify(errorAuditService)
                .recordRejected(
                        TRANSACTION_ID,
                        null,
                        "TRANSACTION_CORRELATION_ID_REQUIRED",
                        thrown
                );
    }

    @Test
    void shouldBuildRejectedAndFailureAuditEvents() {

        AuditEventServiceInterface delegate =
                mock(AuditEventServiceInterface.class);

        TransactionRiskAssessmentAuditService auditService =
                new TransactionRiskAssessmentAuditService(
                        delegate
                );

        IllegalArgumentException rejectedException =
                new IllegalArgumentException(
                        "missing transaction information"
                );

        RuntimeException failureException =
                new RuntimeException(
                        "risk processing failure"
                );

        auditService.recordRejected(
                TRANSACTION_ID,
                CORRELATION_ID,
                "TRANSACTION_INFORMATION_INSUFFICIENT",
                rejectedException
        );

        UUID failureCorrelationId =
                UUID.randomUUID();

        auditService.recordFailure(
                TRANSACTION_ID,
                failureCorrelationId,
                "TRANSACTION_RISK_ASSESSMENT_FAILED",
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
                "TRANSACTION_RISK_ASSESSED",
                rejected.getEventType()
        );

        assertEquals(
                "RISK_ASSESSMENT",
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
                CORRELATION_ID,
                rejected.getCorrelationId()
        );

        assertEquals(
                "REJECTED",
                rejected.getEventResult()
        );

        assertEquals(
                TRANSACTION_ID.toString(),
                rejected.getEventDetails()
                        .get("transactionId")
        );

        assertEquals(
                "TRANSACTION_INFORMATION_INSUFFICIENT",
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
                "TRANSACTION_RISK_ASSESSMENT_FAILED",
                failure.getEventDetails()
                        .get("reason")
        );
    }

    @Test
    void rejectedAndFailureAuditMethodsShouldUseRequiresNew()
            throws Exception {

        Method rejectedMethod =
                TransactionRiskAssessmentAuditService.class
                        .getMethod(
                                "recordRejected",
                                UUID.class,
                                UUID.class,
                                String.class,
                                RuntimeException.class
                        );

        Method failureMethod =
                TransactionRiskAssessmentAuditService.class
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

    private void prepareTransaction(
            UUID correlationId) {

        Transaction transaction =
                new Transaction();

        transaction.setTransactionId(
                TRANSACTION_ID
        );

        transaction.setOrganizationId(
                ORGANIZATION_ID
        );

        transaction.setCorrelationId(
                correlationId
        );

        when(
                transactionRepository
                        .findByTransactionIdAndDeletedAtIsNull(
                                TRANSACTION_ID
                        )
        ).thenReturn(
                Optional.of(transaction)
        );
    }

    private RiskAssessmentRequest request() {

        RiskAssessmentRequest request =
                new RiskAssessmentRequest();

        request.setTransactionId(
                TRANSACTION_ID
        );

        request.setAssessmentType(
                "TRANSACTION"
        );

        request.setAssessmentStage(
                "FINAL"
        );

        request.setAssessmentResult(
                "REVIEW"
        );

        BigDecimal score =
                new BigDecimal("50.00");

        request.setRulesScore(score);
        request.setBehavioralScore(score);
        request.setCustomerScore(score);
        request.setGeographicScore(score);
        request.setDeviceScore(score);

        return request;
    }
}