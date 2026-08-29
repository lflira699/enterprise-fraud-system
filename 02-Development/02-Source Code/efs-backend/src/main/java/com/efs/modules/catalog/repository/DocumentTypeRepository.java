package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTypeRepository
        extends JpaRepository<DocumentType, UUID> {

    Optional<DocumentType> findByOrganizationIdAndDocumentTypeCode(
            UUID organizationId,
            String documentTypeCode
    );

    List<DocumentType> findByOrganizationIdOrderByDisplayOrderAsc(
            UUID organizationId
    );

    List<DocumentType> findByOrganizationIdAndStatusOrderByDisplayOrderAsc(
            UUID organizationId,
            String status
    );
}