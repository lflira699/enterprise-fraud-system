package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditSecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditSecurityEventRepository
        extends JpaRepository<AuditSecurityEvent, UUID> {

    List<AuditSecurityEvent> findByUserIdOrderByDetectedAtDesc(
            UUID userId
    );

    List<AuditSecurityEvent> findByOrganizationIdOrderByDetectedAtDesc(
            UUID organizationId
    );

    List<AuditSecurityEvent> findByAuditEventIdOrderByDetectedAtDesc(
            UUID auditEventId
    );

    List<AuditSecurityEvent> findBySeverityOrderByDetectedAtDesc(
            String severity
    );

    List<AuditSecurityEvent> findByEventCategoryOrderByDetectedAtDesc(
            String eventCategory
    );
}