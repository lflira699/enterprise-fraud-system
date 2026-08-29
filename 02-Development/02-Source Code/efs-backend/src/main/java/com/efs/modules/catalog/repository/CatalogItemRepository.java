package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CatalogItemRepository
        extends JpaRepository<CatalogItem, UUID> {

    List<CatalogItem> findByCatalogIdOrderByDisplayOrderAsc(
            UUID catalogId
    );

    Optional<CatalogItem> findByCatalogIdAndItemCode(
            UUID catalogId,
            String itemCode
    );

    List<CatalogItem> findByParentItemIdOrderByDisplayOrderAsc(
            UUID parentItemId
    );

    List<CatalogItem> findByCatalogIdAndStatusOrderByDisplayOrderAsc(
            UUID catalogId,
            String status
    );
}