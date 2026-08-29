package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.DocumentTypeRequest;
import com.efs.modules.catalog.dto.DocumentTypeResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentTypeServiceInterface {

    DocumentTypeResponse createDocumentType(
            DocumentTypeRequest request
    );

    DocumentTypeResponse getDocumentTypeById(
            UUID documentTypeId
    );

    DocumentTypeResponse getDocumentTypeByOrganizationAndCode(
            UUID organizationId,
            String documentTypeCode
    );

    List<DocumentTypeResponse> getDocumentTypesByOrganization(
            UUID organizationId
    );

    List<DocumentTypeResponse> getDocumentTypesByOrganizationAndStatus(
            UUID organizationId,
            String status
    );
}