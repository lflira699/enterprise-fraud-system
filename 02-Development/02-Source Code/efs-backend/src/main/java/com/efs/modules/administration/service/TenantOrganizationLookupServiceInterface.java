package com.efs.modules.administration.service;

import java.util.UUID;

public interface TenantOrganizationLookupServiceInterface {

    UUID getOrganizationIdByTenantId(
            UUID tenantId
    );
}
