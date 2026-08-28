package com.efs.modules.audit.repository;

import com.efs.modules.audit.entity.AuditApiAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditApiAccessRepository
        extends JpaRepository<AuditApiAccess, UUID> {

    List<AuditApiAccess> findByApiClientIdOrderByRequestedAtDesc(
            UUID apiClientId
    );

    List<AuditApiAccess> findByEndpointOrderByRequestedAtDesc(
            String endpoint
    );

    List<AuditApiAccess> findByResponseCodeOrderByRequestedAtDesc(
            Integer responseCode
    );

    List<AuditApiAccess> findByCorrelationIdOrderByRequestedAtDesc(
            UUID correlationId
    );
}