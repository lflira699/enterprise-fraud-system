package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.DocumentTypeRequest;
import com.efs.modules.catalog.dto.DocumentTypeResponse;
import com.efs.modules.catalog.entity.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class DocumentTypeMapper {

    public DocumentType toEntity(
            DocumentTypeRequest request) {

        DocumentType documentType =
                new DocumentType();

        documentType.setOrganizationId(
                request.getOrganizationId()
        );

        documentType.setDocumentTypeCode(
                request.getDocumentTypeCode()
        );

        documentType.setDocumentTypeName(
                request.getDocumentTypeName()
        );

        documentType.setDescription(
                request.getDescription()
        );

        documentType.setDisplayOrder(
                request.getDisplayOrder()
        );

        documentType.setStatus(
                request.getStatus()
        );

        return documentType;
    }

    public DocumentTypeResponse toResponse(
            DocumentType documentType) {

        DocumentTypeResponse response =
                new DocumentTypeResponse();

        response.setDocumentTypeId(
                documentType.getDocumentTypeId()
        );

        response.setOrganizationId(
                documentType.getOrganizationId()
        );

        response.setDocumentTypeCode(
                documentType.getDocumentTypeCode()
        );

        response.setDocumentTypeName(
                documentType.getDocumentTypeName()
        );

        response.setDescription(
                documentType.getDescription()
        );

        response.setDisplayOrder(
                documentType.getDisplayOrder()
        );

        response.setStatus(
                documentType.getStatus()
        );

        response.setCreatedAt(
                documentType.getCreatedAt()
        );

        return response;
    }
}