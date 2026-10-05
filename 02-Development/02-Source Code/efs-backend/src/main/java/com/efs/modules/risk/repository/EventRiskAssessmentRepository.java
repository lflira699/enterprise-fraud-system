package com.efs.modules.risk.repository;

import com.efs.modules.risk.entity.EventRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRiskAssessmentRepository
        extends JpaRepository<EventRiskAssessment, UUID> {

    Optional<EventRiskAssessment>
    findByEventRiskAssessmentIdAndOrganizationIdAndTenantId(
            UUID eventRiskAssessmentId,
            UUID organizationId,
            UUID tenantId
    );

    List<EventRiskAssessment>
    findByFraudEventIdAndOrganizationIdAndTenantIdOrderByAssessmentTimestampDesc(
            UUID fraudEventId,
            UUID organizationId,
            UUID tenantId
    );

    List<EventRiskAssessment>
    findByFraudEventIdAndOrganizationIdAndTenantIdAndCorrelationIdAndModelIdAndModelVersionOrderByAssessmentTimestampDesc(
            UUID fraudEventId,
            UUID organizationId,
            UUID tenantId,
            UUID correlationId,
            String modelId,
            String modelVersion
    );
}
