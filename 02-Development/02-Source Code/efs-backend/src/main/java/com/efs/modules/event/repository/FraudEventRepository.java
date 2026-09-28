package com.efs.modules.event.repository;

import com.efs.modules.event.entity.FraudEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface FraudEventRepository
        extends JpaRepository<FraudEvent, UUID>,
                JpaSpecificationExecutor<FraudEvent> {

    boolean existsByOrganizationIdAndTenantIdAndIdempotencyKey(
            UUID organizationId,
            UUID tenantId,
            String idempotencyKey);

    Optional<FraudEvent> findByFraudEventIdAndOrganizationIdAndTenantId(
            UUID fraudEventId,
            UUID organizationId,
            UUID tenantId);
}
