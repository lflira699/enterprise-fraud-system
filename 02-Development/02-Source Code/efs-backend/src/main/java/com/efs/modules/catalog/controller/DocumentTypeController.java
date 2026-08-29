package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.DocumentTypeRequest;
import com.efs.modules.catalog.dto.DocumentTypeResponse;
import com.efs.modules.catalog.service.DocumentTypeServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/document-types")
public class DocumentTypeController {

    private final DocumentTypeServiceInterface documentTypeService;

    public DocumentTypeController(
            DocumentTypeServiceInterface documentTypeService) {

        this.documentTypeService =
                documentTypeService;
    }

    @PostMapping
    public ResponseEntity<DocumentTypeResponse> createDocumentType(
            @Valid @RequestBody DocumentTypeRequest request) {

        DocumentTypeResponse response =
                documentTypeService.createDocumentType(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{documentTypeId}")
    public ResponseEntity<DocumentTypeResponse> getDocumentTypeById(
            @PathVariable UUID documentTypeId) {

        return ResponseEntity.ok(
                documentTypeService.getDocumentTypeById(
                        documentTypeId
                )
        );
    }

    @GetMapping("/organization/{organizationId}/code/{documentTypeCode}")
    public ResponseEntity<DocumentTypeResponse>
            getDocumentTypeByOrganizationAndCode(
                    @PathVariable UUID organizationId,
                    @PathVariable String documentTypeCode) {

        return ResponseEntity.ok(
                documentTypeService
                        .getDocumentTypeByOrganizationAndCode(
                                organizationId,
                                documentTypeCode
                        )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<DocumentTypeResponse>>
            getDocumentTypesByOrganization(
                    @PathVariable UUID organizationId,
                    @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    documentTypeService
                            .getDocumentTypesByOrganizationAndStatus(
                                    organizationId,
                                    status
                            )
            );
        }

        return ResponseEntity.ok(
                documentTypeService
                        .getDocumentTypesByOrganization(
                                organizationId
                        )
        );
    }
}