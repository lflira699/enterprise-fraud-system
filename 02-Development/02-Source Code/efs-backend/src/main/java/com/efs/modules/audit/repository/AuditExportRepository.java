package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditExportRepository
        extends JpaRepository<AuditExport, UUID> {

    List<AuditExport> findByUserIdOrderByExportedAtDesc(
            UUID userId
    );

    List<AuditExport> findByOrganizationIdOrderByExportedAtDesc(
            UUID organizationId
    );

    List<AuditExport> findByExportTypeOrderByExportedAtDesc(
            String exportType
    );

    List<AuditExport> findByResourceTypeAndResourceIdOrderByExportedAtDesc(
            String resourceType,
            UUID resourceId
    );
}