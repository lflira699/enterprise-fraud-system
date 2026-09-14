package com.efs.modules.risk.service;

import com.efs.modules.risk.repository.RiskAssessmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskDashboardMetricsTest {

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @InjectMocks
    private RiskAssessmentService riskAssessmentService;

    @Test
    void tenantScopeShouldRemainTenantIsolated() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        BigDecimal expected =
                new BigDecimal("62.50");

        when(
                riskAssessmentRepository
                        .averageLatestRiskScoreByOrganizationIdAndTenantId(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(expected);

        assertEquals(
                expected,
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                tenantId
                        )
        );

        verify(
                riskAssessmentRepository,
                never()
        ).averageLatestRiskScoreByOrganizationIdAndTenantIdIsNull(
                organizationId
        );
    }

    @Test
    void organizationOnlyScopeShouldUseOnlyTenantNullRows() {

        UUID organizationId = UUID.randomUUID();

        BigDecimal expected =
                new BigDecimal("35.00");

        when(
                riskAssessmentRepository
                        .averageLatestRiskScoreByOrganizationIdAndTenantIdIsNull(
                                organizationId
                        )
        ).thenReturn(expected);

        assertEquals(
                expected,
                riskAssessmentService
                        .getAverageLatestRiskScore(
                                organizationId,
                                null
                        )
        );
    }

    @Test
    void missingOrganizationShouldFailClosed() {

        assertThrows(
                IllegalArgumentException.class,
                () -> riskAssessmentService
                        .getAverageLatestRiskScore(
                                null,
                                UUID.randomUUID()
                        )
        );

        verifyNoInteractions(
                riskAssessmentRepository
        );
    }
}
