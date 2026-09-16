package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.ConfigurationChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationChangeRequestRepository
        extends JpaRepository<
                ConfigurationChangeRequest,
                UUID> {

    Optional<ConfigurationChangeRequest>
    findByChangeRequestIdAndOrganizationId(
            UUID changeRequestId,
            UUID organizationId
    );

    Optional<ConfigurationChangeRequest>
    findByChangeRequestIdAndOrganizationIdAndTenantId(
            UUID changeRequestId,
            UUID organizationId,
            UUID tenantId
    );

    Optional<ConfigurationChangeRequest>
    findByChangeRequestIdAndOrganizationIdAndTenantIdIsNull(
            UUID changeRequestId,
            UUID organizationId
    );

    List<ConfigurationChangeRequest>
    findByOrganizationIdAndTenantIdOrderByRequestedAtDesc(
            UUID organizationId,
            UUID tenantId
    );

    List<ConfigurationChangeRequest>
    findByOrganizationIdAndTenantIdIsNullOrderByRequestedAtDesc(
            UUID organizationId
    );

    List<ConfigurationChangeRequest>
    findByOrganizationIdAndStatusOrderByRequestedAtDesc(
            UUID organizationId,
            String status
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    @Query(
            value = """
                    UPDATE administration.configuration_change_request
                    SET status = 'FAILED',
                        failure_reason = :failureReason,
                        updated_at = CURRENT_TIMESTAMP
                    WHERE change_request_id = :changeRequestId
                      AND organization_id = :organizationId
                      AND status = 'APPROVED'
                    """,
            nativeQuery = true
    )
    int markApprovedRequestFailedRequiresNew(
            @Param("changeRequestId")
            UUID changeRequestId,
            @Param("organizationId")
            UUID organizationId,
            @Param("failureReason")
            String failureReason
    );
}