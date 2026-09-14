package com.efs.modules.alert.service;

import com.efs.modules.alert.dto.AlertDashboardMetricsResponse;
import com.efs.modules.alert.repository.AlertRepository;
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
class AlertDashboardMetricsTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void tenantScopeShouldRemainTenantIsolated() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                alertRepository
                        .countByOrganizationIdAndTenantIdAndPriority(
                                organizationId,
                                tenantId,
                                "CRITICAL"
                        )
        ).thenReturn(3L);

        when(
                alertRepository
                        .countByOrganizationIdAndTenantIdAndStatusNot(
                                organizationId,
                                tenantId,
                                "CLOSED"
                        )
        ).thenReturn(7L);

        AlertDashboardMetricsResponse response =
                alertService.getDashboardMetrics(
                        organizationId,
                        tenantId
                );

        assertEquals(
                3L,
                response.getCriticalAlerts()
        );

        assertEquals(
                7L,
                response.getOpenAlerts()
        );

        verify(
                alertRepository,
                never()
        ).countByOrganizationIdAndTenantIdIsNullAndPriority(
                organizationId,
                "CRITICAL"
        );
    }

    @Test
    void organizationOnlyScopeShouldNotExpandAcrossTenants() {

        UUID organizationId = UUID.randomUUID();

        when(
                alertRepository
                        .countByOrganizationIdAndTenantIdIsNullAndPriority(
                                organizationId,
                                "CRITICAL"
                        )
        ).thenReturn(2L);

        when(
                alertRepository
                        .countByOrganizationIdAndTenantIdIsNullAndStatusNot(
                                organizationId,
                                "CLOSED"
                        )
        ).thenReturn(4L);

        AlertDashboardMetricsResponse response =
                alertService.getDashboardMetrics(
                        organizationId,
                        null
                );

        assertEquals(
                2L,
                response.getCriticalAlerts()
        );

        assertEquals(
                4L,
                response.getOpenAlerts()
        );
    }

    @Test
    void missingOrganizationShouldFailClosed() {

        assertThrows(
                IllegalArgumentException.class,
                () -> alertService.getDashboardMetrics(
                        null,
                        UUID.randomUUID()
                )
        );

        verifyNoInteractions(
                alertRepository
        );
    }
}
