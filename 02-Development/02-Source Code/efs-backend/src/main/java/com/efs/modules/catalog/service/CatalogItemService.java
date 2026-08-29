package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CatalogItemRequest;
import com.efs.modules.catalog.dto.CatalogItemResponse;
import com.efs.modules.catalog.entity.CatalogItem;
import com.efs.modules.catalog.mapper.CatalogItemMapper;
import com.efs.modules.catalog.repository.CatalogItemRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CatalogItemService
        implements CatalogItemServiceInterface {

    private final CatalogItemRepository catalogItemRepository;
    private final CatalogItemMapper catalogItemMapper;

    public CatalogItemService(
            CatalogItemRepository catalogItemRepository,
            CatalogItemMapper catalogItemMapper) {

        this.catalogItemRepository =
                catalogItemRepository;

        this.catalogItemMapper =
                catalogItemMapper;
    }

    @Override
    public CatalogItemResponse createCatalogItem(
            CatalogItemRequest request) {

        CatalogItem catalogItem =
                catalogItemMapper.toEntity(
                        request
                );

        CatalogItem savedCatalogItem =
                catalogItemRepository.save(
                        catalogItem
                );

        return catalogItemMapper.toResponse(
                savedCatalogItem
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogItemResponse getCatalogItemById(
            UUID catalogItemId) {

        CatalogItem catalogItem =
                catalogItemRepository
                        .findById(catalogItemId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Catalog item not found: "
                                                        + catalogItemId
                                        )
                        );

        return catalogItemMapper.toResponse(
                catalogItem
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogItemResponse getCatalogItemByCatalogAndCode(
            UUID catalogId,
            String itemCode) {

        CatalogItem catalogItem =
                catalogItemRepository
                        .findByCatalogIdAndItemCode(
                                catalogId,
                                itemCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Catalog item not found: "
                                                        + catalogId
                                                        + " / "
                                                        + itemCode
                                        )
                        );

        return catalogItemMapper.toResponse(
                catalogItem
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogItemResponse> getCatalogItemsByCatalogId(
            UUID catalogId) {

        return catalogItemRepository
                .findByCatalogIdOrderByDisplayOrderAsc(
                        catalogId
                )
                .stream()
                .map(
                        catalogItemMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogItemResponse> getCatalogItemsByParentItemId(
            UUID parentItemId) {

        return catalogItemRepository
                .findByParentItemIdOrderByDisplayOrderAsc(
                        parentItemId
                )
                .stream()
                .map(
                        catalogItemMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogItemResponse>
    getCatalogItemsByCatalogIdAndStatus(
            UUID catalogId,
            String status) {

        return catalogItemRepository
                .findByCatalogIdAndStatusOrderByDisplayOrderAsc(
                        catalogId,
                        status
                )
                .stream()
                .map(
                        catalogItemMapper::toResponse
                )
                .toList();
    }
}