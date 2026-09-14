package com.efs.modules.risk.service;

import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.entity.RiskAssessment;
import com.efs.modules.risk.mapper.RiskAssessmentMapper;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RiskAssessmentOrganizationalScopeTest {

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "95111111-1111-1111-1111-111111111111"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "95222222-2222-2222-2222-222222222222"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "95333333-3333-3333-3333-333333333333"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "95444444-4444-4444-4444-444444444444"
            );

    private static final UUID RISK_ASSESSMENT_ID =
            UUID.fromString(
                    "95555555-5555-5555-5555-555555555555"
            );

    private RiskAssessmentRepository repository;

    private TransactionServiceInterface transactionService;

    private RiskScoringModelResolver modelResolver;

    private RiskCalculator riskCalculator;

    private DomainEventOutboxService outboxService;

    private AuditEventServiceInterface auditEventService;

    private TransactionRiskAssessmentAuditService
            riskAssessmentAuditService;

    private RiskAssessmentService service;

    @BeforeEach
    void setUp() {

        repository =
                mock(RiskAssessmentRepository.class);

        transactionService =
                mock(TransactionServiceInterface.class);

        modelResolver =
                mock(RiskScoringModelResolver.class);

        riskCalculator =
                mock(RiskCalculator.class);

        outboxService =
                mock(DomainEventOutboxService.class);

        auditEventService =
                mock(AuditEventServiceInterface.class);

        riskAssessmentAuditService =
                mock(
                        TransactionRiskAssessmentAuditService.class
                );

        service =
                new RiskAssessmentService(
                        repository,
                        new RiskAssessmentMapper(),
                        transactionService,
                        modelResolver,
                        riskCalculator,
                        outboxService,
                        auditEventService,
                        riskAssessmentAuditService
                );
    }

    @Test
    void shouldInheritOrganizationalScopeFromTransaction() {

        TransactionResponse transaction =
                transactionWithScope(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        when(
                transactionService.getTransactionById(
                        TRANSACTION_ID
                )
        ).thenReturn(
                transaction
        );

        RiskScoringModel model =
                mock(RiskScoringModel.class);

        when(
                modelResolver.resolve(
                        ORGANIZATION_ID,
                        TENANT_ID
                )
        ).thenReturn(
                model
        );

        RiskCalculationResult calculation =
                mock(RiskCalculationResult.class);

        when(
                calculation.overallRiskScore()
        ).thenReturn(
                new BigDecimal("42.00")
        );

        when(
                calculation.riskLevel()
        ).thenReturn(
                "MEDIUM"
        );

        when(
                calculation.modelName()
        ).thenReturn(
                "V157_SCOPE_TEST"
        );

        when(
                calculation.modelVersion()
        ).thenReturn(
                "1.0"
        );

        when(
                calculation.factorContributions()
        ).thenReturn(
                List.of()
        );

        when(
                riskCalculator.calculate(
                        eq(model),
                        anyMap()
                )
        ).thenReturn(
                calculation
        );

        when(
                repository
                        .findByTransactionIdAndAssessmentTypeOrderByAssessmentTimestampDesc(
                                TRANSACTION_ID,
                                "TRANSACTION"
                        )
        ).thenReturn(
                List.of()
        );

        when(
                repository.save(
                        any(RiskAssessment.class)
                )
        ).thenAnswer(
                invocation -> {

                    RiskAssessment assessment =
                            invocation.getArgument(0);

                    assessment.setRiskAssessmentId(
                            RISK_ASSESSMENT_ID
                    );

                    return assessment;
                }
        );

        service.createRiskAssessment(
                request()
        );

        ArgumentCaptor<RiskAssessment> captor =
                ArgumentCaptor.forClass(
                        RiskAssessment.class
                );

        verify(
                repository
        ).save(
                captor.capture()
        );

        RiskAssessment persisted =
                captor.getValue();

        assertEquals(
                TRANSACTION_ID,
                persisted.getTransactionId()
        );

        assertEquals(
                ORGANIZATION_ID,
                persisted.getOrganizationId()
        );

        assertEquals(
                TENANT_ID,
                persisted.getTenantId()
        );

        assertEquals(
                CORRELATION_ID,
                persisted.getCorrelationId()
        );

        verify(
                modelResolver
        ).resolve(
                ORGANIZATION_ID,
                TENANT_ID
        );
    }

    @Test
    void shouldFailClosedWhenTransactionOrganizationIsMissing() {

        TransactionResponse transaction =
                transactionWithScope(
                        null,
                        TENANT_ID
                );

        when(
                transactionService.getTransactionById(
                        TRANSACTION_ID
                )
        ).thenReturn(
                transaction
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.createRiskAssessment(
                                        request()
                                )
                );

        assertEquals(
                "Transaction organizationId is required "
                        + "for RiskAssessment organizational scope",
                exception.getMessage()
        );

        verify(
                transactionService
        ).getTransactionById(
                TRANSACTION_ID
        );

        verifyNoInteractions(
                modelResolver,
                riskCalculator,
                repository,
                outboxService,
                auditEventService
        );
    }

    private TransactionResponse transactionWithScope(
            UUID organizationId,
            UUID tenantId) {

        TransactionResponse transaction =
                new TransactionResponse();

        transaction.setTransactionId(
                TRANSACTION_ID
        );

        transaction.setOrganizationId(
                organizationId
        );

        transaction.setTenantId(
                tenantId
        );

        transaction.setCorrelationId(
                CORRELATION_ID
        );

        return transaction;
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
                "DECISION"
        );

        return request;
    }
}
