package com.efs.e2e;

import com.efs.modules.alert.repository.AlertRepository;
import com.efs.modules.casemanagement.repository.CaseRepository;
import com.efs.modules.detection.repository.ScenarioActivationRepository;
import com.efs.modules.risk.repository.RiskAssessmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class OwnerDashboardMetricRepositoryQueryIntegrationTest {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private RiskAssessmentRepository
            riskAssessmentRepository;

    @Autowired
    private ScenarioActivationRepository
            scenarioActivationRepository;

    @Test
    void ownerMetricQueriesShouldExecuteWithStrictUnknownScope() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        assertEquals(
                0L,
                alertRepository
                        .countByOrganizationIdAndTenantIdAndPriority(
                                organizationId,
                                tenantId,
                                "CRITICAL"
                        )
        );

        assertEquals(
                0L,
                alertRepository
                        .countByOrganizationIdAndTenantIdAndStatusNot(
                                organizationId,
                                tenantId,
                                "CLOSED"
                        )
        );

        assertEquals(
                0L,
                alertRepository
                        .countByOrganizationIdAndTenantIdIsNullAndPriority(
                                organizationId,
                                "CRITICAL"
                        )
        );

        assertEquals(
                0L,
                alertRepository
                        .countByOrganizationIdAndTenantIdIsNullAndStatusNot(
                                organizationId,
                                "CLOSED"
                        )
        );

        assertEquals(
                0L,
                caseRepository
                        .countByOrganizationIdAndTenantIdAndCurrentStatusNot(
                                organizationId,
                                tenantId,
                                "CLOSED"
                        )
        );

        assertEquals(
                0L,
                caseRepository
                        .countByOrganizationIdAndTenantIdAndCurrentStatus(
                                organizationId,
                                tenantId,
                                "CLOSED"
                        )
        );

        assertEquals(
                0L,
                caseRepository
                        .countByOrganizationIdAndTenantIdIsNullAndCurrentStatusNot(
                                organizationId,
                                "CLOSED"
                        )
        );

        assertEquals(
                0L,
                caseRepository
                        .countByOrganizationIdAndTenantIdIsNullAndCurrentStatus(
                                organizationId,
                                "CLOSED"
                        )
        );

        assertNull(
                riskAssessmentRepository
                        .averageLatestRiskScoreByOrganizationIdAndTenantId(
                                organizationId,
                                tenantId
                        )
        );

        assertNull(
                riskAssessmentRepository
                        .averageLatestRiskScoreByOrganizationIdAndTenantIdIsNull(
                                organizationId
                        )
        );

        assertEquals(
                0L,
                scenarioActivationRepository
                        .countDistinctScenariosByOrganizationIdAndTenantId(
                                organizationId,
                                tenantId
                        )
        );

        assertEquals(
                0L,
                scenarioActivationRepository
                        .countDistinctScenariosByOrganizationIdAndTenantIdIsNull(
                                organizationId
                        )
        );
    }
}
