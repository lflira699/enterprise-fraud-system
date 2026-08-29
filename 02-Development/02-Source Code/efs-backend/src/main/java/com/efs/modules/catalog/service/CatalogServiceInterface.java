package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CatalogRequest;
import com.efs.modules.catalog.dto.CatalogResponse;

import java.util.List;
import java.util.UUID;

public interface CatalogServiceInterface {

    CatalogResponse createCatalog(
            CatalogRequest request
    );

    CatalogResponse getCatalogById(
            UUID catalogId
    );

    CatalogResponse getCatalogByCode(
            String catalogCode
    );

    List<CatalogResponse> getCatalogsByStatus(
            String status
    );

    List<CatalogResponse> getCatalogsByOrganizationId(
            UUID organizationId
    );

    List<CatalogResponse> getCatalogsByTenantId(
            UUID tenantId
    );
}