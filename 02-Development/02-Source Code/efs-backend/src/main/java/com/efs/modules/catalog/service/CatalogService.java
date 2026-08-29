package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CatalogRequest;
import com.efs.modules.catalog.dto.CatalogResponse;
import com.efs.modules.catalog.entity.Catalog;
import com.efs.modules.catalog.mapper.CatalogMapper;
import com.efs.modules.catalog.repository.CatalogRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CatalogService
        implements CatalogServiceInterface {

    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;

    public CatalogService(
            CatalogRepository catalogRepository,
            CatalogMapper catalogMapper) {

        this.catalogRepository =
                catalogRepository;

        this.catalogMapper =
                catalogMapper;
    }

    @Override
    public CatalogResponse createCatalog(
            CatalogRequest request) {

        Catalog catalog =
                catalogMapper.toEntity(
                        request
                );

        Catalog savedCatalog =
                catalogRepository.save(
                        catalog
                );

        return catalogMapper.toResponse(
                savedCatalog
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogResponse getCatalogById(
            UUID catalogId) {

        Catalog catalog =
                catalogRepository.findById(
                                catalogId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Catalog not found: "
                                                        + catalogId
                                        )
                        );

        return catalogMapper.toResponse(
                catalog
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogResponse getCatalogByCode(
            String catalogCode) {

        Catalog catalog =
                catalogRepository.findByCatalogCode(
                                catalogCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Catalog not found: "
                                                        + catalogCode
                                        )
                        );

        return catalogMapper.toResponse(
                catalog
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getCatalogsByStatus(
            String status) {

        return catalogRepository
                .findByStatusOrderByCreatedAtDesc(
                        status
                )
                .stream()
                .map(
                        catalogMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getCatalogsByOrganizationId(
            UUID organizationId) {

        return catalogRepository
                .findByOrganizationIdOrderByCreatedAtDesc(
                        organizationId
                )
                .stream()
                .map(
                        catalogMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getCatalogsByTenantId(
            UUID tenantId) {

        return catalogRepository
                .findByTenantIdOrderByCreatedAtDesc(
                        tenantId
                )
                .stream()
                .map(
                        catalogMapper::toResponse
                )
                .toList();
    }
}