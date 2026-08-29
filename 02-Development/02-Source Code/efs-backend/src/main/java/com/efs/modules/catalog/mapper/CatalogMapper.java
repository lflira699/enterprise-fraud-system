package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.CatalogRequest;
import com.efs.modules.catalog.dto.CatalogResponse;
import com.efs.modules.catalog.entity.Catalog;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    public Catalog toEntity(
            CatalogRequest request) {

        Catalog catalog =
                new Catalog();

        catalog.setCatalogCode(
                request.getCatalogCode()
        );

        catalog.setCatalogName(
                request.getCatalogName()
        );

        catalog.setDescription(
                request.getDescription()
        );

        catalog.setOrganizationId(
                request.getOrganizationId()
        );

        catalog.setTenantId(
                request.getTenantId()
        );

        catalog.setStatus(
                request.getStatus()
        );

        return catalog;
    }

    public CatalogResponse toResponse(
            Catalog catalog) {

        CatalogResponse response =
                new CatalogResponse();

        response.setCatalogId(
                catalog.getCatalogId()
        );

        response.setCatalogCode(
                catalog.getCatalogCode()
        );

        response.setCatalogName(
                catalog.getCatalogName()
        );

        response.setDescription(
                catalog.getDescription()
        );

        response.setOrganizationId(
                catalog.getOrganizationId()
        );

        response.setTenantId(
                catalog.getTenantId()
        );

        response.setStatus(
                catalog.getStatus()
        );

        response.setCreatedAt(
                catalog.getCreatedAt()
        );

        return response;
    }
}