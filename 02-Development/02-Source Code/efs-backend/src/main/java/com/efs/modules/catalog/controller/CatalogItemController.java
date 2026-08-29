package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.CatalogItemRequest;
import com.efs.modules.catalog.dto.CatalogItemResponse;
import com.efs.modules.catalog.service.CatalogItemServiceInterface;
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
@RequestMapping("/api/v1/catalog-items")
public class CatalogItemController {

    private final CatalogItemServiceInterface catalogItemService;

    public CatalogItemController(
            CatalogItemServiceInterface catalogItemService) {

        this.catalogItemService =
                catalogItemService;
    }

    @PostMapping
    public ResponseEntity<CatalogItemResponse> createCatalogItem(
            @Valid @RequestBody CatalogItemRequest request) {

        CatalogItemResponse response =
                catalogItemService.createCatalogItem(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{catalogItemId}")
    public ResponseEntity<CatalogItemResponse> getCatalogItemById(
            @PathVariable UUID catalogItemId) {

        return ResponseEntity.ok(
                catalogItemService.getCatalogItemById(
                        catalogItemId
                )
        );
    }

    @GetMapping("/catalog/{catalogId}")
    public ResponseEntity<List<CatalogItemResponse>>
    getCatalogItemsByCatalogId(
            @PathVariable UUID catalogId) {

        return ResponseEntity.ok(
                catalogItemService.getCatalogItemsByCatalogId(
                        catalogId
                )
        );
    }

    @GetMapping("/catalog/{catalogId}/code/{itemCode}")
    public ResponseEntity<CatalogItemResponse>
    getCatalogItemByCatalogAndCode(
            @PathVariable UUID catalogId,
            @PathVariable String itemCode) {

        return ResponseEntity.ok(
                catalogItemService.getCatalogItemByCatalogAndCode(
                        catalogId,
                        itemCode
                )
        );
    }

    @GetMapping("/parent/{parentItemId}")
    public ResponseEntity<List<CatalogItemResponse>>
    getCatalogItemsByParentItemId(
            @PathVariable UUID parentItemId) {

        return ResponseEntity.ok(
                catalogItemService.getCatalogItemsByParentItemId(
                        parentItemId
                )
        );
    }

    @GetMapping("/catalog/{catalogId}/status")
    public ResponseEntity<List<CatalogItemResponse>>
    getCatalogItemsByCatalogIdAndStatus(
            @PathVariable UUID catalogId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                catalogItemService.getCatalogItemsByCatalogIdAndStatus(
                        catalogId,
                        status
                )
        );
    }
}