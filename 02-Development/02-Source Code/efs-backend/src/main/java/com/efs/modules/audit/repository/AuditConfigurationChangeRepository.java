package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditConfigurationChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditConfigurationChangeRepository
        extends JpaRepository<AuditConfigurationChange, UUID> {

    List<AuditConfigurationChange> findByAuditEventIdOrderByChangedAtDesc(
            UUID auditEventId
    );

    List<AuditConfigurationChange> findByConfigurationKeyOrderByChangedAtDesc(
            String configurationKey
    );

    List<AuditConfigurationChange> findByChangedByOrderByChangedAtDesc(
            UUID changedBy
    );
}