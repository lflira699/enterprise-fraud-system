package com.efs.modules.administration.service;

import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TenantOrganizationLookupService
        implements TenantOrganizationLookupServiceInterface {

    private final EntityManager entityManager;

    public TenantOrganizationLookupService(
            EntityManager entityManager) {

        this.entityManager = entityManager;
    }

    @Override
    public UUID getOrganizationIdByTenantId(
            UUID tenantId) {

        if (tenantId == null) {
            throw new IllegalArgumentException(
                    "tenantId is required"
            );
        }

        Object organizationId;

        try {
            organizationId =
                    entityManager
                            .createNativeQuery("""
                                    SELECT organization_id
                                    FROM administration.tenant
                                    WHERE tenant_id = :tenantId
                                    """)
                            .setParameter(
                                    "tenantId",
                                    tenantId
                            )
                            .getSingleResult();
        }
        catch (NoResultException exception) {
            throw new ResourceNotFoundException(
                    "Tenant not found: " + tenantId
            );
        }

        if (organizationId instanceof UUID uuid) {
            return uuid;
        }

        return UUID.fromString(
                organizationId.toString()
        );
    }
}
