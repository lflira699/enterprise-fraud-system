package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.CatalogItemRequest;
import com.efs.modules.catalog.dto.CatalogItemResponse;
import com.efs.modules.catalog.entity.CatalogItem;
import org.springframework.stereotype.Component;

@Component
public class CatalogItemMapper {

    public CatalogItem toEntity(
            CatalogItemRequest request) {

        CatalogItem entity =
                new CatalogItem();

        entity.setCatalogId(
                request.getCatalogId()
        );

        entity.setItemCode(
                request.getItemCode()
        );

        entity.setItemName(
                request.getItemName()
        );

        entity.setDisplayOrder(
                request.getDisplayOrder()
        );

        entity.setParentItemId(
                request.getParentItemId()
        );

        entity.setIsDefault(
                request.getIsDefault()
        );

        entity.setStatus(
                request.getStatus()
        );

        return entity;
    }

    public CatalogItemResponse toResponse(
            CatalogItem entity) {

        CatalogItemResponse response =
                new CatalogItemResponse();

        response.setCatalogItemId(
                entity.getCatalogItemId()
        );

        response.setCatalogId(
                entity.getCatalogId()
        );

        response.setItemCode(
                entity.getItemCode()
        );

        response.setItemName(
                entity.getItemName()
        );

        response.setDisplayOrder(
                entity.getDisplayOrder()
        );

        response.setParentItemId(
                entity.getParentItemId()
        );

        response.setIsDefault(
                entity.getIsDefault()
        );

        response.setStatus(
                entity.getStatus()
        );

        response.setCreatedAt(
                entity.getCreatedAt()
        );

        return response;
    }
}