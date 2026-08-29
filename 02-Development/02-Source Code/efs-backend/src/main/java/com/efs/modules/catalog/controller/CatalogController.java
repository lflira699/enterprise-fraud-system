package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.CatalogRequest;
import com.efs.modules.catalog.dto.CatalogResponse;
import com.efs.modules.catalog.service.CatalogServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalogs")
public class CatalogController {

    private final CatalogServiceInterface catalogService;

    public CatalogController(
            CatalogServiceInterface catalogService) {

        this.catalogService =
                catalogService;
    }

    @PostMapping
    public ResponseEntity<CatalogResponse> createCatalog(
            @Valid @RequestBody CatalogRequest request) {

        CatalogResponse response =
                catalogService.createCatalog(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{catalogId}")
    public ResponseEntity<CatalogResponse> getCatalogById(
            @PathVariable UUID catalogId) {

        return ResponseEntity.ok(
                catalogService.getCatalogById(
                        catalogId
                )
        );
    }

    @GetMapping("/code/{catalogCode}")
    public ResponseEntity<CatalogResponse> getCatalogByCode(
            @PathVariable String catalogCode) {

        return ResponseEntity.ok(
                catalogService.getCatalogByCode(
                        catalogCode
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CatalogResponse>> getCatalogsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                catalogService.getCatalogsByStatus(
                        status
                )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<CatalogResponse>>
    getCatalogsByOrganizationId(
            @PathVariable UUID organizationId) {

        return ResponseEntity.ok(
                catalogService.getCatalogsByOrganizationId(
                        organizationId
                )
        );
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<CatalogResponse>>
    getCatalogsByTenantId(
            @PathVariable UUID tenantId) {

        return ResponseEntity.ok(
                catalogService.getCatalogsByTenantId(
                        tenantId
                )
        );
    }
}