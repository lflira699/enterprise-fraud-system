package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditEntityChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditEntityChangeRepository
        extends JpaRepository<AuditEntityChange, UUID> {

    List<AuditEntityChange> findByAuditEventIdOrderByChangedAtDesc(
            UUID auditEventId
    );

    List<AuditEntityChange> findByEntityTypeAndEntityIdOrderByChangedAtDesc(
            String entityType,
            UUID entityId
    );

    List<AuditEntityChange> findByOperationOrderByChangedAtDesc(
            String operation
    );
}