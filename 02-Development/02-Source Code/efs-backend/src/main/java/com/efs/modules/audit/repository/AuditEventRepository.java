package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent> findByEventTypeOrderByEventTimestampDesc(
            String eventType
    );

    List<AuditEvent> findByEntityTypeAndEntityIdOrderByEventTimestampDesc(
            String entityType,
            UUID entityId
    );

    List<AuditEvent> findByUserIdOrderByEventTimestampDesc(
            UUID userId
    );

    List<AuditEvent> findByOrganizationIdOrderByEventTimestampDesc(
            UUID organizationId
    );

    List<AuditEvent> findByCorrelationIdOrderByEventTimestampDesc(
            UUID correlationId
    );
}