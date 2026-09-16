package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.ConfigurationChangeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationChangeItemRepository
        extends JpaRepository<
                ConfigurationChangeItem,
                UUID> {

    List<ConfigurationChangeItem>
    findByChangeRequestIdOrderByCreatedAtAsc(
            UUID changeRequestId
    );

    Optional<ConfigurationChangeItem>
    findByChangeRequestIdAndConfigurationKey(
            UUID changeRequestId,
            String configurationKey
    );

    boolean existsByChangeRequestIdAndConfigurationKey(
            UUID changeRequestId,
            String configurationKey
    );

    @Query(
            value = """
                    SELECT COALESCE(
                        MAX(item.version_number),
                        0
                    )
                    FROM administration.configuration_change_item item
                    JOIN administration.configuration_change_request request
                      ON request.change_request_id =
                         item.change_request_id
                    WHERE item.configuration_key =
                          :configurationKey
                      AND request.organization_id =
                          :organizationId
                      AND request.tenant_id IS NULL
                      AND request.status = 'APPLIED'
                    """,
            nativeQuery = true
    )
    Integer findMaxAppliedVersionForOrganization(
            @Param("configurationKey")
            String configurationKey,
            @Param("organizationId")
            UUID organizationId
    );

    @Query(
            value = """
                    SELECT COALESCE(
                        MAX(item.version_number),
                        0
                    )
                    FROM administration.configuration_change_item item
                    JOIN administration.configuration_change_request request
                      ON request.change_request_id =
                         item.change_request_id
                    WHERE item.configuration_key =
                          :configurationKey
                      AND request.organization_id =
                          :organizationId
                      AND request.tenant_id =
                          :tenantId
                      AND request.status = 'APPLIED'
                    """,
            nativeQuery = true
    )
    Integer findMaxAppliedVersionForTenant(
            @Param("configurationKey")
            String configurationKey,
            @Param("organizationId")
            UUID organizationId,
            @Param("tenantId")
            UUID tenantId
    );
}