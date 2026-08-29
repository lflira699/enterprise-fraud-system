package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CatalogRepository
        extends JpaRepository<Catalog, UUID> {

    Optional<Catalog> findByCatalogCode(
            String catalogCode
    );

    List<Catalog> findByStatusOrderByCreatedAtDesc(
            String status
    );

    List<Catalog> findByOrganizationIdOrderByCreatedAtDesc(
            UUID organizationId
    );

    List<Catalog> findByTenantIdOrderByCreatedAtDesc(
            UUID tenantId
    );
}