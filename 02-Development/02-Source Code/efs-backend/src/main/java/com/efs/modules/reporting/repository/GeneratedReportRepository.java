package com.efs.modules.reporting.repository;

import com.efs.modules.reporting.entity.GeneratedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedReportRepository
        extends JpaRepository<GeneratedReport, UUID> {

    Optional<GeneratedReport>
    findByReportIdAndOrganizationId(
            UUID reportId,
            UUID organizationId
    );

    Optional<GeneratedReport>
    findByReportIdAndOrganizationIdAndTenantId(
            UUID reportId,
            UUID organizationId,
            UUID tenantId
    );
}
