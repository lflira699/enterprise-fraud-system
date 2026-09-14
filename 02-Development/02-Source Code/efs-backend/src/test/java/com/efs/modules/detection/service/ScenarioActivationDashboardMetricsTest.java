package com.efs.modules.detection.service;

import com.efs.modules.detection.repository.ScenarioActivationRepository;
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
class ScenarioActivationDashboardMetricsTest {

    @Mock
    private ScenarioActivationRepository
            scenarioActivationRepository;

    @InjectMocks
    private ScenarioActivationService
            scenarioActivationService;

    @Test
    void tenantScopeShouldRemainTenantIsolated() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                scenarioActivationRepository
                        .countDistinctScenariosByOrganizationIdAndTenantId(
                                organizationId,
                                tenantId
                        )
        ).thenReturn(6L);

        assertEquals(
                6L,
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                tenantId
                        )
        );

        verify(
                scenarioActivationRepository,
                never()
        ).countDistinctScenariosByOrganizationIdAndTenantIdIsNull(
                organizationId
        );
    }

    @Test
    void organizationOnlyScopeShouldNotExpandAcrossTenants() {

        UUID organizationId = UUID.randomUUID();

        when(
                scenarioActivationRepository
                        .countDistinctScenariosByOrganizationIdAndTenantIdIsNull(
                                organizationId
                        )
        ).thenReturn(2L);

        assertEquals(
                2L,
                scenarioActivationService
                        .countActivatedDetectionScenarios(
                                organizationId,
                                null
                        )
        );
    }

    @Test
    void missingOrganizationShouldFailClosed() {

        assertThrows(
                IllegalArgumentException.class,
                () -> scenarioActivationService
                        .countActivatedDetectionScenarios(
                                null,
                                UUID.randomUUID()
                        )
        );

        verifyNoInteractions(
                scenarioActivationRepository
        );
    }
}
