package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditExportRequest;
import com.efs.modules.audit.dto.AuditExportResponse;
import com.efs.modules.audit.service.AuditExportServiceInterface;
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
@RequestMapping("/api/v1/audit/exports")
public class AuditExportController {

    private final AuditExportServiceInterface auditExportService;

    public AuditExportController(
            AuditExportServiceInterface auditExportService) {

        this.auditExportService =
                auditExportService;
    }

    @PostMapping
    public ResponseEntity<AuditExportResponse>
    createAuditExport(
            @Valid @RequestBody AuditExportRequest request) {

        AuditExportResponse response =
                auditExportService.createAuditExport(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{exportId}")
    public ResponseEntity<AuditExportResponse>
    getAuditExportById(
            @PathVariable UUID exportId) {

        return ResponseEntity.ok(
                auditExportService.getAuditExportById(
                        exportId
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditExportResponse>>
    getAuditExportsByUserId(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                auditExportService.getAuditExportsByUserId(
                        userId
                )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<AuditExportResponse>>
    getAuditExportsByOrganizationId(
            @PathVariable UUID organizationId) {

        return ResponseEntity.ok(
                auditExportService.getAuditExportsByOrganizationId(
                        organizationId
                )
        );
    }

    @GetMapping("/type/{exportType}")
    public ResponseEntity<List<AuditExportResponse>>
    getAuditExportsByExportType(
            @PathVariable String exportType) {

        return ResponseEntity.ok(
                auditExportService.getAuditExportsByExportType(
                        exportType
                )
        );
    }

    @GetMapping("/resource")
    public ResponseEntity<List<AuditExportResponse>>
    getAuditExportsByResource(
            @RequestParam String resourceType,
            @RequestParam UUID resourceId) {

        return ResponseEntity.ok(
                auditExportService.getAuditExportsByResource(
                        resourceType,
                        resourceId
                )
        );
    }
}