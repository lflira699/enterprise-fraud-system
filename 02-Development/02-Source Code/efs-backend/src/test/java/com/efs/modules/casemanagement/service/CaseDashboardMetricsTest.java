package com.efs.modules.casemanagement.service;

import com.efs.modules.casemanagement.dto.CaseDashboardMetricsResponse;
import com.efs.modules.casemanagement.repository.CaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseDashboardMetricsTest {

    @Mock
    private CaseRepository caseRepository;

    @InjectMocks
    private CaseService caseService;

    @Test
    void tenantScopeShouldRemainTenantIsolated() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                caseRepository
                        .countByOrganizationIdAndTenantIdAndCurrentStatusNot(
                                organizationId,
                                tenantId,
                                "CLOSED"
                        )
        ).thenReturn(5L);

        when(
                caseRepository
                        .countByOrganizationIdAndTenantIdAndCurrentStatus(
                                organizationId,
                                tenantId,
                                "CLOSED"
                        )
        ).thenReturn(2L);

        CaseDashboardMetricsResponse response =
                caseService.getDashboardMetrics(
                        organizationId,
                        tenantId
                );

        assertEquals(
                5L,
                response.getOpenCases()
        );

        assertEquals(
                2L,
                response.getClosedCases()
        );

        verify(
                caseRepository,
                never()
        ).countByOrganizationIdAndTenantIdIsNullAndCurrentStatus(
                organizationId,
                "CLOSED"
        );
    }

    @Test
    void organizationOnlyScopeShouldNotExpandAcrossTenants() {

        UUID organizationId = UUID.randomUUID();

        when(
                caseRepository
                        .countByOrganizationIdAndTenantIdIsNullAndCurrentStatusNot(
                                organizationId,
                                "CLOSED"
                        )
        ).thenReturn(4L);

        when(
                caseRepository
                        .countByOrganizationIdAndTenantIdIsNullAndCurrentStatus(
                                organizationId,
                                "CLOSED"
                        )
        ).thenReturn(1L);

        CaseDashboardMetricsResponse response =
                caseService.getDashboardMetrics(
                        organizationId,
                        null
                );

        assertEquals(
                4L,
                response.getOpenCases()
        );

        assertEquals(
                1L,
                response.getClosedCases()
        );
    }

    @Test
    void missingOrganizationShouldFailClosed() {

        assertThrows(
                IllegalArgumentException.class,
                () -> caseService.getDashboardMetrics(
                        null,
                        UUID.randomUUID()
                )
        );

        verifyNoInteractions(
                caseRepository
        );
    }
}
