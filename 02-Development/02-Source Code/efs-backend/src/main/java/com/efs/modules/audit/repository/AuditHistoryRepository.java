package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuditHistoryRepository
        extends JpaRepository<AuditHistory, UUID> {

    Optional<AuditHistory> findBySourceTableAndSourceRecordId(
            String sourceTable,
            UUID sourceRecordId
    );

    List<AuditHistory> findByOrganizationIdOrderByArchivedAtDesc(
            UUID organizationId
    );

    List<AuditHistory> findByTenantIdOrderByArchivedAtDesc(
            UUID tenantId
    );

    List<AuditHistory> findByCorrelationIdOrderByArchivedAtDesc(
            UUID correlationId
    );

    List<AuditHistory> findBySourceTableOrderByArchivedAtDesc(
            String sourceTable
    );
}