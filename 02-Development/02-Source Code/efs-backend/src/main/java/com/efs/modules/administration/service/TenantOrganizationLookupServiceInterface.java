package com.efs.modules.administration.service;

import java.util.UUID;

public interface TenantOrganizationLookupServiceInterface {

    UUID getOrganizationIdByTenantId(
            UUID tenantId
    );

    void lockOrganization(
            UUID organizationId
    );

    void lockTenant(
            UUID tenantId
    );
}