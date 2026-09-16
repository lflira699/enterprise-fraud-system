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

        return toUuid(
                organizationId
        );
    }

    @Override
    @Transactional
    public void lockOrganization(
            UUID organizationId) {

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "organizationId is required"
            );
        }

        try {
            entityManager
                    .createNativeQuery("""
                            SELECT organization_id
                            FROM administration.organization
                            WHERE organization_id = :organizationId
                            FOR UPDATE
                            """)
                    .setParameter(
                            "organizationId",
                            organizationId
                    )
                    .getSingleResult();
        }
        catch (NoResultException exception) {
            throw new ResourceNotFoundException(
                    "Organization not found: "
                            + organizationId
            );
        }
    }

    @Override
    @Transactional
    public void lockTenant(
            UUID tenantId) {

        if (tenantId == null) {
            throw new IllegalArgumentException(
                    "tenantId is required"
            );
        }

        try {
            entityManager
                    .createNativeQuery("""
                            SELECT tenant_id
                            FROM administration.tenant
                            WHERE tenant_id = :tenantId
                            FOR UPDATE
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
    }

    private UUID toUuid(
            Object value) {

        if (value instanceof UUID uuid) {
            return uuid;
        }

        return UUID.fromString(
                value.toString()
        );
    }
}