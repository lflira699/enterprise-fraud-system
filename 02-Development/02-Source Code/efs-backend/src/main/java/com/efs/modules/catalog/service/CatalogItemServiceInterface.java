package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CatalogItemRequest;
import com.efs.modules.catalog.dto.CatalogItemResponse;

import java.util.List;
import java.util.UUID;

public interface CatalogItemServiceInterface {

    CatalogItemResponse createCatalogItem(
            CatalogItemRequest request
    );

    CatalogItemResponse getCatalogItemById(
            UUID catalogItemId
    );

    CatalogItemResponse getCatalogItemByCatalogAndCode(
            UUID catalogId,
            String itemCode
    );

    List<CatalogItemResponse> getCatalogItemsByCatalogId(
            UUID catalogId
    );

    List<CatalogItemResponse> getCatalogItemsByParentItemId(
            UUID parentItemId
    );

    List<CatalogItemResponse> getCatalogItemsByCatalogIdAndStatus(
            UUID catalogId,
            String status
    );
}