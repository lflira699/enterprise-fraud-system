package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.DocumentTypeRequest;
import com.efs.modules.catalog.dto.DocumentTypeResponse;
import com.efs.modules.catalog.entity.DocumentType;
import com.efs.modules.catalog.mapper.DocumentTypeMapper;
import com.efs.modules.catalog.repository.DocumentTypeRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DocumentTypeService
        implements DocumentTypeServiceInterface {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;

    public DocumentTypeService(
            DocumentTypeRepository documentTypeRepository,
            DocumentTypeMapper documentTypeMapper) {

        this.documentTypeRepository =
                documentTypeRepository;

        this.documentTypeMapper =
                documentTypeMapper;
    }

    @Override
    public DocumentTypeResponse createDocumentType(
            DocumentTypeRequest request) {

        DocumentType documentType =
                documentTypeMapper.toEntity(
                        request
                );

        DocumentType savedDocumentType =
                documentTypeRepository.save(
                        documentType
                );

        return documentTypeMapper.toResponse(
                savedDocumentType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTypeResponse getDocumentTypeById(
            UUID documentTypeId) {

        DocumentType documentType =
                documentTypeRepository
                        .findById(documentTypeId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Document type not found: "
                                                        + documentTypeId
                                        )
                        );

        return documentTypeMapper.toResponse(
                documentType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTypeResponse getDocumentTypeByOrganizationAndCode(
            UUID organizationId,
            String documentTypeCode) {

        DocumentType documentType =
                documentTypeRepository
                        .findByOrganizationIdAndDocumentTypeCode(
                                organizationId,
                                documentTypeCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Document type not found for organization "
                                                        + organizationId
                                                        + " and code: "
                                                        + documentTypeCode
                                        )
                        );

        return documentTypeMapper.toResponse(
                documentType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentTypeResponse> getDocumentTypesByOrganization(
            UUID organizationId) {

        return documentTypeRepository
                .findByOrganizationIdOrderByDisplayOrderAsc(
                        organizationId
                )
                .stream()
                .map(documentTypeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentTypeResponse> getDocumentTypesByOrganizationAndStatus(
            UUID organizationId,
            String status) {

        return documentTypeRepository
                .findByOrganizationIdAndStatusOrderByDisplayOrderAsc(
                        organizationId,
                        status
                )
                .stream()
                .map(documentTypeMapper::toResponse)
                .toList();
    }
}